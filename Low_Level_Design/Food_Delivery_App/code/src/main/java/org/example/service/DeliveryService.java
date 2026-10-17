package org.example.service;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.model.user.DeliveryAgent;
import org.example.repository.DeliveryRepository;
import org.example.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository,  NotificationService notificationService){
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    };

    // Try to assign immediately; if not possible, queue by pincode
    public boolean assignOrQueue(Order order, int pincode) {
        if (assignOrder(order, order.getRestaurant().getPincode())) {
            return true;
        }
        // If assignment failed, keep it pending (status remains ACCEPTED)
        int pin = order.getRestaurant().getPincode();
        deliveryRepository.enqueuePending(pin, order.getOrderId());
        System.out.println("⏳ Queued order " + order.getOrderId() + " for pincode " + pin + " (no agent available)");
        return false;
    }

    public boolean assignOrder(Order order, int pincode){
        List<String> agentIds = deliveryRepository.findAgentIdsByPincode(pincode);
        if (agentIds.isEmpty()) {
            System.out.println("🚫 No delivery agents registered for pincode " + pincode);
            return false;
        };
        // Step 2: Sort agents by current active order count (least loaded first)
        List<String> sorted = new ArrayList<>(agentIds);
        Collections.sort(sorted, Comparator.comparingInt(deliveryRepository::getActiveCount));
        // Step 3: Try to reserve a slot on each agent in that order
        String chosenId = null;
        for (String id : sorted) {
            boolean reserved = deliveryRepository.tryReserveSlot(id); // atomic check + increment
            if (reserved) {
                chosenId = id;
                break; // stop at the first agent we successfully reserve
            }
        };
        // Step 4: If none available, bail out
        if (chosenId == null) {
            System.out.println("🚫 All delivery agents are at capacity for pincode " + pincode);
            return false;
        };
        // Step 5: Assign and persist
        order.setAssignedAgentId(chosenId);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);
        deliveryRepository.addOrderToAgent(chosenId, order.getOrderId());
        // Optional: print agent name if you want
        DeliveryAgent agent = deliveryRepository.findById(chosenId);
//        int agentCapacity = deliveryRepository.getCapacity(chosenId);
//        System.out.println("Agent Remaining Capacity "+ agentCapacity);
        String agentName = (agent != null) ? agent.getUserName() : chosenId;
        System.out.println("🚴 Assigned order " + order.getOrderId() + " to agent " + agentName);
        // Step 6: Notify customer
        notificationService.notifyCustomer(order);
        return true;
    };

    public boolean markOutForDelivery(String orderId){
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getAssignedAgentId() == null) return false;
        if (order.getStatus() != OrderStatus.ASSIGNED) return false;
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
        notificationService.notifyCustomer(order);
        return true;
    };

    public boolean markDelivered(String orderId){
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getAssignedAgentId() == null) return false;
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) return false;
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        deliveryRepository.releaseSlot(order.getAssignedAgentId());
        notificationService.notifyCustomer(order);
        System.out.println("✅ Delivered order " + order.getOrderId());

        // After freeing capacity, try to process pending queue for the same pincode
        int pin = order.getRestaurant().getPincode();
        processPendingForPincode(pin);
        return true;
    };

    // Use this when an ASSIGNED/OUT_FOR_DELIVERY order is canceled
    public void releaseAgentIfAssigned(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) return;
        String agentId = order.getAssignedAgentId();
        if (agentId == null) return;

        // Release capacity only; do not remove from history
        deliveryRepository.releaseSlot(agentId);

        // Also attempt to process pending queue for this pincode
        int pin = order.getRestaurant().getPincode();
        processPendingForPincode(pin);
    }

    // Drain pending queue for a pincode while there is free capacity
    public void processPendingForPincode(int pincode) {
        while (true) {
            String nextOrderId = deliveryRepository.pollPending(pincode);
            if (nextOrderId == null) return;

            Order next = orderRepository.findById(nextOrderId);
            if (next == null) continue;
            if (next.getStatus() != OrderStatus.ACCEPTED) continue; // only assign accepted ones

            boolean assigned = assignOrder(next, pincode);
            if (!assigned) {
                // Put it back at the end and stop to avoid busy loop
                deliveryRepository.enqueuePending(pincode, nextOrderId);
                return;
            }

            // Optionally auto-advance: out-for-delivery -> delivered
            // markOutForDelivery(next.getOrderId());
            // markDelivered(next.getOrderId());
        }
    }
}
