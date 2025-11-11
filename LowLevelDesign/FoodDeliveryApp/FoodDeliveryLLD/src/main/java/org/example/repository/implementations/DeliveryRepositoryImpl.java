package org.example.repository.implementations;

import org.example.model.user.DeliveryAgent;
import org.example.repository.DeliveryRepository;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryRepositoryImpl implements DeliveryRepository {
    // Map to keep track of Agent based on AgentID.
    private final Map<String, DeliveryAgent> agents = new ConcurrentHashMap<>();
    // Map to keep track of pincode based on List of Agents available in that pincode.
    private final Map<Integer, List<String>> pincodeAgents = new ConcurrentHashMap<>();
    private final Map<String, Integer> capacities = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> activeCounts = new ConcurrentHashMap<>();
    private final Map<String, List<String>> agentOrders = new ConcurrentHashMap<>();
    // Pending queues by pincode
    private final Map<Integer, Queue<String>> pendingByPincode = new ConcurrentHashMap<>();

    @Override
    public void registerAgent(DeliveryAgent agent) {
        agents.put(agent.getAgentId(), agent);
        capacities.putIfAbsent(agent.getAgentId(), 3); // default capacity
        activeCounts.putIfAbsent(agent.getAgentId(), new AtomicInteger(0));
        agentOrders.putIfAbsent(agent.getAgentId(), new CopyOnWriteArrayList<>());
    }

    @Override
    public void addCoverage(String agentId, int pincode) {
        pincodeAgents.computeIfAbsent(pincode, k -> new CopyOnWriteArrayList<>()).add(agentId);
    }

    @Override
    public List<String> findAgentIdsByPincode(int pincode) {
        return pincodeAgents.computeIfAbsent(pincode, k-> new CopyOnWriteArrayList<>());
    }

    @Override
    public void setCapacity(String agentId, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        capacities.put(agentId, capacity);
    }

    @Override
    public int getCapacity(String agentId) {
        return capacities.getOrDefault(agentId, 0);
    }

    @Override
    public int getActiveCount(String agentId) {
        return activeCounts.getOrDefault(agentId, new AtomicInteger(0)).get();
    }

    @Override
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

    @Override
    public void releaseSlot(String agentId) {
        activeCounts.putIfAbsent(agentId, new AtomicInteger(0));
        activeCounts.get(agentId).updateAndGet(v -> Math.max(0, v - 1));
    }

    @Override
    public void addOrderToAgent(String agentId, String orderId) {
        agentOrders.computeIfAbsent(agentId, k -> new CopyOnWriteArrayList<>()).add(orderId);
    }

    @Override
    public void removeOrderFromAgent(String agentId, String orderId) {
        releaseSlot(agentId);
        // Do NOT remove from agentOrders to preserve history
    }

    @Override
    public List<String> findOrdersByAgent(String agentId) {
        return new CopyOnWriteArrayList<>(agentOrders.getOrDefault(agentId, List.of()));
    }

    @Override
    public DeliveryAgent findById(String agentId) {
        return agents.get(agentId);
    };

    // Pending queue ops
    @Override
    public void enqueuePending(int pincode, String orderId) {
        pendingByPincode.computeIfAbsent(pincode, k -> new ConcurrentLinkedQueue<>()).offer(orderId);
    }

    @Override
    public String pollPending(int pincode) {
        Queue<String> q = pendingByPincode.get(pincode);
        return q == null ? null : q.poll();
    }

    @Override
    public List<String> snapshotPending(int pincode) {
        Queue<String> q = pendingByPincode.get(pincode);
        return q == null ? List.of() : List.copyOf(q);
    }
}
