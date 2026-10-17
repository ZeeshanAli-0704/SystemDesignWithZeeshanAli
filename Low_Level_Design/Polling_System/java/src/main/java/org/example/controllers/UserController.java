package org.example.controllers;

import org.example.models.Vote;
import org.example.service.UserService;

public class UserController {

        UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    };

    public void castVote(Vote vote) throws Exception {
        this.userService.castVote(vote);
    }
}
