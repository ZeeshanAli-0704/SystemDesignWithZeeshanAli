// replace fields & methods with atomic-safe versions
package org.example.repository;

import org.example.enums.PollOption;
import org.example.models.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteRepository {

    private final Map<Integer, Map<PollOption, AtomicInteger>> pollResults = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, PollOption>> userVotes = new ConcurrentHashMap<>();

    public boolean hasUserVoted(int userId, int pollId) {
        return userVotes.getOrDefault(userId, Map.of()).containsKey(pollId);
    }

    public void recordVote(Vote vote) {
        int pollId = vote.getPollID();
        PollOption option = vote.getOption();

        pollResults.computeIfAbsent(pollId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(option, k -> new AtomicInteger(0))
                .incrementAndGet();

        userVotes.computeIfAbsent(vote.getUserID(), k -> new ConcurrentHashMap<>())
                .put(pollId, option);
    }

    public Map<PollOption, Integer> getResults(int pollId) {
        Map<PollOption, AtomicInteger> raw = pollResults.getOrDefault(pollId, Map.of());
        Map<PollOption, Integer> out = new EnumMap<>(PollOption.class);
        raw.forEach((k, v) -> out.put(k, v.get()));
        return out;
    }

    public PollOption getUserVoteForPoll(int userId, int pollId) {
        return userVotes.getOrDefault(userId, Map.of()).get(pollId);
    }
}
