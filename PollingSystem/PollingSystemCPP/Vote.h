#ifndef VOTE_H
#define VOTE_H

#include <string>
#include <ctime>

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

#endif // VOTE_H
