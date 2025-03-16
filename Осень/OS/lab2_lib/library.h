#pragma once

#ifndef LAB2_LIB_LIBRARY_H
#define LAB2_LIB_LIBRARY_H

#ifdef CACHEDIOTEST_EXPORTS
#define CACHEDIOTEST_API __declspec(dllexport)
#else
#define CACHEDIOTEST_API __declspec(dllimport)
#endif

#define BLOCK_SIZE 4096
#define SECTOR_SIZE 512;

#include <list>
#include <unordered_map>
#include <mutex>
#include <vector>
#include <windows.h>
#include <algorithm>
#include <atomic>

class CACHEDIOTEST_API BlockCache {

public:
    BlockCache(const size_t _pageSize, const size_t _maxPages) {
        pageSize = _pageSize;
        maxPages = _maxPages;
        mruOrder.resize(maxPages);
        fileDescriptor = 0;
        nextBlockId = 0;
    }

    // char getPage(const uint64_t &fileID, CachedPage *&page);
    // int putPage(char buff[], size_t size);
    // bool removePage(const uint64_t &fileID);
    // void clearCache();
    // size_t GetFileSize(const uint64_t &fileID);
    // bool isCacheFull();
    // bool evict();

    uint64_t lab2_open(const char *path);
    int lab2_close(const uint64_t &fileId);
    ssize_t lab2_read(const uint64_t &fileId, size_t count, void* buf);
    ssize_t lab2_write(const uint64_t &fileId, size_t count, const void* buf);
    off_t lab2_lseek(const uint64_t &fileId, off_t offset, int whence);
    int lab2_fsync(const uint64_t &fileId);
    bool lab2_evict();

    size_t generateBlockId() {
        return nextBlockId++;
    }

    size_t pageSize;
    size_t maxPages;
    uint64_t fileDescriptor;

private:
    std::unordered_map<size_t, std::vector<char>> cacheMap;
    std::list<int> mruOrder;
    std::mutex cacheMutex;
    size_t nextBlockId;
};



#endif // LAB2_LIB_LIBRARY_H
