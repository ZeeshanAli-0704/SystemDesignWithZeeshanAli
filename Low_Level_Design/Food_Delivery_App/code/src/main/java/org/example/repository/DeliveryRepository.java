package org.example.repository;

import org.example.model.user.DeliveryAgent;

import java.util.List;

public interface DeliveryRepository {
    // Agent registry
    void registerAgent(DeliveryAgent agent);

    // Coverage management
    void addCoverage(String agentId, int pincode);
    List<String> findAgentIdsByPincode(int pincode);

    // Capacity management
    void setCapacity(String agentId, int capacity);
    int getCapacity(String agentId);

    // Active assignments
    int getActiveCount(String agentId);
    boolean tryReserveSlot(String agentId); // atomic check+increment
    void releaseSlot(String agentId);       // decrement safely

    // Agent → order history (IDs only)
    void addOrderToAgent(String agentId, String orderId);
    void removeOrderFromAgent(String agentId, String orderId);
    List<String> findOrdersByAgent(String agentId);

    // Lookup
    DeliveryAgent findById(String agentId);

    // Pending queue (by pincode)
    void enqueuePending(int pincode, String orderId);
    String pollPending(int pincode); // returns null if none
    List<String> snapshotPending(int pincode); // read-only snapshot
}
