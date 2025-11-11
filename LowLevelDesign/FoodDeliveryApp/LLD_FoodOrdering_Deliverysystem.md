# Building a clean food ordering system in Java with in memory repos, delivery agent auto assign, and pending queues

## Table of Contents
- [Introduction](#introduction)
- [High level architecture](#high-level-architecture)
- [High level ER style diagram](#high-level-er-style-diagram)
- [Folder structure](#folder-structure)
- [Step 1 Domain model](#step-1-domain-model)
- [Step 2 Order status lifecycle](#step-2-order-status-lifecycle)
- [Step 3 Repository layer](#step-3-repository-layer)
- [Step 4 Repository implementations](#step-4-repository-implementations)
- [Step 5 Service layer](#step-5-service-layer)
- [Step 6 Main driver](#step-6-main-driver)
- [Step 7 UML like ASCII overview](#step-7-uml-like-ascii-overview)
- [Step 8 Design decisions and tradeoffs](#step-8-design-decisions-and-tradeoffs)
- [Step 9 How to run](#step-9-how-to-run)
- [Step 10 Extensions and next steps](#step-10-extensions-and-next-steps)

---

## Introduction
This post walks through building a small but production minded food ordering system in Java. It supports:
- Customers placing orders at restaurants
- Synchronous restaurant acceptance or rejection based on active load or put in a pending queue
- Delivery agents discoverable by pincode with capacity limits
- Fair delivery assignment to the least loaded agent
- Pending queue when agents are at capacity, with automatic assignment once capacity frees up

Everything is in memory for simplicity but layered to swap repositories for a database later.

---

## High level architecture
- Models: User, DeliveryAgent, Restaurant, FoodItem, Order
- Repositories: In memory stores behind interfaces
  - OrderRepository: single source of truth for Order objects
  - RestaurantRepository and CustomerRepository: keep order history by IDs
  - DeliveryRepository: agents, pincode coverage, capacity/active counts, agent assignment history, pending queues by pincode
- Services orchestrate flows
  - OrderService: create order, log to customer history, delegate to RestaurantService
  - RestaurantService: accept or reject, log to restaurant history, cancel handling
  - DeliveryService: assign, start delivery, deliver, release capacity, manage pending queues (auto assign later)
  - NotificationService: user notifications (console print for demo)
  - CustomerService: log orders to customer history

---

## High level ER style diagram

```text
    User (userId, userType, userName, email, city, gender)
    ├─ DeliveryAgent extends User (same identity; agentId == userId)
    └─ RestaurantOwner, Customer are Users distinguished by userType

    Restaurant (id, name, location, owner:User, pincode, serviceablePincodes, menu[foodId->FoodItem])
    └── has many FoodItem (by foodId)
    └── has many Orders (tracked as orderIds in RestaurantRepository)

    FoodItem (foodId, name, price, type, description)

    Order (orderId, customer:User, restaurant:Restaurant, items[List<FoodItem>], status, timestamp, assignedAgentId?)

    Repositories (IDs only for relationships; OrderRepository stores the full Orders)
    - OrderRepository: orderId -> Order
    - CustomerRepository: customerId -> List<orderId>
    - RestaurantRepository: restaurantId -> List<orderId>  (history)
    - DeliveryRepository:
        agents: agentId -> DeliveryAgent
        pincodeAgents: pincode -> List<agentId>
        capacities: agentId -> capacity
        activeCounts: agentId -> activeCount (Atomic)
        agentOrders: agentId -> List<orderId> (append-only history)
        pendingByPincode: pincode -> Queue<orderId> (unassigned waiting)

    Relationships (cardinality)
    - User (CUSTOMER) 1 --- * Order (as customer)
    - User (RESTAURANT_OWNER) 1 --- * Restaurant (as owner)
    - Restaurant 1 --- * FoodItem (menu)
    - Restaurant 1 --- * Order (via RestaurantRepository: List<orderId>)
    - DeliveryAgent 1 --- * Order (assignments; tracked by agentOrders history)
    - Pincode 1 --- * DeliveryAgent (via DeliveryRepository.pincodeAgents)

```
---

## Folder structure
```text
src/
└── org/
    └── example/
        ├── Main.java
        ├── model/
        │   ├── food/
        │   │   ├── FoodItem.java
        │   │   └── FoodType.java
        │   ├── order/
        │   │   ├── Order.java
        │   │   └── OrderStatus.java
        │   ├── restaurant/
        │   │   └── Restaurant.java
        │   └── user/
        │       ├── GenderType.java
        │       ├── User.java
        │       ├── UserType.java
        │       └── DeliveryAgent.java
        ├── repository/
        │   ├── CustomerRepository.java
        │   ├── DeliveryRepository.java
        │   ├── OrderRepository.java
        │   ├── RestaurantRepository.java
        │   └── implementations/
        │       ├── CustomerRepositoryImpl.java
        │       ├── DeliveryRepositoryImpl.java
        │       ├── OrderRepositoryImpl.java
        │       └── RestaurantRepositoryImpl.java
        └── service/
            ├── CustomerService.java
            ├── DeliveryService.java
            ├── NotificationService.java
            ├── OrderService.java
            └── RestaurantService.java
```

---

## Step 1 Domain model

Define core entities and enums. 
DeliveryAgent extends User for identity only. 
Order holds assignedAgentId as a nullable field.

### UserType and GenderType
```java
package org.example.model.user;

public enum UserType { RESTAURANT_OWNER, CUSTOMER_USER, DELIVERY_AGENT }
```
```java
package org.example.model.user;

public enum GenderType { MALE, FEMALE }
```

### User
```java
package org.example.model.user;

import java.util.UUID;

public class User {
    private final String userId = UUID.randomUUID().toString();
    private final UserType userType;
    private final String userName;
    private final String email;
    private final String city;
    private final GenderType gender;

    public User(UserType userType, String userName, String email, String city, GenderType gender) {
        this.userType = userType;
        this.userName = userName;
        this.email = email;
        this.city = city;
        this.gender = gender;
    }

    public String getUserId() { return userId; }
    public UserType getUserType() { return userType; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public GenderType getGender() { return gender; }
}
```

### DeliveryAgent (identity only; agentId == userId)
```java
package org.example.model.user;

public class DeliveryAgent extends User {
    public DeliveryAgent(String name, String email, String city, GenderType gender) {
        super(UserType.DELIVERY_AGENT, name, email, city, gender);
    }
    public String getAgentId() { return getUserId(); }
}
```

### FoodType and FoodItem
```java
package org.example.model.food;

public enum FoodType { VEGETARIAN, NON_VEGETARIAN }
```

```java
package org.example.model.food;

import java.util.UUID;

public class FoodItem {
    private final String foodId = UUID.randomUUID().toString();
    private final String name;
    private final double price;
    private final FoodType type;
    private final String description;

    public FoodItem(String name, double price, FoodType type, String description) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    public String getFoodId() { return foodId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public FoodType getType() { return type; }
    public String getDescription() { return description; }
}
```

### Restaurant
```java
package org.example.model.restaurant;

import org.example.model.food.FoodItem;
import org.example.model.user.User;

import java.util.*;

public class Restaurant {
    private final String id = "REST-" + UUID.randomUUID();
    private final String name;
    private final String location;
    private final User owner;
    private final int pincode;
    private final Set<Integer> serviceablePincodes = new HashSet<>();
    private final Map<String, FoodItem> menu = new LinkedHashMap<>();

    public Restaurant(String name, String location, User owner, int pincode) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.pincode = pincode;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public User getOwner() { return owner; }
    public int getPincode() { return pincode; }

    public void addServiceablePincode(int pin) { serviceablePincodes.add(pin); }
    public Set<Integer> getServiceablePincodes() { return new HashSet<>(serviceablePincodes); }

    public void addFoodItem(FoodItem food) { menu.put(food.getFoodId(), food); }
    public List<FoodItem> getMenu() { return new ArrayList<>(menu.values()); }
}
```

### Order

```java
package org.example.model.order;
import org.example.model.food.FoodItem;
import org.example.model.restaurant.Restaurant;
import org.example.model.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String orderId = "ORD-" + UUID.randomUUID();
    private final User customer;
    private final Restaurant restaurant;
    private final List<FoodItem> items;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private OrderStatus status = OrderStatus.PLACED;
    private String assignedAgentId; // nullable

    public Order(User customer, Restaurant restaurant, List<FoodItem> items) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public User getCustomer() { return customer; }
    public Restaurant getRestaurant() { return restaurant; }
    public List<FoodItem> getItems() { return items; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getAssignedAgentId() { return assignedAgentId; }
    public void setAssignedAgentId(String assignedAgentId) { this.assignedAgentId = assignedAgentId; }
}
```

---

## Step 2 Order status lifecycle
Lifecycle: PLACED -> ACCEPTED -> ASSIGNED -> OUT FOR DELIVERY -> DELIVERED; REJECTED and CANCELED as exits.

```java
package org.example.model.order;

public enum OrderStatus {
    PLACED,
    ACCEPTED,
    ASSIGNED,
    OUT_FOR_DELIVERY,
    IN_PROGRESS,
    REJECTED,
    DELIVERED,
    CANCELED
}
```

---

## Step 3 Repository layer
Only OrderRepository stores full Orders; others store IDs and metadata.

```java
package org.example.repository;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findByRestaurant(String restaurantId);
    List<Order> findByCustomer(String customerId);
    void updateStatus(String orderId, OrderStatus newStatus);
}
```

```java
package org.example.repository;

import java.util.List;

public interface CustomerRepository {
    void addOrderId(String customerId, String orderId);
    List<String> findOrderIds(String customerId);
}
```

```java
package org.example.repository;

import java.util.List;

public interface RestaurantRepository {
    boolean addOrderId(String restaurantId, String orderId);
    List<String> findOrderIds(String restaurantId);
}
```

```java
package org.example.repository;

import org.example.model.user.DeliveryAgent;
import java.util.List;

public interface DeliveryRepository {
    void registerAgent(DeliveryAgent agent);
    DeliveryAgent findById(String agentId);

    void addCoverage(String agentId, int pincode);
    List<String> findAgentIdsByPincode(int pincode);

    void setCapacity(String agentId, int capacity);
    int getActiveCount(String agentId);
    boolean tryReserveSlot(String agentId);
    void releaseSlot(String agentId);

    void addOrderToAgent(String agentId, String orderId);
    List<String> findOrdersByAgent(String agentId);

    void enqueuePending(int pincode, String orderId);
    String pollPending(int pincode);
    List<String> snapshotPending(int pincode);
}
```

---

## Step 4 Repository implementations
Thread safe using ConcurrentHashMap, CopyOnWriteArrayList, ConcurrentLinkedQueue.

### OrderRepositoryImpl
```java
package org.example.repository.implementations;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository {
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    public void save(Order order) { orderMap.put(order.getOrderId(), order); }
    public Order findById(String orderId) { return orderMap.get(orderId); }

    public List<Order> findByRestaurant(String restaurantId) {
        return orderMap.values().stream()
                .filter(o -> o.getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.toList());
    }
    public List<Order> findByCustomer(String customerId) {
        return orderMap.values().stream()
                .filter(o -> o.getCustomer().getUserId().equals(customerId))
                .collect(Collectors.toList());
    }
    public void updateStatus(String orderId, OrderStatus newStatus) {
        Order o = orderMap.get(orderId);
        if (o != null) o.setStatus(newStatus);
    }
}
```

### CustomerRepositoryImpl
```java
package org.example.repository.implementations;
import org.example.repository.CustomerRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Map<String, List<String>> customerOrderIds = new ConcurrentHashMap<>();

    public void addOrderId(String customerId, String orderId) {
        customerOrderIds.computeIfAbsent(customerId, k -> new CopyOnWriteArrayList<>()).add(orderId);
    }
    public List<String> findOrderIds(String customerId) {
        return new CopyOnWriteArrayList<>(customerOrderIds.getOrDefault(customerId, List.of()));
    }
}
```

### RestaurantRepositoryImpl
```java
package org.example.repository.implementations;
import org.example.repository.RestaurantRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RestaurantRepositoryImpl implements RestaurantRepository {
    private final Map<String, List<String>> restaurantOrderIds = new ConcurrentHashMap<>();

    public boolean addOrderId(String restaurantId, String orderId) {
        restaurantOrderIds.computeIfAbsent(restaurantId, k -> new CopyOnWriteArrayList<>()).add(orderId);
        return true;
    }
    public List<String> findOrderIds(String restaurantId) {
        return new CopyOnWriteArrayList<>(restaurantOrderIds.getOrDefault(restaurantId, List.of()));
    }
}
```

### DeliveryRepositoryImpl (capacity + pending queues)
```java
package org.example.repository.implementations;
import org.example.model.user.DeliveryAgent;
import org.example.repository.DeliveryRepository;
import java.util.*;
import java.util.concurrent.*;

public class DeliveryRepositoryImpl implements DeliveryRepository {
    private final Map<String, DeliveryAgent> agents = new ConcurrentHashMap<>();
    private final Map<Integer, List<String>> pincodeAgents = new ConcurrentHashMap<>();
    private final Map<String, Integer> capacities = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> activeCounts = new ConcurrentHashMap<>();
    private final Map<String, List<String>> agentOrders = new ConcurrentHashMap<>();
    private final Map<Integer, Queue<String>> pendingByPincode = new ConcurrentHashMap<>();

    public void registerAgent(DeliveryAgent agent) {
        agents.put(agent.getAgentId(), agent);
        capacities.putIfAbsent(agent.getAgentId(), 3);
        activeCounts.putIfAbsent(agent.getAgentId(), new AtomicInteger(0));
        agentOrders.putIfAbsent(agent.getAgentId(), new CopyOnWriteArrayList<>());
    }
    public DeliveryAgent findById(String agentId) { return agents.get(agentId); }

    public void addCoverage(String agentId, int pincode) {
        pincodeAgents.computeIfAbsent(pincode, k -> new CopyOnWriteArrayList<>()).add(agentId);
    }
    public List<String> findAgentIdsByPincode(int pincode) {
        return new CopyOnWriteArrayList<>(pincodeAgents.getOrDefault(pincode, List.of()));
    }

    public void setCapacity(String agentId, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        capacities.put(agentId, capacity);
    }
    public int getActiveCount(String agentId) {
        return activeCounts.getOrDefault(agentId, new AtomicInteger(0)).get();
    }
    public boolean tryReserveSlot(String agentId) {
        capacities.putIfAbsent(agentId, 0);
        activeCounts.putIfAbsent(agentId, new AtomicInteger(0));
        AtomicInteger cur = activeCounts.get(agentId);
        int cap = capacities.get(agentId);
        while (true) {
            int v = cur.get();
            if (v >= cap) return false;
            if (cur.compareAndSet(v, v + 1)) return true;
        }
    }
    public void releaseSlot(String agentId) {
        activeCounts.putIfAbsent(agentId, new AtomicInteger(0));
        activeCounts.get(agentId).updateAndGet(v -> Math.max(0, v - 1));
    }

    public void addOrderToAgent(String agentId, String orderId) {
        agentOrders.computeIfAbsent(agentId, k -> new CopyOnWriteArrayList<>()).add(orderId);
    }
    public List<String> findOrdersByAgent(String agentId) {
        return new CopyOnWriteArrayList<>(agentOrders.getOrDefault(agentId, List.of()));
    }

    public void enqueuePending(int pincode, String orderId) {
        pendingByPincode.computeIfAbsent(pincode, k -> new ConcurrentLinkedQueue<>()).offer(orderId);
    }
    public String pollPending(int pincode) {
        Queue<String> q = pendingByPincode.get(pincode);
        return q == null ? null : q.poll();
    }
    public List<String> snapshotPending(int pincode) {
        Queue<String> q = pendingByPincode.get(pincode);
        return q == null ? List.of() : List.copyOf(q);
    }
}
```

---

## Step 5 Service layer
User facing updates, history, placement, acceptance, delivery assignment and pending logic.

### NotificationService
```java
package org.example.service;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;

public class NotificationService {
    public void notifyCustomer(Order order) {
        String msg = switch (order.getStatus()) {
            case ACCEPTED -> "🎉 Your order has been accepted!";
            case ASSIGNED -> "🚗 Your order has been assigned to Delivery Agent";
            case OUT_FOR_DELIVERY -> "🚗 Your order is out for delivery!";
            case IN_PROGRESS -> "🍳 Your order is being prepared.";
            case DELIVERED -> "✅ Your order has been delivered!";
            case CANCELED -> "🛑 Your order has been canceled.";
            case REJECTED -> "😞 Sorry, your order was rejected.";
            default -> "📦 Order placed successfully!";
        };
        System.out.println("📩 Notification to " + order.getCustomer().getUserName()
                + ": " + msg + " (Order ID: " + order.getOrderId() + ")");
    }
    public void notifyRestaurantCancellation(Order order, String actor) {
        System.out.println("📢 Notify restaurant (" + order.getRestaurant().getName()
                + "): Order " + order.getOrderId() + " canceled by " + actor + ".");
    }
}
```

### CustomerService
```java
package org.example.service;

import org.example.model.order.Order;
import org.example.model.user.User;
import org.example.repository.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository repo) { this.customerRepository = repo; }
    public void addOrder(User customer, Order order) {
        customerRepository.addOrderId(customer.getUserId(), order.getOrderId());
    }
}
```

### OrderService
```java
package org.example.service;

import org.example.model.food.FoodItem;
import org.example.model.order.Order;
import org.example.model.restaurant.Restaurant;
import org.example.model.user.User;
import org.example.repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepo, RestaurantService restaurantService, CustomerService customerService) {
        this.orderRepository = orderRepo;
        this.restaurantService = restaurantService;
        this.customerService = customerService;
    }

    public Order createOrder(User user, Restaurant restaurant, List<FoodItem> items) {
        if (user == null || restaurant == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Invalid order request.");
        }
        Order order = new Order(user, restaurant, items);

        customerService.addOrder(user, order);

        System.out.println("\n🛒 " + user.getUserName() + " placed an order at "
                + restaurant.getName() + " waiting for confirmation.");

        restaurantService.handleNewOrder(order);

        orderRepository.save(order);
        return order;
    }
}
```

### RestaurantService
```java
package org.example.service;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.RestaurantRepository;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             OrderRepository orderRepository,
                             NotificationService notificationService) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    public void handleNewOrder(Order order) {
        long active = restaurantRepository.findOrderIds(order.getRestaurant().getId()).stream()
                .map(orderRepository::findById)
                .filter(o -> o != null)
                .map(Order::getStatus)
                .filter(s -> s == OrderStatus.PLACED || s == OrderStatus.ACCEPTED || s == OrderStatus.IN_PROGRESS)
                .count();

        boolean canAccept = active < 5;
        if (canAccept) {
            order.setStatus(OrderStatus.ACCEPTED);
            restaurantRepository.addOrderId(order.getRestaurant().getId(), order.getOrderId());
            System.out.println("✅ " + order.getRestaurant().getName() + " accepted the order.");
        } else {
            order.setStatus(OrderStatus.REJECTED);
            System.out.println("❌ " + order.getRestaurant().getName() + " rejected the order due to load.");
        }
        notificationService.notifyCustomer(order);
    }

    // Add cancel methods as needed; ensure they call DeliveryService.releaseAgentIfAssigned(orderId)
}
```

### DeliveryService
```java
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

    public DeliveryService(DeliveryRepository deliveryRepository,
                           OrderRepository orderRepository,
                           NotificationService notificationService) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    public boolean assignOrQueue(Order order) {
        int pin = order.getRestaurant().getPincode();
        if (assignOrder(order, pin)) return true;
        deliveryRepository.enqueuePending(pin, order.getOrderId());
        System.out.println("⏳ Queued order " + order.getOrderId() + " for pincode " + pin + " (no agent available)");
        return false;
    }

    public boolean assignOrder(Order order, int pincode) {
        if (order == null) return false;
        if (order.getAssignedAgentId() != null) return true;

        List<String> agentIds = deliveryRepository.findAgentIdsByPincode(pincode);
        if (agentIds.isEmpty()) {
            System.out.println("🚫 No delivery agents registered for pincode " + pincode);
            return false;
        }

        List<String> sorted = new ArrayList<>(agentIds);
        Collections.sort(sorted, Comparator.comparingInt(deliveryRepository::getActiveCount));

        String chosenId = null;
        for (String id : sorted) {
            if (deliveryRepository.tryReserveSlot(id)) { chosenId = id; break; }
        }
        if (chosenId == null) return false;

        order.setAssignedAgentId(chosenId);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        deliveryRepository.addOrderToAgent(chosenId, order.getOrderId());
        DeliveryAgent agent = deliveryRepository.findById(chosenId);
        System.out.println("🚴 Assigned order " + order.getOrderId() + " to agent " +
                (agent != null ? agent.getUserName() : chosenId));
        notificationService.notifyCustomer(order);
        return true;
    }

    public boolean markOutForDelivery(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getAssignedAgentId() == null) return false;
        if (order.getStatus() != OrderStatus.ASSIGNED) return false;

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
        notificationService.notifyCustomer(order);
        System.out.println("📦 Order " + order.getOrderId() + " is out for delivery.");
        return true;
    }

    public boolean markDelivered(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getAssignedAgentId() == null) return false;
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY && order.getStatus() != OrderStatus.ASSIGNED) return false;

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        deliveryRepository.releaseSlot(order.getAssignedAgentId());
        notificationService.notifyCustomer(order);
        System.out.println("✅ Delivered order " + order.getOrderId());

        processPendingForPincode(order.getRestaurant().getPincode());
        return true;
    }

    public void releaseAgentIfAssigned(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null || order.getAssignedAgentId() == null) return;
        deliveryRepository.releaseSlot(order.getAssignedAgentId());
        processPendingForPincode(order.getRestaurant().getPincode());
    }

    public void processPendingForPincode(int pincode) {
        while (true) {
            String nextOrderId = deliveryRepository.pollPending(pincode);
            if (nextOrderId == null) return;

            Order next = orderRepository.findById(nextOrderId);
            if (next == null || next.getStatus() != OrderStatus.ACCEPTED) continue;

            boolean assigned = assignOrder(next, pincode);
            if (!assigned) {
                deliveryRepository.enqueuePending(pincode, nextOrderId);
                return;
            }
        }
    }
}
```

---

## Step 6 Main driver
Sets up data, registers agents with capacity, places 8 orders for one pincode, assigns immediately or enqueues, then marks a couple delivered to free capacity and auto assign queued ones.

```java
package org.example;

import org.example.model.food.FoodItem;
import org.example.model.food.FoodType;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.model.restaurant.Restaurant;
import org.example.model.user.*;
import org.example.repository.*;
import org.example.repository.implementations.*;
import org.example.service.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        User owner = new User(UserType.RESTAURANT_OWNER, "Owner", "owner@ex.com", "BLR", GenderType.MALE);
        User customer = new User(UserType.CUSTOMER_USER, "Customer", "cust@ex.com", "BLR", GenderType.FEMALE);

        Restaurant spiceGarden = new Restaurant("Spice Garden", "MG Road", owner, 560001);
        spiceGarden.addServiceablePincode(560001);

        FoodItem biryani = new FoodItem("Chicken Biryani", 220.0, FoodType.NON_VEGETARIAN, "Aromatic basmati rice");
        FoodItem paneer = new FoodItem("Paneer Tikka", 180.0, FoodType.VEGETARIAN, "Grilled paneer");
        spiceGarden.addFoodItem(biryani);
        spiceGarden.addFoodItem(paneer);

        OrderRepository orderRepo = new OrderRepositoryImpl();
        RestaurantRepository restaurantRepo = new RestaurantRepositoryImpl();
        CustomerRepository customerRepo = new CustomerRepositoryImpl();
        DeliveryRepository deliveryRepo = new DeliveryRepositoryImpl();

        CustomerService customerService = new CustomerService(customerRepo);
        NotificationService notificationService = new NotificationService();
        RestaurantService restaurantService = new RestaurantService(restaurantRepo, orderRepo, notificationService);
        OrderService orderService = new OrderService(orderRepo, restaurantService, customerService);
        DeliveryService deliveryService = new DeliveryService(deliveryRepo, orderRepo, notificationService);

        DeliveryAgent a = new DeliveryAgent("Heena", "heena@ex.com", "BLR", GenderType.FEMALE);
        DeliveryAgent b = new DeliveryAgent("Shabbir", "shabbir@ex.com", "BLR", GenderType.MALE);
        deliveryRepo.registerAgent(a);
        deliveryRepo.registerAgent(b);
        deliveryRepo.addCoverage(a.getAgentId(), 560001);
        deliveryRepo.addCoverage(b.getAgentId(), 560001);
        deliveryRepo.setCapacity(a.getAgentId(), 3);
        deliveryRepo.setCapacity(b.getAgentId(), 3);

        System.out.println("\n=== Burst: 8 orders (assign or queue) ===\n");
        Order[] burst = new Order[8];
        for (int i = 0; i < burst.length; i++) {
            burst[i] = orderService.createOrder(customer, spiceGarden, Arrays.asList(biryani, paneer));
            if (burst[i].getStatus() == OrderStatus.ACCEPTED) {
                deliveryService.assignOrQueue(burst[i]); // assign now or enqueue
            }
            System.out.println("Order " + (i + 1) + ": " + burst[i].getOrderId() + " → " + burst[i].getStatus()
                    + (burst[i].getAssignedAgentId() != null ? " (agent=" + burst[i].getAssignedAgentId() + ")" : " (no agent)"));
        }

        // Complete two assigned orders to free capacity and trigger auto assignment
        int completed = 0;
        for (int i = 0; i < burst.length && completed < 2; i++) {
            if (burst[i].getStatus() == OrderStatus.ASSIGNED) {
                deliveryService.markOutForDelivery(burst[i].getOrderId());
                deliveryService.markDelivered(burst[i].getOrderId()); // triggers pending processing
                completed++;
            }
        }

        System.out.println("\n=== Summary ===");
        for (int i = 0; i < burst.length; i++) {
            System.out.println("Order " + (i + 1) + ": " + burst[i].getOrderId() + " → " + burst[i].getStatus()
                    + (burst[i].getAssignedAgentId() != null ? " (agent=" + burst[i].getAssignedAgentId() + ")" : " (no agent)"));
        }
    }
}
```

---

## Step 7 UML like ASCII overview
```text
UML-like ASCII overview (compact)

Packages
--------
org.example.model.user
org.example.model.restaurant
org.example.model.food
org.example.model.order
org.example.repository (+ .implementations)
org.example.service

Core domain entities
--------------------
+----------------------------+
| User                       |
+----------------------------+
| - userId: String           |
| - userType: UserType       |
| - userName: String         |
| - email: String            |
| - city: String             |
| - gender: GenderType       |
+----------------------------+

           ^ (extends)
           |
+----------------------------+
| DeliveryAgent              |
+----------------------------+
| + getAgentId(): String     |  // alias to userId
+----------------------------+

+----------------------------+
| Restaurant                 |
+----------------------------+
| - id: String               |
| - name: String             |
| - location: String         |
| - owner: User              |
| - pincode: int             |
| - serviceablePincodes: Set |
| - menu: Map<foodId,Item>   |
+----------------------------+

+----------------------------+
| FoodItem                   |
+----------------------------+
| - foodId: String           |
| - name: String             |
| - price: double            |
| - type: FoodType           |
| - description: String      |
+----------------------------+

+----------------------------+
| Order                      |
+----------------------------+
| - orderId: String          |
| - customer: User           |
| - restaurant: Restaurant   |
| - items: List<FoodItem>    |
| - status: OrderStatus      |
| - timestamp: LocalDateTime |
| - assignedAgentId: String? |
+----------------------------+

Enums
-----
UserType { RESTAURANT_OWNER, CUSTOMER_USER, DELIVERY_AGENT }
GenderType { MALE, FEMALE }
FoodType { VEGETARIAN, NON_VEGETARIAN }
OrderStatus { PLACED, ACCEPTED, ASSIGNED, OUT_FOR_DELIVERY, IN_PROGRESS, REJECTED, DELIVERED, CANCELED }

Relationships (cardinality)
---------------------------
User (CUSTOMER) 1 ─── * Order                 (Order.customer)
User (OWNER)    1 ─── * Restaurant           (Restaurant.owner)
Restaurant      1 ─── * FoodItem (menu)      (Restaurant.menu values)
Restaurant      1 ─── * Order (history IDs)  (RestaurantRepository)
DeliveryAgent   1 ─── * Order (assignments)  (DeliveryRepository.agentOrders)
Pincode         1 ─── * DeliveryAgent        (DeliveryRepository.pincodeAgents)

Repositories (interfaces, in-memory stores)
-------------------------------------------
+----------------------------------------------+
| OrderRepository                              |
+----------------------------------------------+
| + save(order: Order): void                   |
| + findById(id: String): Order                |
| + findByRestaurant(restId: String): List     |
| + findByCustomer(custId: String): List       |
| + updateStatus(id: String, s: OrderStatus)   |
+----------------------------------------------+

Impl: OrderRepositoryImpl
- orderMap: ConcurrentHashMap<String, Order>

+----------------------------------------------+
| CustomerRepository                           |
+----------------------------------------------+
| + addOrderId(customerId, orderId): void      |
| + findOrderIds(customerId): List<String>     |
+----------------------------------------------+

Impl: CustomerRepositoryImpl
- customerOrderIds: ConcurrentHashMap<String, CopyOnWriteArrayList<String>>

+----------------------------------------------+
| RestaurantRepository                         |
+----------------------------------------------+
| + addOrderId(restaurantId, orderId): boolean |
| + findOrderIds(restaurantId): List<String>   |
+----------------------------------------------+

Impl: RestaurantRepositoryImpl
- restaurantOrderIds: ConcurrentHashMap<String, CopyOnWriteArrayList<String>>

+--------------------------------------------------------------------+
| DeliveryRepository                                                 |
+--------------------------------------------------------------------+
| Agent registry                                                     |
| + registerAgent(agent: DeliveryAgent): void                        |
| + findById(agentId): DeliveryAgent                                 |
| Coverage                                                           |
| + addCoverage(agentId, pincode): void                              |
| + findAgentIdsByPincode(pin): List<String>                         |
| Capacity / active counts                                           |
| + setCapacity(agentId, cap): void                                  |
| + getCapacity(agentId): int                                        |
| + getActiveCount(agentId): int                                     |
| + tryReserveSlot(agentId): boolean                                 |
| + releaseSlot(agentId): void                                       |
| Assignments (history)                                              |
| + addOrderToAgent(agentId, orderId): void                          |
| + findOrdersByAgent(agentId): List<String>                         |
| Pending queues (by pincode)                                        |
| + enqueuePending(pin, orderId): void                               |
| + pollPending(pin): String                                         |
| + snapshotPending(pin): List<String>                               |
+--------------------------------------------------------------------+

Impl: DeliveryRepositoryImpl
- agents: ConcurrentHashMap<String, DeliveryAgent>
- pincodeAgents: ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>
- capacities: ConcurrentHashMap<String, Integer>
- activeCounts: ConcurrentHashMap<String, AtomicInteger>
- agentOrders: ConcurrentHashMap<String, CopyOnWriteArrayList<String>> // history
- pendingByPincode: ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>>

Services (business orchestration)
---------------------------------
+------------------------------------------------------+
| OrderService                                         |
+------------------------------------------------------+
| - orderRepository: OrderRepository                   |
| - restaurantService: RestaurantService               |
| - customerService: CustomerService                   |
| + createOrder(user, restaurant, items): Order        |
|   (adds to customer history, calls RestaurantService)|
+------------------------------------------------------+

+-------------------------------------------------------------------+
| RestaurantService                                                 |
+-------------------------------------------------------------------+
| - restaurantRepository: RestaurantRepository                      |
| - orderRepository: OrderRepository                                 |
| - notificationService: NotificationService                         |
| - (optionally) deliveryService: DeliveryService                    |
| + handleNewOrder(order): void                                      |
|   -> ACCEPTED/REJECTED, add to restaurant history, notify          |
|   -> optionally call deliveryService.assignOrQueue(order)          |
| + cancelByCustomer(orderId): boolean                               |
| + cancelByRestaurant(orderId, reason): boolean                     |
|   (sets CANCELED, releases agent via DeliveryService, notifies)    |
+-------------------------------------------------------------------+

+--------------------------------------------------------------------------------------------------+
| DeliveryService                                                                                  |
+--------------------------------------------------------------------------------------------------+
| - deliveryRepository: DeliveryRepository                                                         |
| - orderRepository: OrderRepository                                                                |
| - notificationService: NotificationService                                                        |
| + assignOrder(order, pincode): boolean                                                            |
| + assignOrQueue(order): boolean  (assign or enqueue by pincode)                                   |
| + markOutForDelivery(orderId): boolean  (ASSIGNED -> OUT_FOR_DELIVERY)                            |
| + markDelivered(orderId): boolean      (OFD/ASSIGNED -> DELIVERED, release capacity, process Q)   |
| + releaseAgentIfAssigned(orderId): void (on cancel/unassign; release capacity, process Q)         |
| + processPendingForPincode(pin): void  (drain pending while capacity available)                   |
+--------------------------------------------------------------------------------------------------+

+------------------------------------------+
| CustomerService                          |
+------------------------------------------+
| - customerRepository: CustomerRepository |
| + addOrder(user, order): void            |
+------------------------------------------+

+-------------------------------------------+
| NotificationService                       |
+-------------------------------------------+
| + notifyCustomer(order): void             |
| + notifyRestaurantCancellation(order,actor)|
+-------------------------------------------+

Key flows (sequence summary)
----------------------------
Customer -> OrderService.createOrder
  -> CustomerRepository.addOrderId
  -> RestaurantService.handleNewOrder
     -> if ACCEPTED: RestaurantRepository.addOrderId
     -> NotificationService.notifyCustomer
  -> OrderRepository.save(final state)

RestaurantService (cancel)
  -> set CANCELED, save
  -> DeliveryService.releaseAgentIfAssigned(orderId)
  -> notify customer and restaurant

DeliveryService.assignOrQueue
  -> tryReserveSlot on least-loaded agent in pincode
     -> if success: set ASSIGNED, save, addOrderToAgent, notify
     -> else: enqueuePending(pin, orderId)

DeliveryService.markDelivered
  -> set DELIVERED, save
  -> releaseSlot(agentId)
  -> notify
  -> processPendingForPincode(pin) to auto-assign queued orders

Legend
------
- PK/FK noted in comments; associations via IDs in repositories.
- Multiplicity: 1---* means one-to-many.
- All repositories are in-memory, thread-safe (ConcurrentHashMap, CopyOnWriteArrayList, ConcurrentLinkedQueue).
```

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/7cxyvywnfrt3gxjcwgxp.jpg)
---

## Step 8 Design decisions and tradeoffs
- Single source of truth: Only OrderRepository stores full Order objects, preventing divergence.
- IDs in customer and restaurant repos: Lightweight history; easy to replace with a DB.
- Pending queues by pincode: Ensures fair, FIFO assignment when agents are busy.
- Capacity management: Atomic counters per agent guarantee no over assignment under concurrency.
- Synchronous flow: Immediate acceptance decision; easy to move to async later with futures or messaging.

---

## Step 9 How to run
- Use Java 17+ (or your preferred version).
- Place files under the folder structure above.
- Compile and run Main.java.
- Expected behavior with two agents (capacity 3 each):
  - First 6 orders are assigned
  - Remaining orders are queued (status ACCEPTED)
  - When you mark deliveries complete, pending orders are auto assigned

---

## Step 10 Extensions and next steps
- Persistence: Replace repository implementations with JDBC/JPA or a document store; keep interfaces intact.
- Async: Use CompletableFuture or a message broker for acceptance and assignment.
- Reassignment: Add an unassign flow; reuse the pending queue to pick the next agent.
- SLAs: Auto cancel or reassign if ASSIGNED or OUT FOR DELIVERY exceeds a threshold.
- Pricing and tax: Add a PricingService to compute totals and discounts.
- Observability: Replace System.out with a logging framework (e.g., SLF4J + Logback).
- API layer: Expose REST endpoints with Spring Boot to make it a usable service.



More Details:

Get all articles related to system design
Hastag: SystemDesignWithZeeshanAli

systemdesignwithzeeshanali

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

