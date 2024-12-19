#ifndef CACHE_UTILITY_H
#define CACHE_UTILITY_H

#include <unordered_map>
#include <mutex>
#include <cstring>
#include <windows.h>

using namespace std;

struct CachedPage {
    size_t offset = 0;
    HANDLE fileHandle;
    vector<char> data;
};

enum class FileStatus {
    OK = 0,
    INVALID_HANDLE = -1,
    FILE_TOO_BIG = -2,
    READ_FAILURE = -3,
    WRITE_FAILURE = -4,
    CACHE_FULL = -5,
    CANT_ADD_TO_CACHE = -6,
    NOT_FOUND_IN_CACHE = -7
};

class BlockCache {
public:
    explicit BlockCache(const size_t pageSize, const size_t maxPages);

    bool getPage(const string &fileID, CachedPage &page);
    bool putPage(const CachedPage& page);
    bool removePage(const string& fileID);
    void clearCache();
    uint64_t GetFileID(HANDLE fileHandle);

    size_t pageSize;
    size_t maxPages;
private:
    unordered_map<string, CachedPage> cacheMap;
    mutex cacheMutex;
};

#endif //CACHE_UTILITY_H
