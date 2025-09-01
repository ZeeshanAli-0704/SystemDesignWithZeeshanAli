package org.example.repository;

import org.example.models.Poll;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PollRepository {
    private final Map<Integer, Poll> byId = new ConcurrentHashMap<>();
    private final Map<Integer, List<Poll>> byAdmin = new ConcurrentHashMap<>();

    public void save(Poll poll) {
        byId.put(poll.getPollId(), poll);
        byAdmin.computeIfAbsent(poll.getAdminId(), k -> new CopyOnWriteArrayList<>())
                .add(poll);
    }

    public Poll findById(int pollId) {
        return byId.get(pollId);
    }

    public List<Poll> findAll() {
        return new ArrayList<>(byId.values());
    }

    public List<Poll> findByAdmin(int adminId) {
        return new ArrayList<>(byAdmin.getOrDefault(adminId, List.of()));
    }
}
