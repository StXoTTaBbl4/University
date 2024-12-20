#include <unordered_map>
#include <list>
#include <fstream>
#include <vector>
#include <mutex>
#include <iostream>

#include "./cache_utility.h"

using namespace std;

BlockCache::BlockCache(const size_t pageSize, const size_t maxPages)
    : pageSize(pageSize), maxPages(maxPages) {}

uint64_t BlockCache::GetFileID(HANDLE fileHandle) {
    BY_HANDLE_FILE_INFORMATION fileInfo;

    if (GetFileInformationByHandle(fileHandle, &fileInfo)) {
        const uint64_t fileId = static_cast<uint64_t>(fileInfo.nFileIndexHigh) << 32 | fileInfo.nFileIndexLow;
        return fileId;
    }
    cerr << "Failed to get file information. Error: " << GetLastError() << endl;
    return -1;
}

bool BlockCache::putPage(const CachedPage& page) {
    lock_guard<mutex> lock(cacheMutex);
    if (cacheMap.size() < maxPages) {
        if (const uint64_t fileId = GetFileID(page.fileHandle); !cacheMap.contains(fileId)) {
            cacheMap[fileId] = page;
            return true;
        }
    }
    cout << "Cache is full, remove something first." << endl;
    return false;
}

bool BlockCache::getPage(const uint64_t &fileID, CachedPage &page) {
    lock_guard<mutex> lock(cacheMutex);
    if (const auto it = cacheMap.find(fileID); it != cacheMap.end()) {
        page = it->second;
        return true;
    }
    return false;
}

bool BlockCache::removePage(const uint64_t &fileID) {
    lock_guard<mutex> lock(cacheMutex);
    if (!cacheMap.contains(fileID)) {
        cacheMap.extract(fileID);
        return true;
    }
    return false;
}

void BlockCache::clearCache() {
    lock_guard<mutex> lock(cacheMutex);
    cacheMap.clear();
}