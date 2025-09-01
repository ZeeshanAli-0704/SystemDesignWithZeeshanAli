package org.example;

import org.example.enums.PollOption;
import org.example.controllers.PollController;
import org.example.controllers.UserController;
import org.example.enums.UserType;
import org.example.models.Poll;
import org.example.models.User;
import org.example.models.Vote;
import org.example.repository.PollRepository;
import org.example.repository.UserRepository;
import org.example.repository.VoteRepository;
import org.example.service.PollService;
import org.example.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        // Repositories
        PollRepository pollRepository = new PollRepository();
        VoteRepository voteRepository = new VoteRepository();
        UserRepository userRepository = new UserRepository();

        // Services & Controllers
        PollService pollService = new PollService(pollRepository, voteRepository, userRepository);
        UserService userService = new UserService(pollService, voteRepository, userRepository);
        PollController pollController = new PollController(pollService);
        UserController userController = new UserController(userService);

        // Admin
        User admin = new User(1001, "Admin", UserType.ADMIN);
        userService.registerUser(admin);

        // Create a poll
        List<String> options = Arrays.asList("Apple", "Mango", "Banana", "Grapes");
        String question = "Which is your favorite option?";
        Poll poll = pollController.createPoll(options, question, admin);

        // Users
        User user1 = new User(1, "Alice", UserType.USER);
        User user2 = new User(2, "Bob", UserType.USER);
        User user3 = new User(3, "Charlie", UserType.USER);
        User user4 = new User(4, "Diana", UserType.USER);
        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(user3);
        userService.registerUser(user4);

        int pollId = poll.getPollId();
        userController.castVote(new Vote(pollId, user1.getUserId(), PollOption.OPTION_A));
        userController.castVote(new Vote(pollId, user2.getUserId(), PollOption.OPTION_B));
        userController.castVote(new Vote(pollId, user3.getUserId(), PollOption.OPTION_A));
        userController.castVote(new Vote(pollId, user4.getUserId(), PollOption.OPTION_C));

        System.out.println("Poll Results for Poll ID " + pollId + ":");
        Map<PollOption, Integer> results = pollController.getPollResults(pollId);
        for (PollOption option : PollOption.values()) {
            int count = results.getOrDefault(option, 0);
            System.out.println(option + ": " + count);
        }

        System.out.println("\nUser 1 voted: " + userService.getUserVoteForPoll(user1.getUserId(), pollId));
    }
}
