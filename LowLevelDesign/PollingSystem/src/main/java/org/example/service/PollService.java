package org.example.service;

import org.example.enums.PollOption;
import org.example.enums.UserType;
import org.example.models.Poll;
import org.example.models.User;
import org.example.models.Vote;
import org.example.repository.PollRepository;
import org.example.repository.UserRepository;
import org.example.repository.VoteRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PollService {
    private final AtomicInteger pollCounter = new AtomicInteger(1);
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public PollService(PollRepository pollRepository, VoteRepository voteRepository, UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    public List<Poll> getPollsByUserId(int adminId) {
        return pollRepository.findByAdmin(adminId);
    }

    public Poll createPoll(List<String> pollOptions, String pollQuestion, int adminId) {
        User user = userRepository.findById(adminId);
        if (user == null || user.getUserType() != UserType.ADMIN) {
            throw new org.example.exception.UnauthorizedActionException("Only admins can create polls");
        };

        int pollId = pollCounter.getAndIncrement();
        List<String> cleanOptions = pollOptions.stream()
                .map(opt -> opt == null ? "" : opt.trim().toLowerCase())
                .filter(opt -> !opt.isEmpty())
                .distinct()
                .toList();

        if (cleanOptions.size() < 2) {
            throw new IllegalArgumentException("Poll must have at least 2 unique non-empty options");
        };

        Poll poll = new Poll(pollId, cleanOptions, pollQuestion, LocalDateTime.now(), adminId);
        pollRepository.save(poll);
        return poll;
    }

    public void recordVote(Vote vote) {
        voteRepository.recordVote(vote);
    }

    public Map<PollOption, Integer> getPollResults(int pollId) {
        return voteRepository.getResults(pollId);
    }

    public Poll getPoll(int pollId) {
        return pollRepository.findById(pollId);
    }
}
