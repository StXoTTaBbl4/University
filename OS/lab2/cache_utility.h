//
// Created by Alexandr on 14.12.2024.
//

#ifndef CACHE_UTILITY_H
#define CACHE_UTILITY_H

#include <unordered_map>
#include <list>
#include <mutex>
#include <cstring>
#include <windows.h>

using namespace std;

struct CachedPage {
    size_t offset = 0;
    HANDLE fileHandle;
};

class BlockCache {
public:
    // Constructor initializing the cache with a given page size and maximum number of pages
    explicit BlockCache(const size_t pageSize, const size_t maxPages)
        : pageSize(pageSize), maxPages(maxPages) {}

    CachedPage* getPage(const string &filePath); // Retrieves a page from the cache or loads it from the file
    bool putPage(const string &filePath, CachedPage &page); // Writes a page to the file if dirty
    bool removePage(const string &filePath); // Removes the most recently used page from the cache
    void clearCache();

    size_t pageSize; // Size of each page in the cache
    size_t maxPages; // Maximum number of pages in the cache
private:
    unordered_map<string, CachedPage> cacheMap;
    mutex cacheMutex; // Mutex to ensure thread safety
};

#endif //CACHE_UTILITY_H
