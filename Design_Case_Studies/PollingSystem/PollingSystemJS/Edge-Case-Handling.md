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

1. [Low-Level Design: Polling System: Basic](https://dev.to/zeeshanali0704/polling-2hc8)
  
2. [Low-Level Design: Polling System - Using Node.js & SQL](https://dev.to/zeeshanali0704/low-level-design-polling-system-2-2j21)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli