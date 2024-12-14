#include <unordered_map>
#include <list>
#include <fstream>
#include <vector>
#include <mutex>
#include <iostream>

#include "./cache_utility.h"

using namespace std;

CachedPage* BlockCache::getPage(const string &filePath) {
    lock_guard<mutex> lock(cacheMutex);
    return &cacheMap[filePath];
}



// Removes the most recently used page from the cache
bool BlockCache::putPage(const string &filePath, CachedPage &page) {
    lock_guard<mutex> lock(cacheMutex);
    if (cacheMap.size() < maxPages) {
        if (!cacheMap.contains(filePath)) {
            cacheMap[filePath] = page;
            return true;
        }
    } else {
        cout << "Cache is full, remove something first." << endl;
    }
    return false;
}

// Writes a page to the file
bool BlockCache::removePage(const string &filePath) {
    lock_guard<mutex> lock(cacheMutex);
    if (!cacheMap.contains(filePath)) {
        cacheMap.erase(filePath);
        return true;
    }
    return false;
}

// Synchronizes all dirty pages for a specific file
void BlockCache::clearCache() {
    lock_guard<mutex> lock(cacheMutex);
    cacheMap.clear();
}