```
UML-like ASCII overview

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

