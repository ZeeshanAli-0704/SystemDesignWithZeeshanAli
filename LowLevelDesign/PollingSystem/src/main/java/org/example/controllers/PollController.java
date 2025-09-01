package org.example.controllers;

import org.example.enums.PollOption;
import org.example.enums.UserType;
import org.example.exceptions.UnauthorizedException;
import org.example.models.Poll;
import org.example.models.User;
import org.example.service.PollService;

import java.util.List;
import java.util.Map;

public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    public List<Poll> getAllPolls(){
        return this.pollService.getAllPolls();
    }

    public List<Poll> getPollsByUserId(int id){
        return this.pollService.getPollsByUserId(id);
    }

    public Poll createPoll(List<String> pollOptions, String pollQuestion, User user) throws Exception{
        if (user.getUserType() != UserType.ADMIN) {
            throw new UnauthorizedException("Only admin can create poll");
        }
        return this.pollService.createPoll(pollOptions, pollQuestion, user.getUserId());
    }

    public Map<PollOption, Integer> getPollResults(int pollId) {
        return this.pollService.getPollResults(pollId);
    }
}
