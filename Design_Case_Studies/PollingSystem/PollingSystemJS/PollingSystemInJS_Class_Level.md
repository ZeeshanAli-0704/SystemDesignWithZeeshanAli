
### Table of Contents

1. [Problem Statement](#problem-statement)
2. [Assumptions](#assumptions)
3. [Requirements](#requirements)
   - [Create Polls](#create-polls)
   - [Manage Polls](#manage-polls)
   - [Vote in Polls](#vote-in-polls)
   - [View Poll Results](#view-poll-results)
   - [Poll Data](#poll-data)
4. [Implementation Details](#implementation-details)
   - [Functions/Methods](#functionsmethods)
     - [Create Poll](#create-poll)
     - [Update Poll](#update-poll)
     - [Delete Poll](#delete-poll)
     - [Vote In Poll](#vote-in-poll)
     - [View Poll Results](#view-poll-results)
5. [Data Models](#data-models)
   - [Poll](#poll)
   - [Vote](#vote)
6. [Implementation Details](#implementation-details)
7. [CODE](#code)


## Problem Statement
You are required to design and implement an online polling system. The system should allow users to create, manage, and participate in polls. Each poll consists of a question and multiple options for answers. Users can vote on polls and view the results.

## Assumptions :
- We are usin a Class based implementation
- We are not using additional Database to store this information
- As not DB & server is involved, no REST API
- simple Low Level approch on Polling System

## Requirements
### Create Polls:
- Users should be able to create a new poll with a question and multiple answer options.
- Each poll must have a unique identifier, a question, a list of options, and a timestamp of creation.

### Manage Polls:
- Users should be able to update the question or options of an existing poll.
- Users should be able to delete a poll.

### Vote in Polls:
- Users should be able to cast a vote for one of the options in a poll.
- Each user can only vote once per poll.

### View Poll Results:
- Users should be able to view the current results of a poll, including the number of votes for each option.

### Poll Data:
- Store polls, options, and votes in a way that allows efficient retrieval and updates.
- Ensure data integrity and consistency, especially when multiple users are voting simultaneously.

## Implementation Details

### Functions/Methods:
#### Create Poll
**createPoll** :
   - **Input**: `question` (string), `options` (array of strings)
   - **Output**: `pollId` (string), `message` (string)
   - **Example**: `createPoll("What is your favorite color?", ["Red", "Blue", "Green", "Yellow"])` returns `{"pollId": "123", "message": "Poll created successfully."}`

#### Update Poll
**updatePoll** :
   - **Input**: `pollId` (string), `question` (string), `options` (array of strings)
   - **Output**: `message` (string)
   - **Example**: `updatePoll("123", "Updated question?", ["Option1", "Option2"])` returns `{"message": "Poll updated successfully."}`

#### Delete Poll
**deletePoll** :
   - **Input**: `pollId` (string)
   - **Output**: `message` (string)
   - **Example**: `deletePoll("123")` returns `{"message": "Poll deleted successfully."}`

#### Vote In Poll
 **voteInPoll** :
   - **Input**: `pollId` (string), `userId` (string), `option` (string)
   - **Output**: `message` (string)
   - **Example**: `voteInPoll("123", "user1", "Option1")` returns `{"message": "Vote cast successfully."}`

#### View Poll Results 
**viewPollResults** :
   - **Input**: `pollId` (string)
   - **Output**: `pollId` (string), `question` (string), `results` (object with option keys and vote count values)
   - **Example**: `viewPollResults("123")` returns `{"pollId": "123", "question": "What is your favorite color?", "results": {"Red": 10, "Blue": 5, "Green": 3, "Yellow": 2}}`

## Data Models
- **Poll**: 
```json
{
  "pollId": "123",
  "question": "What is your favorite color?",
  "options": ["Red", "Blue", "Green", "Yellow"],
  "createdAt": "2024-07-11T00:00:00Z"
}
```

- **Vote**:
```json
{
  "pollId": "123",
  "userId": "user1",
  "option": "Red",
  "timestamp": "2024-07-11T01:00:00Z"
}
```

# Implementation Details

This code defines a basic polling system using JavaScript classes. It allows the creation, management, and voting in polls. Let's break down each part of the code:

### 1. **Class Definitions**

#### `Poll` Class
- **Purpose:** Represents an individual poll.
- **Constructor Parameters:**
  - `id`: A unique identifier for the poll.
  - `question`: The question being asked in the poll.
  - `options`: An array of possible options that users can vote on.
- **Properties:**
  - `pollId`: Stores the unique ID of the poll.
  - `question`: Stores the poll question.
  - `options`: Stores the array of options.
  - `createdAt`: Stores the creation date and time of the poll.

#### `Vote` Class
- **Purpose:** Represents an individual vote cast by a user.
- **Constructor Parameters:**
  - `pollId`: The ID of the poll the vote is associated with.
  - `userId`: The ID of the user who cast the vote.
  - `option`: The option that the user voted for.
- **Properties:**
  - `pollId`: Stores the poll ID for which the vote was cast.
  - `userId`: Stores the user ID of the voter.
  - `option`: Stores the option that the user voted for.
  - `timestamp`: Stores the date and time when the vote was cast.

### 2. **PollManager Class**

#### **Purpose:** Manages the entire polling system, including creating polls, managing votes, and viewing results.

#### **Constructor:**
- **Properties:**
  - `polls`: A `Map` that stores all polls, where the key is the poll ID and the value is the `Poll` object.
  - `pollResults`: A `Map` that stores the results of each poll, where the key is the poll ID and the value is another `Map` that tracks votes for each option.
  - `userVotes`: A `Map` that stores the votes by each user, where the key is the poll ID and the value is another `Map` that tracks whether a user has voted in that poll.

#### **Methods:**

- **`createPoll(question, options)`**
  - Generates a new poll with a unique ID.
  - Initializes the poll results, setting the vote count for each option to 0.
  - Stores the poll and returns the generated poll ID.

- **`updatePoll(pollId, question, options)`**
  - Updates the question and options for an existing poll.
  - Resets the poll results to 0 for the updated options.
  - Returns a success message if the poll is found, otherwise returns "Poll not found."

- **`deletePoll(pollId)`**
  - Deletes a poll by its ID.
  - Also removes associated poll results and user votes.
  - Returns a success message if the poll is found, otherwise returns "Poll not found."

- **`voteInPoll(pollId, userId, option)`**
  - Allows a user to cast a vote in a specific poll.
  - Ensures a user can only vote once per poll.
  - Updates the vote count for the selected option if valid.
  - Returns appropriate messages depending on whether the vote was successful, the user had already voted, or the poll or option was invalid.

- **`viewPollResults(pollId)`**
  - Returns the results of a specific poll in an array format, where each entry is a tuple of the option and its vote count.
  - Returns "Poll not found" if the poll doesn't exist.

### 3. **Example Usage**
- **Poll Creation:** A poll is created asking, "What is your favorite color?" with options ["Red", "Blue", "Green", "Yellow"]. The poll ID is generated as "1".
- **Voting:** Multiple users vote in the poll, and the system ensures users can only vote once.
- **Viewing Results:** The poll results are displayed, showing the vote counts for each option.
- **Updating the Poll:** The poll's question and options are updated, resetting the results.
- **Voting in Updated Poll:** A user votes in the updated poll, and the results are displayed.
- **Poll Deletion:** The poll is deleted, and an attempt to view the results afterward confirms the poll no longer exists.

This implementation provides a basic but functional polling system, handling common scenarios such as poll creation, updating, voting, and deletion.

# CODE

```js
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

```

**Note**: Please follow for more detail & Enchanced version of this article with DB, API & other system Design Concept.
[Low-Level Design: Polling System - API's Using Nodejs & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

[Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
