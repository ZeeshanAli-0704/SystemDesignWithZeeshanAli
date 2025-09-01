
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

[Low-Level Design: Polling System - In JAVA](
https://dev.to/zeeshanali0704/lld-building-a-polling-system-in-java-with-repository-pattern-17h)



## Table of Contents
1. [Database Setup](#database-setup)
   - [MySQL Database Schema](#mysql-database-schema)
   - [ERD for the Polling System](#erd-for-the-polling-system)
2. [Backend Setup](#backend-setup)
   - [Step 1: Initialize the Project](#step-1-initialize-the-project)
   - [Step 2: Project Structure](#step-2-project-structure)
3. [API Implementation](#api-implementation)
   - [Step 1: Database Connection (`db/db.js`)](#step-1-database-connection)
   - [Step 2: Environment Variables (`.env`)](#step-2-environment-variables)
   - [Step 3: Poll Controller (`controllers/pollController.js`)](#step-3-poll-controller)
   - [Step 4: Poll Routes (`routes/pollRoutes.js`)](#step-4-poll-routes)
   - [Step 5: Server Entry Point (`index.js`)](#step-5-server-entry-point)
4. [Error Handling](#error-handling)
5. [Testing](#testing)
6. [Conclusion](#conclusion)


Please refer to the article [Polling System Basic Low-Level Design - I](https://dev.to/zeeshanali0704/polling-2hc8) 

Let's break down the entire process into detailed steps, including the database setup, API implementation using Node.js with Express, and interaction with MySQL. We will cover:

### Database Setup

First, we'll define the schema for the MySQL database and create the necessary tables.

#### MySQL Database Schema

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/4z4foniwytyxjd2xy1e0.png)

```sql
CREATE DATABASE polling_system;

USE polling_system;

CREATE TABLE polls (
    poll_id INT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE
);

CREATE TABLE votes (
    vote_id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    user_id VARCHAR(255) NOT NULL,
    option_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES options(option_id) ON DELETE CASCADE
);
```

- **polls table**: Stores poll information with a unique identifier, question, and creation timestamp.

- **options table**: Stores the options associated with a poll, linked via `poll_id`.

- **votes table**: Records each vote, linking to the poll, option, and user.
#### ERD for the Polling System

**Entities**:
1. **Polls**: Represents the poll itself, with attributes like `poll_id` and `question`.
2. **Options**: Represents the options available for each poll, with attributes like `option_id` and `option_text`.
3. **Votes**: Represents the votes cast by users, with attributes like `vote_id`, `user_id`, and timestamps.

**Relationships**:
1. **One-to-Many** between `Polls` and `Options`: Each poll can have multiple options.
2. **Many-to-One** between `Votes` and `Options`: Each vote is associated with one option.
3. **Many-to-One** between `Votes` and `Polls`: Each vote is linked to a specific poll.

Here’s a description of the ERD:

1. **Polls Table**:
   - **poll_id** (Primary Key)
   - **question**
   - **created_at**

2. **Options Table**:
   - **option_id** (Primary Key)
   - **poll_id** (Foreign Key referencing `polls.poll_id`)
   - **option_text**

3. **Votes Table**:
   - **vote_id** (Primary Key)
   - **poll_id** (Foreign Key referencing `polls.poll_id`)
   - **option_id** (Foreign Key referencing `options.option_id`)
   - **user_id**
   - **created_at**

The relationships would be represented with lines between the entities:

- **Polls** → **Options**: One `poll` can have many `options`.
- **Options** → **Votes**: One `option` can have many `votes`.
- **Polls** → **Votes**: One `poll` can have many `votes`.

### Backend Setup

Let's set up a Node.js project using Express and MySQL.

#### Step 1: Initialize the Project

```bash

mkdir polling-system
cd polling-system
npm init -y
npm install express mysql2 dotenv

```

- **express**: A web framework for Node.js.
- **mysql2**: A MySQL client for Node.js.
- **dotenv**: For managing environment variables.

#### Step 2: Project Structure

Create a basic structure for the project:

```
polling-system/
│
├── .env
├── index.js
├── db/
│   └── db.js
├── routes/
│   └── pollRoutes.js
└── controllers/
    └── pollController.js
```

### API Implementation

#### Step 1: Database Connection
 File - `db/db.js`

```javascript

const mysql = require('mysql2/promise');
require('dotenv').config();

const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

module.exports = pool;

```

#### Step 2: Environment Variables 
File - `.env`

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=polling_system
PORT=3000
```

#### Step 3: Poll Controller
File - `controllers/pollController.js`

This file will implement all the necessary CRUD operations for the polling system.

```javascript
const pool = require('../db/db');

// Create Poll
exports.createPoll = async (req, res) => {
    const { question, options } = req.body;

    if (!question || !options || !Array.isArray(options) || options.length < 2) {
        return res.status(400).json({ message: "Invalid input data. Question and at least two options are required." });
    }

    try {
        const connection = await pool.getConnection();
        await connection.beginTransaction();

        const [result] = await connection.execute(
            'INSERT INTO polls (question) VALUES (?)',
            [question]
        );

        const pollId = result.insertId;

        const optionQueries = options.map(option => {
            return connection.execute(
                'INSERT INTO options (poll_id, option_text) VALUES (?, ?)',
                [pollId, option]
            );
        });

        await Promise.all(optionQueries);

        await connection.commit();
        connection.release();

        res.status(201).json({ pollId, message: "Poll created successfully." });

    } catch (error) {
        console.error("Error creating poll:", error.message);
        res.status(500).json({ message: "Error creating poll." });
    }
};

// Update Poll
exports.updatePoll = async (req, res) => {
    const { pollId } = req.params;
    const { question, options } = req.body;

    if (!pollId || !question || !options || !Array.isArray(options) || options.length < 2) {
        return res.status(400).json({ message: "Invalid input data. Question and at least two options are required." });
    }

    try {
        const connection = await pool.getConnection();
        await connection.beginTransaction();

        const [pollResult] = await connection.execute(
            'UPDATE polls SET question = ? WHERE poll_id = ?',
            [question, pollId]
        );

        if (pollResult.affectedRows === 0) {
            await connection.rollback();
            connection.release();
            return res.status(404).json({ message: "Poll not found." });
        }

        await connection.execute('DELETE FROM options WHERE poll_id = ?', [pollId]);

        const optionQueries = options.map(option => {
            return connection.execute(
                'INSERT INTO options (poll_id, option_text) VALUES (?, ?)',
                [pollId, option]
            );
        });

        await Promise.all(optionQueries);

        await connection.commit();
        connection.release();

        res.status(200).json({ message: "Poll updated successfully." });

    } catch (error) {
        console.error("Error updating poll:", error.message);
        res.status(500).json({ message: "Error updating poll." });
    }
};

// Delete Poll
exports.deletePoll = async (req, res) => {
    const { pollId } = req.params;

    try {
        const connection = await pool.getConnection();

        const [result] = await connection.execute(
            'DELETE FROM polls WHERE poll_id = ?',
            [pollId]
        );

        connection.release();

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Poll not found." });
        }

        res.status(200).json({ message: "Poll deleted successfully." });

    } catch (error) {
        console.error("Error deleting poll:", error.message);
        res.status(500).json({ message: "Error deleting poll." });
    }
};

// Vote in Poll
exports.voteInPoll = async (req, res) => {
    const { pollId } = req.params;
    const { userId, option } = req.body;

    if (!userId || !option) {
        return res.status(400).json({ message: "User ID and option are required." });
    }

    try {
        const connection = await pool.getConnection();

        const [userVote] = await connection.execute(
            'SELECT * FROM votes WHERE poll_id = ? AND user_id = ?',
            [pollId, userId]
        );

        if (userVote.length > 0) {
            connection.release();
            return res.status(400).json({ message: "User has already voted." });
        }

        const [optionResult] = await connection.execute(
            'SELECT option_id FROM options WHERE poll_id = ? AND option_text = ?',
            [pollId, option]
        );

        if (optionResult.length === 0) {
            connection.release();
            return res.status(404).json({ message: "Option not found." });
        }

        const optionId = optionResult[0].option_id;

        await connection.execute(
            'INSERT INTO votes (poll_id, user_id, option_id) VALUES (?, ?, ?)',
            [pollId, userId, optionId]
        );

        connection.release();

        res.status(200).json({ message: "Vote cast successfully." });

    } catch (error) {
        console.error("Error casting vote:", error.message);
        res.status(500).json({ message: "Error casting vote." });
    }
};

// View Poll Results
exports.viewPollResults = async (req, res) => {
    const { pollId } = req.params;

    try {
        const connection = await pool.getConnection();

        const [poll] = await connection.execute(
            'SELECT * FROM polls WHERE poll_id = ?',
            [pollId]
        );

        if (poll.length === 0) {
            connection.release();
            return res.status(404).json({ message: "Poll not found." });
        }

        const [options] = await connection.execute(
            'SELECT option_text, COUNT(votes.option_id) as vote_count FROM options ' +
            'LEFT JOIN votes ON options.option_id = votes.option_id ' +
            'WHERE options.poll_id = ? GROUP BY options.option_id',
            [pollId]
        );

        connection.release();

        res.status(200).json({
            pollId: poll[0].poll_id,
            question: poll[0].question,
            results: options.reduce((acc, option) => {
                acc[option.option_text] = option.vote_count;
                return acc;
            }, {})
        });

    } catch (error) {
        console.error("Error viewing poll results:", error.message);
        res.status(500).json({ message: "Error viewing poll results." });
    }
};
```

#### Step 4: Poll Routes
File - `routes/pollRoutes.js`
Define the routes for each API endpoint:

```javascript
const express = require('express');
const router = express.Router();
const pollController = require('../controllers/pollController');

//

 Routes
router.post('/polls', pollController.createPoll);
router.put('/polls/:pollId', pollController.updatePoll);
router.delete('/polls/:pollId', pollController.deletePoll);
router.post('/polls/:pollId/vote', pollController.voteInPoll);
router.get('/polls/:pollId/results', pollController.viewPollResults);

module.exports = router;
```

#### Step 5: Server Entry Point
File - `index.js`
Finally, set up the server:

```javascript
const express = require('express');
const pollRoutes = require('./routes/pollRoutes');
require('dotenv').config();

const app = express();
app.use(express.json());

// Routes
app.use('/api', pollRoutes);

// Error Handling Middleware
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ message: "Internal server error" });
});

// Start Server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
```

### Error Handling

Each method includes error handling for common issues like invalid input, duplicate votes, missing poll or option, and server errors.

- **Input Validation**: Checks are performed to ensure that the inputs are valid, such as checking if the required fields are present and properly formatted.
- **Transaction Management**: For operations involving multiple queries (e.g., creating or updating polls), transactions are used to ensure consistency.

### Testing

Test each endpoint using tools like Postman or curl.

- **Create Poll**: POST `/api/polls` with a JSON body containing `question` and an array of `options`.
- **Update Poll**: PUT `/api/polls/:pollId` with updated `question` and `options`.
- **Delete Poll**: DELETE `/api/polls/:pollId`.
- **Vote in Poll**: POST `/api/polls/:pollId/vote` with `userId` and `option`.
- **View Poll Results**: GET `/api/polls/:pollId/results`.

### Conclusion

This is a comprehensive modular implementation of an online polling system using Node.js, Express, and MySQL. It handles the basic CRUD operations and ensures data consistency with transactions. It also includes basic error handling to make the API more robust and user-friendly.

Previous
Please refer to the article [Polling System Basic Low-Level Design - I](https://dev.to/zeeshanali0704/low-level-design-polling-system-basic-2kl5) 

Next:
[Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

Next:
[Low-Level Design: Polling System - API's Using Nodejs & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

Next:
[Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

[Low-Level Design: Polling System - In JAVA](
https://dev.to/zeeshanali0704/lld-building-a-polling-system-in-java-with-repository-pattern-17h)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli





**Table of contents**

[Case 1 - Handle Versioning for Update](#case-1)
[Case 2 - PollID to be as UUID & not Primary Key](#case-2)
[Case 3 - Empty or Invalid Options](#case-3)
[Case 4 - Duplicate Options](#case-4)
[Case 5 - Question Length Limit](#case-5)
[Case 6 - Poll Expiration](#case-6)


Please refer to the following articles first:

1. [Low-Level Design: Polling System: Basic](https://dev.to/zeeshanali0704/polling-2hc8)
  
2. [Low-Level Design: Polling System - Using Node.js & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

# Edge Cases Handling

##  Case 1
**To manage updates to a poll's question and options while retaining the previous details associated with the same poll ID, you can implement a versioning system. This approach allows you to keep track of the historical data for each poll, ensuring that old details are preserved even after updates.**


### Step 1: Database Schema Changes

1. **Update the Polls Table**
   - Add a `current_version_id` column to the `polls` table to track the latest version of the poll.

2. **Create the Poll Versions Table**
   - Create a new table to store historical versions of polls.

#### Updated Database Schema

```sql
CREATE DATABASE polling_system;

USE polling_system;

CREATE TABLE polls (
    poll_id INT AUTO_INCREMENT PRIMARY KEY,
    current_version_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (current_version_id) REFERENCES poll_versions(version_id) ON DELETE SET NULL
);

CREATE TABLE poll_versions (
    version_id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    question VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE
);

CREATE TABLE options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE
);

CREATE TABLE votes (
    vote_id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    user_id VARCHAR(255) NOT NULL,
    option_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES options(option_id) ON DELETE CASCADE
);
```

### Step 2: API Implementation Changes

#### Update the Poll Controller

Modify the `updatePoll` method to check if the question has changed before creating a new version.

##### File: `controllers/pollController.js`

```javascript
const pool = require('../db/db');

// Create Poll
exports.createPoll = async (req, res) => {
    const { question, options } = req.body;

    if (!question || !options || !Array.isArray(options) || options.length < 2) {
        return res.status(400).json({ message: "Invalid input data. Question and at least two options are required." });
    }

    try {
        const connection = await pool.getConnection();
        await connection.beginTransaction();

        const [result] = await connection.execute(
            'INSERT INTO polls (current_version_id) VALUES (NULL)'
        );

        const pollId = result.insertId;

        const [versionResult] = await connection.execute(
            'INSERT INTO poll_versions (poll_id, question) VALUES (?, ?)',
            [pollId, question]
        );

        const versionId = versionResult.insertId;

        // Update the current version in the polls table
        await connection.execute(
            'UPDATE polls SET current_version_id = ? WHERE poll_id = ?',
            [versionId, pollId]
        );

        const optionQueries = options.map(option => {
            return connection.execute(
                'INSERT INTO options (poll_id, option_text) VALUES (?, ?)',
                [pollId, option]
            );
        });

        await Promise.all(optionQueries);

        await connection.commit();
        connection.release();

        res.status(201).json({ pollId, message: "Poll created successfully." });

    } catch (error) {
        console.error("Error creating poll:", error.message);
        res.status(500).json({ message: "Error creating poll." });
    }
};

// Update Poll
exports.updatePoll = async (req, res) => {
    const { pollId } = req.params;
    const { question, options } = req.body;

    if (!pollId || !options || !Array.isArray(options) || options.length < 2) {
        return res.status(400).json({ message: "Invalid input data. At least two options are required." });
    }

    try {
        const connection = await pool.getConnection();
        await connection.beginTransaction();

        // Fetch the existing poll
        const [existingPoll] = await connection.execute(
            'SELECT question FROM poll_versions WHERE poll_id = (SELECT current_version_id FROM polls WHERE poll_id = ?)',
            [pollId]
        );

        if (existingPoll.length === 0) {
            await connection.rollback();
            connection.release();
            return res.status(404).json({ message: "Poll not found." });
        }

        const currentQuestion = existingPoll[0].question;

        // Check if the question has changed
        if (currentQuestion !== question) {
            // Create a new version since the question has changed
            const [versionResult] = await connection.execute(
                'INSERT INTO poll_versions (poll_id, question) VALUES (?, ?)',
                [pollId, question]
            );

            const versionId = versionResult.insertId;

            // Update the current version in the polls table
            await connection.execute(
                'UPDATE polls SET current_version_id = ? WHERE poll_id = ?',
                [versionId, pollId]
            );
        }

        // Remove old options and insert new ones
        await connection.execute('DELETE FROM options WHERE poll_id = ?', [pollId]);

        const optionQueries = options.map(option => {
            return connection.execute(
                'INSERT INTO options (poll_id, option_text) VALUES (?, ?)',
                [pollId, option]
            );
        });

        await Promise.all(optionQueries);

        await connection.commit();
        connection.release();

        res.status(200).json({ message: "Poll updated successfully." });

    } catch (error) {
        console.error("Error updating poll:", error.message);
        await connection.rollback();
        res.status(500).json({ message: "Error updating poll." });
    }
};

// Delete Poll
exports.deletePoll = async (req, res) => {
    const { pollId } = req.params;

    try {
        const connection = await pool.getConnection();

        const [result] = await connection.execute(
            'DELETE FROM polls WHERE poll_id = ?',
            [pollId]
        );

        connection.release();

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Poll not found." });
        }

        res.status(200).json({ message: "Poll deleted successfully." });

    } catch (error) {
        console.error("Error deleting poll:", error.message);
        res.status(500).json({ message: "Error deleting poll." });
    }
};

// Vote in Poll
exports.voteInPoll = async (req, res) => {
    const { pollId } = req.params;
    const { userId, option } = req.body;

    if (!userId || !option) {
        return res.status(400).json({ message: "User ID and option are required." });
    }

    try {
        const connection = await pool.getConnection();

        const [userVote] = await connection.execute(
            'SELECT * FROM votes WHERE poll_id = ? AND user_id = ?',
            [pollId, userId]
        );

        if (userVote.length > 0) {
            connection.release();
            return res.status(400).json({ message: "User has already voted." });
        }

        const [optionResult] = await connection.execute(
            'SELECT option_id FROM options WHERE poll_id = ? AND option_text = ?',
            [pollId, option]
        );

        if (optionResult.length === 0) {
            connection.release();
            return res.status(404).json({ message: "Option not found." });
        }

        const optionId = optionResult[0].option_id;

        await connection.execute(
            'INSERT INTO votes (poll_id, user_id, option_id) VALUES (?, ?, ?)',
            [pollId, userId, optionId]
        );

        connection.release();

        res.status(200).json({ message: "Vote cast successfully." });

    } catch (error) {
        console.error("Error casting vote:", error.message);
        res.status(500).json({ message: "Error casting vote." });
    }
};

// View Poll Results
exports.viewPollResults = async (req, res) => {
    const { pollId } = req.params;

    try {
        const connection = await pool.getConnection();

        const [poll] = await connection.execute(
            'SELECT * FROM polls WHERE poll_id = ?',
            [pollId]
        );

        if (poll.length === 0) {
            connection.release();
            return res.status(404).json({ message: "Poll not found." });
        }

        const [options] = await connection.execute(
            'SELECT option_text, COUNT(votes.option_id) as vote_count FROM options ' +
            'LEFT JOIN votes ON options.option_id = votes.option_id ' +
            'WHERE options.poll_id = ? GROUP BY options.option_id',
            [pollId]
        );

        connection.release();

        res.status(200).json({
            pollId: poll[0].poll_id,
            question: poll[0].question,
            results: options.reduce((acc, option) => {
                acc[option.option_text] = option.vote_count;
                return acc;
            }, {})
        });

    } catch (error) {
        console.error("Error viewing poll results:", error.message);
        res.status(500).json({ message: "Error viewing poll results." });
    }
};
```

### Step 3: Update Poll Routes

Ensure the routes are defined properly in your `pollRoutes.js`.

##### File: `routes/pollRoutes.js`

```javascript
const express = require('express');
const router = express.Router();
const pollController = require('../controllers/pollController');

// Routes
router.post('/polls', pollController.createPoll);
router.put('/polls/:pollId', pollController.updatePoll);
router.delete('/polls/:pollId', pollController.deletePoll);
router.post('/polls/:pollId/vote', pollController.voteInPoll);
router.get('/polls/:pollId/results', pollController.viewPollResults);

module.exports = router;
```

### Summary of Changes

1. **Database:**
   - Updated the `polls` table to include `current_version_id`.
   - Created the `poll_versions` table for tracking question versions.
   - The `options` and `votes` tables remain unchanged.

2. **API:**
   - Created a new `createPoll` method to initialize polls and versions.
   - Updated the `updatePoll` method to check for question changes before creating a new version.
   - Added methods for voting and viewing poll results.

3. **Routing:**
   - Ensured all necessary routes are defined to handle poll creation, updates, voting, and results.


---

##  Case 2
**To handle a scenario where the pollId is required to be a UUID (Universally Unique Identifier).**


Here are the steps to implement UUIDs for the`pollId` in your polling system without providing code:

### Steps to Implement UUID for Poll ID

1. ** Database Schema Update:**
    - Modify the`polls`, `poll_versions`, `options`, and`votes` tables to use `CHAR(36)` for `poll_id` instead of an integer.
   - Create a new `poll_versions` table to store historical versions of poll questions and options linked by the UUID.

2. ** UUID Generation:**
    - Decide on a method for generating UUIDs.You can use a library or built -in functions in your application environment to create UUIDs.

3. ** Create Poll Logic:**
    - When creating a new poll, generate a UUID and use it as the `poll_id`.
   - Insert the new poll record into the `polls` table.
   - Insert the initial question into the `poll_versions` table and link it with the generated UUID.

4. ** Update Poll Logic:**
    - When updating a poll:
- Check if the question has changed.
     - If the question has changed, create a new version entry in the`poll_versions` table to store the old question and options.
     - Update the `polls` table with the new question and options as necessary.

5. ** Voting Logic:**
    - Update the voting mechanism to ensure that it uses the UUID as `poll_id`.
- Validate that the UUID provided in the vote request exists in the`polls` table.

6. ** API Updates:**
    - Modify API endpoints to accept and return UUIDs for `poll_id`.
   - Ensure that all API operations(create, update, delete, vote) reference the UUID format consistently.

7. ** Testing:**
    - Thoroughly test the application to ensure that UUIDs are handled correctly in all scenarios(creation, updates, voting, and retrieving poll results).

8. ** Documentation:**
    - Update your API documentation to reflect the changes in the`poll_id` format and any new behaviors related to versioning and UUID usage.

By following these steps, you can successfully implement UUIDs for `pollId` in your polling system while ensuring data integrity and historical tracking.

---

## Case 3

**Empty or Invalid Options**

**Validation Approach:**
- **API Input Validation:** Implement checks in your API endpoints to verify that the options provided in the request body are not empty and meet specific criteria (e.g., no special characters if not allowed).
- **Feedback Mechanism:** Provide clear error messages to the user if the options are invalid or empty, guiding them to correct their input.

---

## Case 4

**Duplicate Options**

**Uniqueness Check:**
- **Pre-Insert Validation:** Before adding options to a poll, check the existing options in the database for duplicates. This can be done by querying the `options` table using the poll ID and comparing it against the new options.
- **User Feedback:** If a duplicate option is detected, return a meaningful error message to inform the user which options are duplicates, allowing them to modify their input accordingly.

---

## Case 5

**Question Length Limit**

**Character Limitation:**
- **API Validation:** Set a maximum character limit for poll questions and options within your API. This can be done by checking the length of the question and each option during the creation and update processes.
- **User Interface Feedback:** Implement client-side validation to provide instant feedback to users when they exceed the character limit while typing, enhancing the user experience.

---

## Case 6

**Poll Expiration**

**Expiration Mechanism:**
- **Timestamp Management:** Add a timestamp field to the `polls` table to record when each poll is created and optionally another field for the expiration date.
- **Scheduled Checks:** Implement a background job or cron task that periodically checks for expired polls and marks them as inactive in the database. This can also include preventing votes on expired polls.
- **User Notifications:** Optionally, notify poll creators and participants of impending expiration dates, allowing them to engage with the poll before it becomes inactive.

---


Please refer to the following articles first:

1. [Low-Level Design: Polling System: Basic](https://dev.to/zeeshanali0704/low-level-design-polling-system-basic-2kl5)
  
2. [Low-Level Design: Polling System - Using Node.js & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

3. [Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

4. [Low-Level Design: Polling System - In JAVA](
https://dev.to/zeeshanali0704/lld-building-a-polling-system-in-java-with-repository-pattern-17h)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli



# 🗳️ Building a Polling System in Java with Repository Pattern (In-Memory)

---

## Table of Contents

* [Introduction](#introduction)
* [Problem Statement](#problem-statement)
* [System Requirements](#system-requirements)
* [Design Approach](#design-approach)

  * [Repository Pattern](#repository-pattern)
  * [Entities](#entities)
* [ER Diagram](#er-diagram)
* [How Your Final Code Works](#how-your-final-code-works)
* [Implementation](#implementation)

  * [Enums](#enums)
  * [Models](#models)
  * [Repositories](#repositories)
  * [Service Layer](#service-layer)
  * [Controllers](#controllers)
  * [Main Runner](#main-runner)
* [Sample Execution](#sample-execution)
* [Future Improvements](#future-improvements)
* [Key Takeaways](#key-takeaways)

---

## 📌 Introduction

Polls and voting systems are everywhere — from **online classrooms** to **event feedback apps**.

In this blog, we’ll design and implement a **Java-based Polling System** that follows the **Repository Pattern** with an **in-memory store** (no database).

This project is a great way to practice:

* Java OOP principles
* Layered architecture & separation of concerns
* Defensive programming & validations

---

## ❓ Problem Statement

We want to design a **polling platform** where:

* **Admins** can create polls with multiple options.
* **Users** can vote on polls.
* Results can be fetched per poll.

### Constraints:

* A poll must have **at least 2 unique, non-empty options**.
* Options should be **case-insensitive** (`Yes` vs `yes` → same).
* Only **admins** can create polls.

---

## ✅ System Requirements

* Create Polls (Admin only)
* View Polls (per Admin, or all)
* Record Votes (User action)
* View Results (tally votes)

---

## 🏗️ Design Approach

### 🔹 Repository Pattern

We’ll use the **Repository Pattern** to separate persistence from business logic.

Repositories:

* **UserRepository** → manages users
* **PollRepository** → manages polls
* **VoteRepository** → manages votes

Currently, they use **in-memory storage (ConcurrentHashMap/List)**, but can later be swapped with a database.

### 🔹 Entities

1. **User** → `id`, `name`, `type` (ADMIN / USER)
2. **Poll** → `id`, `question`, `options`, `createdBy`
3. **Vote** → `pollId`, `userId`, `selectedOption`

---

## 🗂️ ER Diagram

If we extend to a database, the schema looks like this:

```
┌───────────────┐        ┌───────────────┐        ┌─────────────┐
│    USERS      │        │    POLLS      │        │    VOTES    │
├───────────────┤        ├───────────────┤        ├─────────────┤
│ user_id (PK)  │◄───────┤ created_by (FK)│       │ vote_id (PK)│
│ name          │        │ poll_id (PK)   │──────►│ poll_id (FK)│
│ type (ADMIN/USER)│     │ question       │       │ user_id (FK)│
└───────────────┘        │ options[]      │       │ option      │
                         └───────────────┘       └─────────────┘
```

---
[🔼 Back to Top](#table-of-contents)


## How Your Final Code Works

The final implementation of the polling system follows the **Repository Pattern** using **in-memory storage**. This design ensures a clean separation of concerns between **controllers**, **repositories**, and **models**.

### Execution Flow

1. **User Creation**

   * `UserController` calls `UserRepository.addUser(user)`.
   * Users are stored in-memory inside a `Map<Integer, User>`.

2. **Poll Creation**

   * `PollController` invokes `PollRepository.addPoll(poll)`.
   * Polls are maintained in-memory inside a `Map<Integer, Poll>`.

3. **Voting**

   * When `PollController.vote(userId, pollId, option)` is called:

     * The system retrieves the `User` from `UserRepository`.
     * Retrieves the `Poll` from `PollRepository`.
     * Creates a `Vote` object and adds it to the poll.
     * Prevents duplicate votes from the same user.

4. **Fetching Results**

   * `PollController.getResults(pollId)` aggregates votes per `PollOption`.
   * Returns results as a map of `Option → Count`.

---

### Final Code Walkthrough (In-Memory Repository)

```java
// org/example/repository/UserRepository.java
package org.example.repository;

import org.example.models.User;
import java.util.*;

public class UserRepository {
    private final Map<Integer, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public User getUser(int id) {
        return users.get(id);
    }
}

// org/example/repository/PollRepository.java
package org.example.repository;

import org.example.models.Poll;
import java.util.*;

public class PollRepository {
    private final Map<Integer, Poll> polls = new HashMap<>();

    public void addPoll(Poll poll) {
        polls.put(poll.getId(), poll);
    }

    public Poll getPoll(int id) {
        return polls.get(id);
    }
}

// org/example/controllers/UserController.java
package org.example.controllers;

import org.example.models.User;
import org.example.enums.UserType;
import org.example.repository.UserRepository;

public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(int id, String name, UserType type) {
        User user = new User(id, name, type);
        userRepository.addUser(user);
    }
}

// org/example/controllers/PollController.java
package org.example.controllers;

import org.example.enums.PollOption;
import org.example.models.*;
import org.example.repository.*;

import java.util.*;

public class PollController {
    private final PollRepository pollRepository;
    private final UserRepository userRepository;

    public PollController(PollRepository pollRepository, UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
    }

    public void createPoll(int id, String question, List<PollOption> options) {
        Poll poll = new Poll(id, question, options);
        pollRepository.addPoll(poll);
    }

    public void vote(int userId, int pollId, PollOption option) {
        User user = userRepository.getUser(userId);
        Poll poll = pollRepository.getPoll(pollId);

        if (user != null && poll != null) {
            Vote vote = new Vote(user, option);
            poll.addVote(vote);
        }
    }

    public Map<PollOption, Long> getResults(int pollId) {
        Poll poll = pollRepository.getPoll(pollId);
        return poll.getVotes().stream()
                   .collect(Collectors.groupingBy(Vote::getOption, Collectors.counting()));
    }
}
```
---

[🔼 Back to Top](#table-of-contents)

---

## 💻 Implementation

We’ll keep a **layered structure**:

```
Main → Controller → Service → Repository → Models
```

---

### 📁 Enums

#### `UserType.java`

```java
package org.example.polling.enums;

public enum UserType {
    ADMIN,
    USER
}
```

#### `PollOption.java`

```java
package org.example.polling.enums;

public enum PollOption {
    OPTION_A,
    OPTION_B,
    OPTION_C,
    OPTION_D
}
```

---

### 📁 Models

#### `User.java`

```java
package org.example.polling.entities;

import org.example.polling.enums.UserType;

public class User {
    private final int userId;
    private final String name;
    private final UserType userType;

    public User(int userId, String name, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.userType = userType;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public UserType getUserType() { return userType; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", userType=" + userType +
                '}';
    }
}
```

#### `Poll.java`

```java
package org.example.polling.entities;

import org.example.polling.enums.PollOption;

import java.time.LocalDateTime;
import java.util.Map;

public class Poll {
    private final int pollId;
    private final String question;
    private final Map<PollOption, String> options;
    private final User createdBy;
    private final LocalDateTime createdAt;

    public Poll(int pollId, String question, Map<PollOption, String> options, User createdBy) {
        this.pollId = pollId;
        this.question = question;
        this.options = options;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public int getPollId() { return pollId; }
    public String getQuestion() { return question; }
    public Map<PollOption, String> getOptions() { return options; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```
---

[🔼 Back to Top](#table-of-contents)

---

#### `Vote.java`

```java
package org.example.polling.entities;

import org.example.polling.enums.PollOption;

public class Vote {
    private final int pollId;
    private final int userId;
    private final PollOption selectedOption;

    public Vote(int pollId, int userId, PollOption selectedOption) {
        this.pollId = pollId;
        this.userId = userId;
        this.selectedOption = selectedOption;
    }

    public int getPollId() { return pollId; }
    public int getUserId() { return userId; }
    public PollOption getSelectedOption() { return selectedOption; }
}
```

---

[🔼 Back to Top](#table-of-contents)

---

### 📁 Exceptions

```java
package org.example.polling.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}

public class PollNotFoundException extends RuntimeException {
    public PollNotFoundException(String message) { super(message); }
}

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) { super(message); }
}

public class InvalidOptionException extends RuntimeException {
    public InvalidOptionException(String message) { super(message); }
}

public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException(String message) { super(message); }
}
```
[🔼 Back to Top](#table-of-contents)
---

### 📁 Repositories

#### `UserRepository.java`

```java
package org.example.polling.repositories;

import org.example.polling.entities.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    public void save(User user) { users.put(user.getUserId(), user); }
    public User findById(int userId) { return users.get(userId); }
    public boolean exists(int userId) { return users.containsKey(userId); }
}
```

#### `PollRepository.java`

```java
package org.example.polling.repositories;

import org.example.polling.entities.Poll;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PollRepository {
    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();
    private final AtomicInteger pollCounter = new AtomicInteger(1);

    public Poll save(Poll poll) {
        polls.put(poll.getPollId(), poll);
        return poll;
    }

    public Poll findById(int pollId) { return polls.get(pollId); }
    public int generatePollId() { return pollCounter.getAndIncrement(); }
}
```
---

[🔼 Back to Top](#table-of-contents)

---
#### `VoteRepository.java`

```java
package org.example.polling.repositories;

import org.example.polling.entities.Vote;
import org.example.polling.enums.PollOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteRepository {
    private final Map<Integer, Map<PollOption, AtomicInteger>> pollResults = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, PollOption>> userVotes = new ConcurrentHashMap<>();

    public void saveVote(Vote vote) {
        pollResults.putIfAbsent(vote.getPollId(), new ConcurrentHashMap<>());
        userVotes.putIfAbsent(vote.getPollId(), new ConcurrentHashMap<>());

        if (userVotes.get(vote.getPollId()).containsKey(vote.getUserId())) {
            throw new RuntimeException("User already voted in this poll!");
        }

        userVotes.get(vote.getPollId()).put(vote.getUserId(), vote.getSelectedOption());
        pollResults.get(vote.getPollId())
                .computeIfAbsent(vote.getSelectedOption(), k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public Map<PollOption, Integer> getResults(int pollId) {
        Map<PollOption, Integer> results = new ConcurrentHashMap<>();
        pollResults.getOrDefault(pollId, Map.of())
                .forEach((opt, count) -> results.put(opt, count.get()));
        return results;
    }

    public PollOption getUserVote(int pollId, int userId) {
        return userVotes.getOrDefault(pollId, Map.of()).get(userId);
    }
}
```
---

[🔼 Back to Top](#table-of-contents)

---

### 📁 Services

#### `PollService.java`

```java
package org.example.polling.services;

import org.example.polling.entities.Poll;
import org.example.polling.entities.User;
import org.example.polling.enums.PollOption;
import org.example.polling.enums.UserType;
import org.example.polling.exceptions.InvalidOptionException;
import org.example.polling.exceptions.PollNotFoundException;
import org.example.polling.exceptions.UnauthorizedActionException;
import org.example.polling.repositories.PollRepository;
import org.example.polling.repositories.UserRepository;
import org.example.polling.repositories.VoteRepository;
import java.util.*;

public class PollService {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public PollService(PollRepository pollRepository, VoteRepository voteRepository, UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public Poll createPoll(List<String> options, String question, User creator) {
        if (creator.getUserType() != UserType.ADMIN) {
            throw new UnauthorizedActionException("Only admins can create polls!");
        }

        Set<String> normalizedOptions = new LinkedHashSet<>();
        for (String option : options) {
            normalizedOptions.add(option.trim().toLowerCase());
        }

        if (normalizedOptions.size() < 2) {
            throw new InvalidOptionException("At least two unique options required!");
        }

        Map<PollOption, String> optionMap = new EnumMap<>(PollOption.class);
        int index = 0;
        for (String opt : normalizedOptions) {
            optionMap.put(PollOption.values()[index++], opt);
            if (index >= PollOption.values().length) break;
        }

        int pollId = pollRepository.generatePollId();
        Poll poll = new Poll(pollId, question, optionMap, creator);
        return pollRepository.save(poll);
    }

    public Map<PollOption, Integer> getResults(int pollId) {
        Poll poll = pollRepository.findById(pollId);
        if (poll == null) throw new PollNotFoundException("Poll not found!");
        return voteRepository.getResults(pollId);
    }
}
```
---

[🔼 Back to Top](#table-of-contents)

---
#### `UserService.java`

```java
package org.example.polling.services;

import org.example.polling.entities.User;
import org.example.polling.entities.Vote;
import org.example.polling.enums.PollOption;
import org.example.polling.exceptions.DuplicateVoteException;
import org.example.polling.exceptions.PollNotFoundException;
import org.example.polling.exceptions.UserNotFoundException;
import org.example.polling.repositories.PollRepository;
import org.example.polling.repositories.UserRepository;
import org.example.polling.repositories.VoteRepository;

public class UserService {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public UserService(PollRepository pollRepository, VoteRepository voteRepository, UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public void registerUser(User user) { userRepository.save(user); }

    public void castVote(Vote vote) {
        if (!userRepository.exists(vote.getUserId())) throw new UserNotFoundException("User does not exist!");
        if (pollRepository.findById(vote.getPollId()) == null) throw new PollNotFoundException("Poll does not exist!");
        if (voteRepository.getUserVote(vote.getPollId(), vote.getUserId()) != null)
            throw new DuplicateVoteException("User has already voted!");
        voteRepository.saveVote(vote);
    }

    public PollOption getUserVoteForPoll(int userId, int pollId) {
        return voteRepository.getUserVote(pollId, userId);
    }
}
```

---

[🔼 Back to Top](#table-of-contents)

---

### 📁 Controllers

#### `PollController.java`

```java
package org.example.polling.controllers;

import org.example.polling.entities.Poll;
import org.example.polling.entities.User;
import org.example.polling.enums.PollOption;
import org.example.polling.services.PollService;
import java.util.List;
import java.util.Map;

public class PollController {
    private final PollService pollService;
    public PollController(PollService pollService) { this.pollService = pollService; }
    public Poll createPoll(List<String> options, String question, User creator) {
        return pollService.createPoll(options, question, creator);
    }
    public Map<PollOption, Integer> getPollResults(int pollId) {
        return pollService.getResults(pollId);
    }
}
```

#### `UserController.java`

```java
package org.example.polling.controllers;

import org.example.polling.entities.Vote;
import org.example.polling.enums.PollOption;
import org.example.polling.services.UserService;

public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }
    public void castVote(Vote vote) { userService.castVote(vote); }
    public PollOption getUserVote(int userId, int pollId) {
        return userService.getUserVoteForPoll(userId, pollId);
    }
}
```
---

[🔼 Back to Top](#table-of-contents)

---

### 📁 Main Runner

#### `Main.java`

```java
package org.example.polling;

import org.example.polling.controllers.PollController;
import org.example.polling.controllers.UserController;
import org.example.polling.entities.Poll;
import org.example.polling.entities.User;
import org.example.polling.entities.Vote;
import org.example.polling.enums.PollOption;
import org.example.polling.enums.UserType;
import org.example.polling.repositories.PollRepository;
import org.example.polling.repositories.UserRepository;
import org.example.polling.repositories.VoteRepository;
import org.example.polling.services.PollService;
import org.example.polling.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PollRepository pollRepository = new PollRepository();
        VoteRepository voteRepository = new VoteRepository();
        UserRepository userRepository = new UserRepository();

        PollService pollService = new PollService(pollRepository, voteRepository, userRepository);
        UserService userService = new UserService(pollRepository, voteRepository, userRepository);

        PollController pollController = new PollController(pollService);
        UserController userController = new UserController(userService);

        // Create admin
        User admin = new User(1001, "Admin", UserType.ADMIN);
        userService.registerUser(admin);

        // Create poll
        List<String> options = Arrays.asList("Apple", "Mango", "Banana", "Grapes");
        Poll poll = pollController.createPoll(options, "Which is your favorite option?", admin);

        // Register users
        User user1 = new User(1, "Alice", UserType.USER);
        User user2 = new User(2, "Bob", UserType.USER);
        User user3 = new User(3, "Charlie", UserType.USER);
        User user4 = new User(4, "Diana", UserType.USER);

        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(user3);
        userService.registerUser(user4);

        // Voting
        userController.castVote(new Vote(poll.getPollId(), user1.getUserId(), PollOption.OPTION_A));
        userController.castVote(new Vote(poll.getPollId(), user2.getUserId(), PollOption.OPTION_B));
        userController.castVote(new Vote(poll.getPollId(), user3.getUserId(), PollOption.OPTION_A));
        userController.castVote(new Vote(poll.getPollId(), user4.getUserId(), PollOption.OPTION_C));

        // Results
        System.out.println("Poll Results for Poll ID " + poll.getPollId() + ":");
        Map<PollOption, Integer> results = pollController.getPollResults(poll.getPollId());
        results.forEach((opt, count) -> System.out.println(opt + ": " + count));

        // User check
        System.out.println("\nUser 1 voted: " + userService.getUserVoteForPoll(user1.getUserId(), poll.getPollId()));
    }
}
```

---

[🔼 Back to Top](#table-of-contents)

---

## ▶️ Sample Execution

Output after running `Main.java`:

```
Poll Results for Poll ID 1:
OPTION_A: 2
OPTION_B: 1
OPTION_C: 1

User 1 voted: OPTION_A
```

---

[🔼 Back to Top](#table-of-contents)

---

## 🚀 Future Improvements

* Replace in-memory repositories with a **database** (JPA/Hibernate).
* Add


1. [Polling System Basic Low-Level Design - I](https://dev.to/zeeshanali0704/low-level-design-polling-system-basic-2kl5) 

2.  [Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

3. [Low-Level Design: Polling System - API's Using Nodejs & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

4. [Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

5. [Low-Level Design: Polling System - In JAVA](
https://dev.to/zeeshanali0704/lld-building-a-polling-system-in-java-with-repository-pattern-17h)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

