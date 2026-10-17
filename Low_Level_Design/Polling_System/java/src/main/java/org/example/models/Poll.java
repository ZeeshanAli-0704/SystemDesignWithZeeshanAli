package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Poll {
    private final int pollId;
    private final  List<String> pollOptions;
    private final String PollQuestion;
    private final LocalDateTime createdAt;
    private final int adminId;

    public Poll(int pollId, List<String> pollOptions, String pollQuestion, LocalDateTime createdAt, int adminId) {
        this.pollId = pollId;
        this.pollOptions = pollOptions;
        PollQuestion = pollQuestion;
        this.createdAt = createdAt;
        this.adminId = adminId;
    }

    public int getPollId() {
        return pollId;
    }

    public List<String> getPollOptions() {
        return pollOptions;
    }

    public String getPollQuestion() {
        return PollQuestion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getAdminId() {
        return adminId;
    }
}
