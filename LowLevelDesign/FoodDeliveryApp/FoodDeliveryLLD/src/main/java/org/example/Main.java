package org.example;

import org.example.model.food.FoodItem;
import org.example.model.food.FoodType;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.model.restaurant.Restaurant;
import org.example.model.user.DeliveryAgent;
import org.example.model.user.GenderType;
import org.example.model.user.User;
import org.example.model.user.UserType;
import org.example.repository.CustomerRepository;
import org.example.repository.DeliveryRepository;
import org.example.repository.OrderRepository;
import org.example.repository.RestaurantRepository;
import org.example.repository.implementations.CustomerRepositoryImpl;
import org.example.repository.implementations.DeliveryRepositoryImpl;
import org.example.repository.implementations.OrderRepositoryImpl;
import org.example.repository.implementations.RestaurantRepositoryImpl;
import org.example.service.CustomerService;
import org.example.service.DeliveryService;
import org.example.service.NotificationService;
import org.example.service.OrderService;
import org.example.service.RestaurantService;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create owners and customer
        User owner1 = new User(UserType.RESTAURANT_OWNER, "Zeeshan Ali", "zeeshan@example.com", "Bengaluru", GenderType.MALE);
        User owner2 = new User(UserType.RESTAURANT_OWNER, "Fatima Khan", "fatima@example.com", "Bengaluru", GenderType.FEMALE);
        User customer1 = new User(UserType.CUSTOMER_USER, "Fatima Alam", "fatima.alam@example.com", "Bengaluru", GenderType.FEMALE);

        // Restaurants
        Restaurant spiceGarden = new Restaurant("Spice Garden", "MG Road", owner1, 560001);
        spiceGarden.addServiceablePincode(560001);
        spiceGarden.addServiceablePincode(560002);

        Restaurant biryaniHub = new Restaurant("Biryani Hub", "Koramangala", owner2, 560034);
        biryaniHub.addServiceablePincode(560034);
        biryaniHub.addServiceablePincode(560035);

        // Menus
        FoodItem biryani = new FoodItem("Chicken Biryani", 220.0, FoodType.NON_VEGETARIAN, "Aromatic basmati rice with chicken");
        FoodItem paneerTikka = new FoodItem("Paneer Tikka", 180.0, FoodType.VEGETARIAN, "Grilled paneer with smoky flavor");
        spiceGarden.addFoodItem(biryani);
        spiceGarden.addFoodItem(paneerTikka);

        FoodItem hyderabadiBiryani = new FoodItem("Hyderabadi Biryani", 250.0, FoodType.NON_VEGETARIAN, "Spicy & authentic biryani");
        FoodItem gulabJamun = new FoodItem("Gulab Jamun", 80.0, FoodType.VEGETARIAN, "Sweet dessert");
        biryaniHub.addFoodItem(hyderabadiBiryani);
        biryaniHub.addFoodItem(gulabJamun);

        // Show info
        displayRestaurantInfo(spiceGarden);
        displayRestaurantInfo(biryaniHub);

        // Repositories
        OrderRepository orderRepo = new OrderRepositoryImpl();
        RestaurantRepository restaurantRepo = new RestaurantRepositoryImpl();
        CustomerRepository customerRepo = new CustomerRepositoryImpl();
        DeliveryRepository deliveryRepo = new DeliveryRepositoryImpl();

        // Services
        CustomerService customerService = new CustomerService(customerRepo);
        NotificationService notificationService = new NotificationService();
        DeliveryService deliveryService = new DeliveryService(deliveryRepo, orderRepo, notificationService);
        RestaurantService restaurantService = new RestaurantService(restaurantRepo, orderRepo, notificationService);
        OrderService orderService = new OrderService(orderRepo, restaurantService, customerService);

        // Delivery agents and coverage
        DeliveryAgent agentA = new DeliveryAgent("Heena", "Heena@example.com", "Bengaluru", GenderType.FEMALE);
        DeliveryAgent agentB = new DeliveryAgent("Shabbir", "Shabbir@example.com", "Bengaluru", GenderType.MALE);
        deliveryRepo.registerAgent(agentA);
        deliveryRepo.registerAgent(agentB);
        deliveryRepo.addCoverage(agentA.getAgentId(), 560034);
        deliveryRepo.addCoverage(agentA.getAgentId(), 560001);
        deliveryRepo.addCoverage(agentB.getAgentId(), 560034);
        deliveryRepo.addCoverage(agentB.getAgentId(), 560001);

        System.out.println("\n================== ORDER FLOW START (1) ==================\n");
        // Order 1: accept -> assign -> OFD -> delivered
        Order order1 = orderService.createOrder(customer1, biryaniHub, Arrays.asList(hyderabadiBiryani, gulabJamun));
        if (order1.getStatus() == OrderStatus.ACCEPTED) {
            deliveryService.assignOrQueue(order1, order1.getRestaurant().getPincode());
        }
        if (order1.getStatus() == OrderStatus.ASSIGNED) {
            deliveryService.markOutForDelivery(order1.getOrderId());
        }
        if (order1.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            deliveryService.markDelivered(order1.getOrderId());
        }
        System.out.println("Order 1: " + order1.getOrderId() + " → " + order1.getStatus());

        System.out.println("\n================== ORDER FLOW START (2) ==================\n");
        // Order 2: accept/assign, then cancel but failed to cancel & proceed with delivery
        Order order2 = orderService.createOrder(customer1, spiceGarden, Arrays.asList(biryani, paneerTikka));
        if (order2.getStatus() == OrderStatus.ACCEPTED) {
            deliveryService.assignOrQueue(order2, order2.getRestaurant().getPincode());
        }
        if (order2.getStatus() == OrderStatus.ACCEPTED || order2.getStatus() == OrderStatus.ASSIGNED) {
            boolean canceled = restaurantService.cancelByCustomer(order2.getOrderId());
            if (canceled) {
                deliveryService.releaseAgentIfAssigned(order2.getOrderId());
                System.out.println("Order 2: " + order2.getOrderId() + " → " + order2.getStatus());
            } else {
                // proceed to deliver if not canceled
                if (order2.getStatus() == OrderStatus.ASSIGNED) {
                    deliveryService.markOutForDelivery(order2.getOrderId());
                }
                if (order2.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                    deliveryService.markDelivered(order2.getOrderId());
                }
                System.out.println("Order 2: " + order2.getOrderId() + " → " + order2.getStatus());
            }
        }

        System.out.println("\n================== ORDER FLOW START (3) ==================\n");
        // Order 3: accept then cancel (capacity released)
        Order order3 = orderService.createOrder(customer1, spiceGarden, Arrays.asList(biryani, paneerTikka));
        if (order3.getStatus() == OrderStatus.ACCEPTED || order3.getStatus() == OrderStatus.ASSIGNED) {
            boolean canceled = restaurantService.cancelByCustomer(order3.getOrderId());
            if (canceled) {
                deliveryService.releaseAgentIfAssigned(order3.getOrderId());
                System.out.println("Order 3: " + order3.getOrderId() + " → " + order3.getStatus());
            }
        };

        System.out.println("\n================== 3 - ORDER STATUS  ==================\n");
        System.out.println("Order 1: " + order1.getOrderId() + " → " + order1.getStatus()
                + (order1.getAssignedAgentId() != null ? " (agent=" + order1.getAssignedAgentId() + ")" : ""));
        System.out.println("Order 2: " + order2.getOrderId() + " → " + order2.getStatus()
                + (order2.getAssignedAgentId() != null ? " (agent=" + order2.getAssignedAgentId() + ")" : ""));
        System.out.println("Order 3: " + order3.getOrderId() + " → " + order3.getStatus()
                + (order3.getAssignedAgentId() != null ? " (agent=" + order3.getAssignedAgentId() + ")" : ""));

        System.out.println("\n================== ORDER FLOW: 8 back-to-back ==================\n");

        // Place 8 orders back-to-back at Spice Garden (pincode 560001)

        Order[] burst = new Order[8];
        for (int i = 0; i < burst.length; i++) {
            burst[i] = orderService.createOrder(customer1, spiceGarden, Arrays.asList(biryani, paneerTikka));

            // Try to assign delivery agent if accepted
            if (burst[i].getStatus() == OrderStatus.ACCEPTED) {
                deliveryService.assignOrQueue(burst[i], burst[i].getRestaurant().getPincode());
            };

            // Print status for each order
            System.out.println("Order " + (i + 1) + ": " + burst[i].getOrderId() + " → " + burst[i].getStatus()
                    + (burst[i].getAssignedAgentId() != null ? " (agent=" + burst[i].getAssignedAgentId() + ")" : ""));
        }

        System.out.println("\n================== Current Status ==================\n");
        for (int i = 0; i < burst.length; i++) {
            System.out.println("Order " + (i + 1) + ": " + burst[i].getOrderId() + " → " + burst[i].getStatus()
                    + (burst[i].getAssignedAgentId() != null ? " (agent=" + burst[i].getAssignedAgentId() + ")" : " (no agent)"));
        };

        System.out.println("\n================== 2 Orders Delivered ==================\n");
        // Pick any two assigned orders to complete
        for (int i = 0, completed = 0; i < burst.length && completed < 2; i++) {
            if (burst[i].getStatus() == OrderStatus.ASSIGNED) {
                deliveryService.markOutForDelivery(burst[i].getOrderId());
                deliveryService.markDelivered(burst[i].getOrderId()); // this will auto-pull from pending queue
                completed++;
            }
        }

        System.out.println("\n================== Orders Delivered 3 to 8 ==================\n");
        for (int i = 0; i < burst.length; i++) {
            if (burst[i].getStatus() == OrderStatus.ASSIGNED) {
                deliveryService.markOutForDelivery(burst[i].getOrderId());
                deliveryService.markDelivered(burst[i].getOrderId()); // this will auto-pull from pending queue
            }
        }
    }

    private static void displayRestaurantInfo(Restaurant restaurant) {
        System.out.println("\n🏪 Restaurant: " + restaurant.getName());
        System.out.println("👤 Owner: " + restaurant.getOwner().getUserName());
        System.out.println("📍 Serviceable Pincodes: " + restaurant.getServiceablePincodes());
        System.out.println("\n--- MENU ---");
        restaurant.getMenu().forEach(item ->
                System.out.println(item.getName() + " | ₹" + item.getPrice() + " | " + item.getType()));
    }
}