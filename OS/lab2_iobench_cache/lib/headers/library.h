#pragma once

#ifndef LAB2_LIB_LIBRARY_H
#define LAB2_LIB_LIBRARY_H

#ifdef CACHEDIOTEST_EXPORTS
#define CACHEDIOTEST_API __declspec(dllexport)
#else
#define CACHEDIOTEST_API __declspec(dllimport)
#endif

#include <list>
#include <unordered_map>
#include <mutex>
#include <vector>
#include <windows.h>
#include <algorithm>

// Структура страницы кэша
struct CACHEDIOTEST_API CachedPage {
    size_t offset = 0;
    HANDLE fileHandle;
    std::vector<char> data;
};

// Основной класс BlockCache
class CACHEDIOTEST_API BlockCache {
public:
    explicit BlockCache(size_t pageSize, size_t maxPages);

    bool getPage(const uint64_t &fileID, CachedPage *&page);
    bool putPage(const CachedPage& page);
    bool removePage(const uint64_t &fileID);
    void clearCache();
    uint64_t GetFileID(HANDLE fileHandle);
    size_t GetFileSize(const uint64_t &fileID);
    bool isCacheFull();
    bool evict();

    size_t pageSize;
    size_t maxPages;

private:
    std::unordered_map<uint64_t, CachedPage> cacheMap;
    std::list<uint64_t> mruOrder;
    std::mutex cacheMutex;
};

// Функции для работы с кэшем
CACHEDIOTEST_API uint64_t lab2_open(const char *path);
CACHEDIOTEST_API int lab2_close(const uint64_t &fileId);
CACHEDIOTEST_API ssize_t lab2_read(const uint64_t &fileId, size_t count, void* buf);
CACHEDIOTEST_API ssize_t lab2_write(const uint64_t &fileId, size_t count, const void* buf);
CACHEDIOTEST_API off_t lab2_lseek(const uint64_t &fileId, off_t offset, int whence);
CACHEDIOTEST_API int lab2_fsync(const uint64_t &fileId);


// Экспорт статического объекта BlockCache
extern CACHEDIOTEST_API BlockCache cache;

#endif // LAB2_LIB_LIBRARY_H
