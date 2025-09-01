package org.example.service;

import org.example.enums.PollOption;
import org.example.exceptions.DuplicateVoteException;
import org.example.exceptions.InvalidOptionException;
import org.example.exceptions.PollNotFoundException;
import org.example.exceptions.UserNotFoundException;
import org.example.models.Poll;
import org.example.models.User;
import org.example.models.Vote;
import org.example.repository.UserRepository;
import org.example.repository.VoteRepository;

public class UserService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PollService pollService;

    public UserService(PollService pollService, VoteRepository voteRepository, UserRepository userRepository) {
        this.pollService = pollService;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public void castVote(Vote vote) throws Exception {
        int userId = vote.getUserID();
        int pollId = vote.getPollID();

        if (!userRepository.exists(userId)) {
            throw new UserNotFoundException(userId);
        }

        Poll poll = pollService.getPoll(pollId);
        if (poll == null) {
            throw new PollNotFoundException(pollId);
        }

        if (voteRepository.hasUserVoted(userId, pollId)) {
            throw new DuplicateVoteException("User has already voted in this poll.");
        }
        // (Validation will be added in Step 2)
        voteRepository.recordVote(vote);
    }

    public PollOption getUserVoteForPoll(int userId, int pollId) {
        return voteRepository.getUserVoteForPoll(userId, pollId);
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public User getUser(int userId) {
        return userRepository.findById(userId);
    }
}
