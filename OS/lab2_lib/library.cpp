#include "library.h"
#include <windows.h>

#include <iostream>
#include <unordered_map>
#include <list>
#include <fstream>
#include <vector>
#include <mutex>
#include <cstring>
#include <algorithm>


using namespace std;

// int BlockCache::putPage(char buff[], size_t size) {
//     lock_guard<mutex> lock(cacheMutex);
//
//     // Преобразуем массив в std::vector<char>
//     std::vector<char> buffer(buff, buff + size);
//
//     // Если место в кэше есть, добавляем новую запись
//     if (cacheMap.size() < maxPages) {
//         cacheMap[generateBlockId()] = buffer;
//         return true;
//     }
//
//     // Если кэш полный, выводим сообщение
//     cout << "Cache is full, remove something first." << endl;
//     return false;
// }
//
//
// bool BlockCache::getPage(const uint64_t &fileID, CachedPage*& page) {
//     lock_guard<mutex> lock(cacheMutex);
//     if (const auto it = cacheMap.find(fileID); it != cacheMap.end()) {
//         page = &it->second;
//         return true;
//     }
//     return false;
// }
//
// bool BlockCache::removePage(const uint64_t &fileID) {
//     lock_guard<mutex> lock(cacheMutex);
//     if (cacheMap.contains(fileID)) {
//         CloseHandle(cacheMap[fileID].fileHandle);
//         cacheMap.erase(fileID);
//         if (auto it = std::find(mruOrder.begin(), mruOrder.end(), fileID); it != mruOrder.end()) {
//             mruOrder.erase(it);
//         }
//         return true;
//     }
//     return false;
// }
//
// void BlockCache::clearCache() {
//     lock_guard<mutex> lock(cacheMutex);
//     cacheMap.clear();
// }
//
// bool BlockCache::isCacheFull() {
//     if (cacheMap.size() == maxPages) {
//         return true;
//     }
//     return false;
// }
//
// bool BlockCache::evict() {
//     if (!mruOrder.empty()) {
//         removePage(mruOrder.back());
//         mruOrder.pop_back();
//         return true;
//     }
//     return false;
// }
//
// size_t BlockCache::GetFileSize(const uint64_t &fileID) {
//     CachedPage *page;
//     getPage(fileID, page);
//     LARGE_INTEGER fileSize;
//     if (page->fileHandle != INVALID_HANDLE_VALUE) {
//         if (!GetFileSizeEx(page->fileHandle, &fileSize)) {
//             cerr << "Can't get file size: " << GetLastError() << std::endl;
//             return fileSize.QuadPart;
//         }
//         return 0;
//     }
//     return 0;
// }

bool BlockCache::lab2_evict() {
    
}


uint64_t BlockCache::lab2_open(const char *path) {
    HANDLE hFile = CreateFile(path,
        GENERIC_READ | GENERIC_WRITE,
        FILE_SHARE_READ,
        nullptr,
        OPEN_ALWAYS,
        FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH,
        nullptr);

    if (hFile == INVALID_HANDLE_VALUE) {
        cout << "Error opening file " << path << endl << GetLastError() <<  endl;
        return 0;
    }

    LARGE_INTEGER fileSize;
    if (!GetFileSizeEx(hFile, &fileSize)) {
        cerr << "Can't get file size: " << GetLastError() << std::endl;
        return 0;
    }

    if (fileSize.QuadPart == 0) {
        cout << "File is empty" << endl;
        return 0;
    }

    if (fileSize.QuadPart > cache->pageSize) {
        CloseHandle(hFile);
        cerr << "File " << path << " is too big" << endl;
        return 0;
    }

    const DWORD alignedFileSize = static_cast<DWORD>((fileSize.QuadPart + (SECTOR_SIZE - 1)) / SECTOR_SIZE) * SECTOR_SIZE;

    if (cacheMap.size() > maxPages) {
        evict();
    }

    CachedPage page;
    page.fileHandle = hFile;
    page.data.resize(alignedFileSize);

    DWORD bytesRead = 0;
    if (!ReadFile(hFile, page.data.data(), alignedFileSize, &bytesRead, nullptr)) {
        cerr << "Error reading file: " << GetLastError() << endl;
        return false;
    }

    if (cache->putPage(page)) {
        return cache->GetFileID(hFile);
    }

    CloseHandle(hFile);
    cerr << "Can't add to cache " << path << endl << GetLastError() <<  endl;
    return 0;
}

int lab2_close(const uint64_t &fileId, BlockCache *cache) {
    CachedPage* page;
    if (!cache->getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return 1;
    }

    CloseHandle(page->fileHandle);
    cache->removePage(fileId);
    return 0;
}

ssize_t lab2_read(const uint64_t &fileId, size_t count, void* buf, BlockCache *cache ) {
    CachedPage* page;
    if (cache->getPage(fileId, page)) {
        if (!page->data.empty()) {
            if (page->data.size() <= count) {
                memcpy(buf, page->data.data(), page->data.size());
                return page->data.size();
            }
            return -1;
        }
        cerr << "Cached file is smaller than requested: " << page->data.size() << " vs " << count << endl;
        return -1;
    }

    cerr << "File " << fileId << " not found in cache" << endl;
    return -1;
}

ssize_t lab2_write(const uint64_t &fileId, size_t count, const void* buf, BlockCache *cache) {
    CachedPage* page;
    if (cache->getPage(fileId, page)) {
        if (count > cache->pageSize ) {
            cerr << "File " << fileId << " is too big: " << count << " > " << cache->pageSize << endl;
            return -1;
        }

        if (page->data.size() <= count || page->data.size() > count) {
			page->data.resize(count);
        }

        const char* charBuf = static_cast<const char*>(buf);
        page->data.clear();
        page->data.insert(page->data.begin(), charBuf, charBuf + count);
		return count;

    }
	cerr << "Something gone wrong T_T" << endl;
    return -1;
}

off_t lab2_lseek(const uint64_t &fileId, off_t offset, int whence, BlockCache *cache) {
    CachedPage* page;
    if (!cache->getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return -1;
    }
    if (!page->data.size() < offset) {
        cerr << "File " << fileId << " is too small" << endl;
        return -1;
    }
    LARGE_INTEGER zeroOffset = {0};
    LARGE_INTEGER currentOffset;
    if (!SetFilePointerEx(page->fileHandle, zeroOffset, &currentOffset, FILE_CURRENT)) {
        cerr << "Failed to get file pointer position. Error: " << GetLastError() << std::endl;
        return false;
    }

    cout << "Current offset: " << currentOffset.QuadPart << endl;

    LARGE_INTEGER newPointer;
    LARGE_INTEGER liOffset;
    liOffset.QuadPart = static_cast<LONGLONG>(offset);
    if (!SetFilePointerEx(page->fileHandle, liOffset, &newPointer, whence)) {
        cerr << "Error setting file pointer" << endl;
        return -1;
    }


    if (!SetFilePointerEx(page->fileHandle, zeroOffset, &currentOffset, FILE_CURRENT)) {
        cerr << "Failed to get file pointer position. Error: " << GetLastError() << endl;
        return false;
    }

    cout << "Current offset: " << currentOffset.QuadPart << endl;

    return newPointer.QuadPart;
}

int lab2_fsync(const uint64_t &fileId, BlockCache *cache) {
    CachedPage* page;
    if (!cache->getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return -1;
    }

    DWORD bytesWritten;
    if (!WriteFile(page->fileHandle, page->data.data(), page->data.size(), &bytesWritten, nullptr)) {
        cerr << "Error writing file " << GetLastError() << endl;
        return -1;
    }

    return 0;
}

int main() {

}

