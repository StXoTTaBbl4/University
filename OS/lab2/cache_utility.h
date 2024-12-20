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

class BlockCache {
public:
    explicit BlockCache(const size_t pageSize, const size_t maxPages);

    bool getPage(const uint64_t &fileID, CachedPage &page);
    bool putPage(const CachedPage& page);
    bool removePage(const uint64_t &fileID);
    void clearCache();
    uint64_t GetFileID(HANDLE fileHandle);

    size_t pageSize;
    size_t maxPages;
private:
    unordered_map<uint64_t, CachedPage> cacheMap;
    mutex cacheMutex;
};

#endif //CACHE_UTILITY_H
