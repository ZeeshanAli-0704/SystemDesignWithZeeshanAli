Below is a concise, end-to-end entity mapping for your food ordering system, covering core entities, relationships, repositories (as in-memory stores), and key state transitions.

High-level ER-style diagram
```
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

Entities with key fields
```
class User {
  String userId;          // PK
  UserType userType;      // RESTAURANT_OWNER | CUSTOMER_USER | DELIVERY_AGENT
  String userName;
  String email;
  String city;
  GenderType gender;
}

class DeliveryAgent extends User {
  // No extra state; agentId == userId
  // Profile fields (vehicleType, etc.) could be added here if needed
}

class Restaurant {
  String id;              // PK (e.g., REST-UUID)
  String name;
  String location;
  User owner;             // FK -> User (RESTAURANT_OWNER)
  int pincode;            // primary operating pincode
  Set<Integer> serviceablePincodes;
  Map<String, FoodItem> menu; // foodId -> FoodItem
}

class FoodItem {
  String foodId;          // PK (UUID)
  String name;
  double price;
  FoodType type;          // VEGETARIAN | NON_VEGETARIAN
  String description;
}

class Order {
  String orderId;         // PK (ORD-UUID)
  User customer;          // FK -> User (CUSTOMER_USER)
  Restaurant restaurant;  // FK -> Restaurant
  List<FoodItem> items;
  OrderStatus status;     // see lifecycle below
  LocalDateTime timestamp;
  String assignedAgentId; // nullable, FK -> DeliveryAgent.userId
}
```

Order status lifecycle (state machine)
```
PLACED -> ACCEPTED -> ASSIGNED -> OUT_FOR_DELIVERY -> DELIVERED
         ├──────────> REJECTED
         ├──────────> CANCELED (by customer/restaurant before delivery)
ASSIGNED -> CANCELED (by customer/restaurant) [release capacity]
OUT_FOR_DELIVERY -> CANCELED (rare; if allowed by business rules) [release capacity]
```

Repository models and what they store
```
OrderRepository
- Map<String, Order> orderMap
- Responsibilities:
  - save(Order), findById(orderId)
  - queries: findByRestaurant(restaurantId), findByCustomer(customerId)
  - updateStatus(orderId, OrderStatus)

CustomerRepository (IDs only; history)
- Map<String, List<String>> customerOrderIds  // customerId -> [orderId...]
- Responsibilities:
  - addOrderId(customerId, orderId)
  - findOrderIds(customerId)

RestaurantRepository (IDs only; history)
- Map<String, List<String>> restaurantOrderIds // restaurantId -> [orderId...]
- Responsibilities:
  - addOrderId(restaurantId, orderId)
  - findOrderIds(restaurantId)

DeliveryRepository (routing, capacity, queues, history)
- Map<String, DeliveryAgent> agents
- Map<Integer, List<String>> pincodeAgents           // pincode -> [agentIds...]
- Map<String, Integer> capacities                    // agentId -> capacity (e.g., 3)
- Map<String, AtomicInteger> activeCounts            // agentId -> current active count
- Map<String, List<String>> agentOrders              // agentId -> append-only [orderIds] history
- Map<Integer, Queue<String>> pendingByPincode       // pincode -> FIFO queue of orderIds awaiting assignment

- Responsibilities:
  - registerAgent(agent), addCoverage(agentId, pincode)
  - setCapacity(agentId, capacity), getCapacity, getActiveCount
  - tryReserveSlot(agentId) [CAS increment if < capacity], releaseSlot(agentId)
  - addOrderToAgent(agentId, orderId) [history]
  - findOrdersByAgent(agentId)
  - Pending queue: enqueuePending(pincode, orderId), pollPending(pincode), snapshotPending(pincode)
```

Services and flows
```
OrderService
- createOrder(customer, restaurant, items):
  - Validate input
  - new Order(...), status=PLACED
  - Add to CustomerRepository history immediately (customerId -> orderId)
  - RestaurantService.handleNewOrder(order) [synchronous]
  - Save final order state in OrderRepository
  - Return Order

RestaurantService
- handleNewOrder(order):
  - Compute active restaurant load using OrderRepository + RestaurantRepository IDs
  - If capacity allows -> ACCEPTED:
      - RestaurantRepository.addOrderId(restaurantId, orderId)
      - Notify customer
      - Optionally call DeliveryService.assignOrQueue(order) here
    Else -> REJECTED and notify
- cancelByCustomer / cancelByRestaurant:
  - Validate allowed transition
  - Set status=CANCELED, save
  - deliveryService.releaseAgentIfAssigned(orderId)  // frees capacity and (optionally) processes pending
  - Notify

DeliveryService
- assignOrQueue(order):
  - Try assignOrder(order, restaurant.pincode):
      - If success: set ASSIGNED, save, addOrderToAgent history, notify
      - If no capacity: enqueuePending(pincode, orderId), leave status ACCEPTED
- markOutForDelivery(orderId):
  - ASSIGNED -> OUT_FOR_DELIVERY, save, notify
- markDelivered(orderId):
  - OUT_FOR_DELIVERY or ASSIGNED -> DELIVERED, save, releaseSlot(agentId), notify
  - processPendingForPincode(pincode) to auto-assign waiting orders
- releaseAgentIfAssigned(orderId):
  - If assigned, releaseSlot(agentId)
  - processPendingForPincode(pincode) to reuse freed capacity
- processPendingForPincode(pincode):
  - While capacity exists: pollPending, verify status=ACCEPTED, attempt assignOrder; if fail, re-enqueue and stop
```

Example of assignment capacity behavior with 2 agents
- Agents: Heena, Shabbir
- Capacity: 3 each, total concurrent active capacity = 6
- If 8 orders are ACCEPTED in the same pincode burst:
  - First 6 get ASSIGNED (activeCounts incremented)
  - Orders 7–8 are enqueued in pendingByPincode (status remains ACCEPTED)
  - As orders are delivered/canceled, capacity is freed and processPendingForPincode assigns from the queue

Notes
- Single source of truth: OrderRepository stores Order objects and their status; other repositories store only IDs and routing/capacity metadata.
- RestaurantRepository and CustomerRepository maintain history (no removals on cancel/deliver).
- DeliveryRepository maintains agent assignment history (append-only) and pending queues by pincode.
- All maps use thread-safe structures (ConcurrentHashMap, CopyOnWriteArrayList, ConcurrentLinkedQueue) suitable for your in-memory synchronous model.
