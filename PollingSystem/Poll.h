#ifndef POLL_H
#define POLL_H

#include <string>
#include <vector>
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

#endif // POLL_H
