#include "PollManager.h"
#include <iostream>

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
