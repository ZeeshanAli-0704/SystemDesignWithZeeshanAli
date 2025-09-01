package org.example.models;

import org.example.enums.PollOption;

public class Vote {
    private final int pollID;
    private final int userID;
    private final PollOption option;

    public Vote(int pollID, int userID, PollOption option) {
        this.pollID = pollID;
        this.userID = userID;
        this.option = option;
    };


    public int getPollID() {
        return pollID;
    }

    public int getUserID() {
        return userID;
    }


    public PollOption getOption() {
        return option;
    }

}
