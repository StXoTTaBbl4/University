#include "library.h"

#include <iostream>
#include <unordered_map>
#include <windows.h>
#include <algorithm>
#include <cstdint>
#include <memory>

using namespace std;

#define CACHE_SIZE 100
#define CACHE_BLOCK 4096

struct mru_cache {
    size_t offset = 0;
    HANDLE fileHandle = nullptr;
    unordered_map<ssize_t, char[CACHE_BLOCK]> data;
};

class MruCache {

    public:
        // Открытие файла по заданному пути файла, доступного для чтения. Процедура возвращает некоторый хэндл на файл. Пример:
        int lab2_open(const char *path) {
            HANDLE hFile = CreateFile(path,
            GENERIC_READ | GENERIC_WRITE,
            FILE_SHARE_READ,
            nullptr,
            OPEN_ALWAYS,
            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH,
            nullptr);

            if (hFile == INVALID_HANDLE_VALUE) {
                cout << "Error opening file " << path << endl << GetLastError() <<  endl;
                return 1;
            }

            LARGE_INTEGER fileSize;
            if (!GetFileSizeEx(hFile, &fileSize)) {
                cerr << "Can't get file size: " << GetLastError() << std::endl;
                return 1;
            }

            if (fileSize.QuadPart == 0) {
                cout << "File is empty" << endl;
                return 1;
            }

            if (fileSize.QuadPart > CACHE_SIZE * CACHE_BLOCK) {
                CloseHandle(hFile);
                cerr << "File " << path << " is too big" << endl;
                return 1;
            }
            mru_cache.fileHandle = hFile;
            const DWORD alignedFileSize = static_cast<DWORD>((fileSize.QuadPart + (CACHE_BLOCK - 1)) / CACHE_BLOCK) * CACHE_BLOCK;
            size_t block_index = 0;

            

            return 0;
        }
        // Закрытие файла по хэндлу. Пример:
        int lab2_close() {
            if(!CloseHandle(mru_cache.fileHandle)) {
                cerr << "Error closing file" << endl;
                return 1;
            }
            mru_cache.fileHandle = nullptr;
            mru_cache.offset = 0;
            mru_cache.data.clear();
            return 0;
        }
        // Чтение данных из файла. Пример:
        ssize_t lab2_read(size_t count, void* buf) {

        }
        // Запись данных в файл. Пример:
        ssize_t lab2_write(int fd, size_t count, void* buf) {

        }
        // Перестановка позиции указателя на данные файла. Достаточно поддержать только абсолютные координаты. Пример:
        off_t lab2_lseek(int fd, off_t offset, int whence) {

        }
        // Синхронизация данных из кэша с диском. Пример:
        int lab2_fsync(int fd) {}

    private:
    mru_cache mru_cache;


};

int main() {

}
