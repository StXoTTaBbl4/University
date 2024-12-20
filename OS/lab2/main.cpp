#include <bemapiset.h>
#include <unordered_map>
#include <list>
#include <fstream>
#include <vector>
#include <mutex>
#include <iostream>
#include <cstring>

#include "cache_utility.h"

#define SECTOR_SIZE 512

using namespace std;

static BlockCache cache(4096, 100);

uint64_t lab2_open(const char *path) {
    HANDLE hFile = CreateFile(path,
        GENERIC_READ| GENERIC_WRITE,
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
        return false;
    }

    if (fileSize.QuadPart == 0) {
        cout << "File is empty" << endl;
        return true;
    }

    if (fileSize.QuadPart > cache.pageSize) {
        CloseHandle(hFile);
        cerr << "File " << path << " is too big" << endl;
        return 0;
    }

    const DWORD alignedFileSize = static_cast<DWORD>((fileSize.QuadPart + (SECTOR_SIZE - 1)) / SECTOR_SIZE) * SECTOR_SIZE;

    CachedPage page;
    page.fileHandle = hFile;
    page.data.resize(alignedFileSize);

    DWORD bytesRead = 0;
    if (!ReadFile(hFile, page.data.data(), alignedFileSize, &bytesRead, nullptr)) {
        cerr << "Error reading file: " << GetLastError() << endl;
        return false;
    }

    if (cache.putPage(page)) {
        cout << "Success" << endl;
        return cache.GetFileID(hFile);
    }

    CloseHandle(hFile);
    cerr << "Can't add to cache " << path << endl << GetLastError() <<  endl;
    return 0;
}

int lab2_close(const uint64_t &fileId) {
    CachedPage page;
    if (!cache.getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return 1;
    }

    CloseHandle(page.fileHandle);
    cache.removePage(fileId);
    return 0;
}

ssize_t lab2_read(const uint64_t &fileId, size_t count, void* buf ) {
    CachedPage page;
    if (cache.getPage(fileId, page)) {
        if (!page.data.empty()) {
            if (page.data.size() <= count) {
                memcpy(buf, page.data.data(), page.data.size());
                cache.removePage(fileId);
                return page.data.size();
            }
            return -1;
        }
        cerr << "Cached file is smaller than requested: " << page.data.size() << " vs " << count << endl;
        return -1;
    }

    cerr << "File " << fileId << " not found in cache" << endl;
    return -1;
}

ssize_t lab2_write(const uint64_t &fileId, size_t count, const void* buf) {
    CachedPage page;
    if (cache.getPage(fileId, page)) {
        if (!count <= cache.pageSize ) {
            return -1;
        }

        if (page.data.size() <= count || page.data.size() > count) {
			page.data.resize(count);
        }

        memcpy(page.data.data(), buf, count);
		return count;
        
    }
	cerr << "Cached file is smaller than requested: " << page.data.size() << " vs " << count << endl;
    return -1;
}

off_t lab2_lseek(const uint64_t &fileId, off_t offset, int whence) {
    CachedPage page;
    if (!cache.getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return -1;
    }
    if (!page.data.size() < offset) {
        return -1;
    }

    LARGE_INTEGER newPointer;
    LARGE_INTEGER liOffset;
    liOffset.QuadPart = static_cast<LONGLONG>(offset);
    if (!SetFilePointerEx(page.fileHandle, liOffset, &newPointer, whence)) {
        cerr << "Error setting file pointer" << endl;
        return -1;
    }

    return newPointer.QuadPart;
}

int lab2_fsync(const uint64_t &fileId) {
    CachedPage page;
    if (!cache.getPage(fileId, page)) {
        cout << "File " << fileId << " not found in cache" << endl;
        return -1;
    }

    DWORD bytesWritten;
    if (!WriteFile(page.fileHandle, page.data.data(), page.data.size(), &bytesWritten, nullptr)) {
        cerr << "Error writing file" << endl;
        return -1;
    }

    cache.removePage(fileId);
    return 0;
}

int main(int argc, char** argv) {
    uint64_t fd = lab2_open("D:\\lab2_test.txt");
    char buf[512];
    lab2_read(fd, 512, buf);
    cout << buf << endl;

    lab2_close(fd);
}

