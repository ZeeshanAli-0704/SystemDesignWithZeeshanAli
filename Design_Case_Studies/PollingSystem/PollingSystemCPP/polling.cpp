#include <iostream>
#include <string>
#include <vector>
#include <unordered_map>
#include <map>
#include <mutex>
#include <ctime>

// Poll class to hold the poll information
class Poll
{
public:
    std::string pollId;
    std::string question;
    std::vector<std::string> options;
    std::time_t createdAt;

    Poll(const std::string &id, const std::string &q, const std::vector<std::string> &opts)
        : pollId(id), question(q), options(opts), createdAt(std::time(nullptr)) {}
};

// Vote class to hold the vote information
class Vote
{
public:
    std::string pollId;
    std::string userId;
    std::string option;
    std::time_t timestamp;

    Vote(const std::string &pId, const std::string &uId, const std::string &opt)
        : pollId(pId), userId(uId), option(opt), timestamp(std::time(nullptr)) {}
};

// PollManager class to manage polls and votes
class PollManager
{
private:
    std::unordered_map<std::string, Poll> polls;
    std::unordered_map<std::string, std::map<std::string, int>> pollResults;
    std::unordered_map<std::string, std::unordered_map<std::string, bool>> userVotes;
    std::mutex mtx;

public:
    std::string createPoll(const std::string &question, const std::vector<std::string> &options)
    {
        std::lock_guard<std::mutex> lock(mtx);
        std::string pollId = std::to_string(polls.size() + 1);
        polls.emplace(pollId, Poll(pollId, question, options));
        for (const auto &option : options)
        {
            pollResults[pollId][option] = 0;
        }
        return pollId;
    }

    std::string updatePoll(const std::string &pollId, const std::string &question, const std::vector<std::string> &options)
    {
        std::lock_guard<std::mutex> lock(mtx);
        auto it = polls.find(pollId);
        if (it != polls.end())
        {
            it->second.question = question;
            it->second.options = options;
            return "Poll updated successfully.";
        }
        return "Poll not found.";
    }

    std::string deletePoll(const std::string &pollId)
    {
        std::lock_guard<std::mutex> lock(mtx);
        if (polls.erase(pollId))
        {
            pollResults.erase(pollId);
            userVotes.erase(pollId);
            return "Poll deleted successfully.";
        }
        return "Poll not found.";
    }

    std::string voteInPoll(const std::string &pollId, const std::string &userId, const std::string &option)
    {
        std::lock_guard<std::mutex> lock(mtx);
        auto it = polls.find(pollId);
        if (it != polls.end())
        {
            if (userVotes[pollId][userId])
            {
                return "User has already voted.";
            }
            pollResults[pollId][option]++;
            userVotes[pollId][userId] = true;
            return "Vote cast successfully.";
        }
        return "Poll not found.";
    }

    std::map<std::string, int> viewPollResults(const std::string &pollId)
    {
        std::lock_guard<std::mutex> lock(mtx);
        if (pollResults.find(pollId) != pollResults.end())
        {
            return pollResults[pollId];
        }
        return {};
    }
};

int main()
{
    PollManager pollManager;

    // Creating a poll
    std::string pollId = pollManager.createPoll("What is your favorite color?", {"Red", "Blue", "Green", "Yellow"});
    std::cout << "Poll created with ID: " << pollId << std::endl;

    // Voting in the poll
    std::string voteMessage = pollManager.voteInPoll(pollId, "user1", "Red");
    std::cout << voteMessage << std::endl;

    voteMessage = pollManager.voteInPoll(pollId, "user2", "Blue");
    std::cout << voteMessage << std::endl;

    voteMessage = pollManager.voteInPoll(pollId, "user1", "Green");
    std::cout << voteMessage << std::endl; // Should inform the user has already voted

    // Viewing poll results
    auto results = pollManager.viewPollResults(pollId);
    std::cout << "Poll results for poll ID " << pollId << ":" << std::endl;
    for (const auto &result : results)
    {
        std::cout << result.first << ": " << result.second << " votes" << std::endl;
    }

    // Updating the poll
    std::string updateMessage = pollManager.updatePoll(pollId, "What is your favorite primary color?", {"Red", "Blue", "Yellow"});
    std::cout << updateMessage << std::endl;

    // Voting in the updated poll
    voteMessage = pollManager.voteInPoll(pollId, "user3", "Yellow");
    std::cout << voteMessage << std::endl;

    // Viewing updated poll results
    results = pollManager.viewPollResults(pollId);
    std::cout << "Updated poll results for poll ID " << pollId << ":" << std::endl;
    for (const auto &result : results)
    {
        std::cout << result.first << ": " << result.second << " votes" << std::endl;
    }

    // Deleting the poll
    std::string deleteMessage = pollManager.deletePoll(pollId);
    std::cout << deleteMessage << std::endl;

    // Attempting to view results of a deleted poll
    results = pollManager.viewPollResults(pollId);
    if (results.empty())
    {
        std::cout << "Poll not found or has been deleted." << std::endl;
    }

    return 0;
}
