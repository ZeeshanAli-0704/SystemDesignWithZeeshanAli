#ifndef POLLMANAGER_H
#define POLLMANAGER_H

#include "Poll.h"
#include "Vote.h"
#include <unordered_map>
#include <map>
#include <mutex>
#include <string>
#include <vector>

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
        Poll newPoll(pollId, question, options);
        polls[pollId] = newPoll;
        for (const auto &option : options)
        {
            pollResults[pollId][option] = 0;
        }
        return pollId;
    }

    std::string updatePoll(const std::string &pollId, const std::string &question, const std::vector<std::string> &options)
    {
        std::lock_guard<std::mutex> lock(mtx);
        if (polls.find(pollId) != polls.end())
        {
            polls[pollId].question = question;
            polls[pollId].options = options;
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
        if (polls.find(pollId) != polls.end())
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

#endif // POLLMANAGER_H
