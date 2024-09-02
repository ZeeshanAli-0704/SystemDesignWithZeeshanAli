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
Please refer to the article [Polling System Basic Low-Level Design - I](https://dev.to/zeeshanali0704/polling-2hc8) 

Next
[Low-Level Design: Polling System - Edge Cases](https://dev.to/zeeshanali0704/low-level-design-polling-system-edge-cases-50pf)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli