#include <bemapiset.h>
#include <unordered_map>
#include <list>
#include <fstream>
#include <vector>
#include <mutex>
#include <iostream>
#include <cstring>

#include "cache_utility.h"

#include "./cache_utility.h"

// API Implementation
static BlockCache cache(4096, 100); // Example: 4 KB pages, 100 max pages

// Opens a file and returns a unique file descriptor
bool lab2_open(const string &path) {
    HANDLE hFile;

    hFile = CreateFile(path.c_str(),                // name of the write
                       GENERIC_READ | GENERIC_WRITE,          // open for writing
                       0,                      // do not share
                       nullptr,                   // default security
                       CREATE_NEW,             // create new file only
                       FILE_ATTRIBUTE_NORMAL,  // normal file
                       nullptr);                  // no attr. template

    if (hFile == INVALID_HANDLE_VALUE)
    {
        cout << "CreateFile failed with error: " << GetLastError() << endl;
        return 1;
    }

    CachedPage page;
    page.offset(0);
    page.fileHandle(hFile);

    return cache.putPage(path, page);
}

// Closes a file and synchronizes all dirty pages
int lab2_close(int fd) {

}

// Reads data from the file using the cache
ssize_t lab2_read(int fd, void* buf, size_t count) {

}

// Writes data to the file using the cache
ssize_t lab2_write(int fd, const void* buf, size_t count) {

}

// Moves the file pointer to a specified offset (stub implementation)
off_t lab2_lseek(int fd, off_t offset, int whence) {

}

// Synchronizes all dirty pages for the specified file
int lab2_fsync(int fd) {

}

int main(int argc, char** argv) {

}

