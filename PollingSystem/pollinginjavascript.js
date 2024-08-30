class Poll {
    constructor(id, question, options) {
        this.pollId = id;
        this.question = question;
        this.options = options;
        this.createdAt = new Date();
    }
}

class Vote {
    constructor(pollId, userId, option) {
        this.pollId = pollId;
        this.userId = userId;
        this.option = option;
        this.timestamp = new Date();
    }
}

class PollManager {
    constructor() {
        this.polls = new Map();
        this.pollResults = new Map();
        this.userVotes = new Map();
    }

    createPoll(question, options) {
        const pollId = (this.polls.size + 1).toString();
        const poll = new Poll(pollId, question, options);
        this.polls.set(pollId, poll);

        const result = new Map();
        options.forEach(option => result.set(option, 0));
        this.pollResults.set(pollId, result);

        return pollId;
    }

    updatePoll(pollId, question, options) {
        const poll = this.polls.get(pollId);
        if (poll) {
            poll.question = question;
            poll.options = options;

            // Update results for the new options
            const result = new Map();
            options.forEach(option => result.set(option, 0));
            this.pollResults.set(pollId, result);

            return "Poll updated successfully.";
        }
        return "Poll not found.";
    }

    deletePoll(pollId) {
        if (this.polls.delete(pollId)) {
            this.pollResults.delete(pollId);
            this.userVotes.delete(pollId);
            return "Poll deleted successfully.";
        }
        return "Poll not found.";
    }

    voteInPoll(pollId, userId, option) {
        const poll = this.polls.get(pollId);
        if (poll) {
            if (!this.userVotes.has(pollId)) {
                this.userVotes.set(pollId, new Map());
            }

            const userVote = this.userVotes.get(pollId);
            if (userVote.get(userId)) {
                return "User has already voted.";
            }

            const result = this.pollResults.get(pollId);
            if (result.has(option)) {
                result.set(option, result.get(option) + 1);
                userVote.set(userId, true);
                return "Vote cast successfully.";
            } else {
                return "Invalid option.";
            }
        }
        return "Poll not found.";
    }

    viewPollResults(pollId) {
        const results = this.pollResults.get(pollId);
        if (results) {
            return Array.from(results.entries());
        }
        return "Poll not found.";
    }
}

// Example usage
const pollManager = new PollManager();

// Creating a poll
const pollId = pollManager.createPoll("What is your favorite color?", ["Red", "Blue", "Green", "Yellow"]);
console.log("Poll created with ID:", pollId);

// Voting in the poll
let voteMessage = pollManager.voteInPoll(pollId, "user1", "Red");
console.log(voteMessage);

voteMessage = pollManager.voteInPoll(pollId, "user2", "Blue");
console.log(voteMessage);

voteMessage = pollManager.voteInPoll(pollId, "user1", "Green");
console.log(voteMessage); // Should inform the user has already voted

// Viewing poll results
let results = pollManager.viewPollResults(pollId);
console.log("Poll results for poll ID", pollId, ":", results);

// Updating the poll
let updateMessage = pollManager.updatePoll(pollId, "What is your favorite primary color?", ["Red", "Blue", "Yellow"]);
console.log(updateMessage);

// Voting in the updated poll
voteMessage = pollManager.voteInPoll(pollId, "user3", "Yellow");
console.log(voteMessage);

// Viewing updated poll results
results = pollManager.viewPollResults(pollId);
console.log("Updated poll results for poll ID", pollId, ":", results);

// Deleting the poll
let deleteMessage = pollManager.deletePoll(pollId);
console.log(deleteMessage);

// Attempting to view results of a deleted poll
results = pollManager.viewPollResults(pollId);
if (typeof results === "string") {
    console.log(results);
}


// Response
// Poll created with ID: 1
// Vote cast successfully.
// Vote cast successfully.
// User has already voted.
// Poll results for poll ID 1 : [['Red', 1], ['Blue', 1], ['Green', 0], ['Yellow', 0]]
// Poll updated successfully.
// Vote cast successfully.
// Updated poll results for poll ID 1 : [['Red', 0], ['Blue', 0], ['Yellow', 1]]
// Poll deleted successfully.
// Poll not found.