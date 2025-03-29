#include "library.h"

#include <iostream>
#include <list>
#include <windows.h>
#include <memory>
#include <unordered_map>
#include <algorithm>
#include <chrono>

using namespace std;

MruCache::MruCache() {
    // Инициализация ресурсов
}

// Деструктор
MruCache::~MruCache() {
    // Освобождение ресурсов
    // if (fileHandle != nullptr) {
    //     lab2_close(fileHandle, true);
    // }
}

HANDLE MruCache::lab2_open(const char *path) {
            HANDLE hFile = CreateFile(path,
            GENERIC_READ | GENERIC_WRITE,
            FILE_SHARE_READ,
            nullptr,
            OPEN_ALWAYS,
            FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH,
            nullptr);

            if (hFile == INVALID_HANDLE_VALUE) {
                cout << "Error opening file " << path << endl << GetLastError() <<  endl;
                return nullptr;
            }

            LARGE_INTEGER fileSize;
            if (!GetFileSizeEx(hFile, &fileSize)) {
                cerr << "Can't get file size: " << GetLastError() << std::endl;
                return nullptr;
            }

            if (fileSize.QuadPart == 0) {
                cout << "File is empty" << endl;
                return nullptr;
            }

            if (fileSize.QuadPart > CACHE_SIZE * CACHE_BLOCK) {
                CloseHandle(hFile);
                cerr << "File " << path << " is too big: " << fileSize.QuadPart << " vs " << CACHE_SIZE * CACHE_BLOCK << endl;
                return nullptr;
            }

            fileHandle = hFile;
            mruList.clear();
            currentOffset = 0;
            currentCacheSize = 0;

            return hFile;
        }

// Закрытие файла по хэндлу.
int MruCache::lab2_close(HANDLE handle, bool synchronize) {
    if (handle != fileHandle) return -1;
    if (synchronize) lab2_fsync(handle);
    cache.clear();
    mruList.clear();
    currentCacheSize = 0;
    return CloseHandle(handle) ? 0 : -1;
}

// Чтение данных из файла.
size_t MruCache::lab2_read(void* buf, size_t count) {
    char* dest = static_cast<char*>(buf);
    size_t bytesRead = 0;

    LARGE_INTEGER fileSize;
    if (!GetFileSizeEx(fileHandle, &fileSize)) {
        return -1;
    }

    while (count > 0) {
        if (currentOffset >= fileSize.QuadPart) {
            break;
        }

        size_t blockIndex = currentOffset / CACHE_BLOCK;
        size_t blockOffset = currentOffset % CACHE_BLOCK;
        size_t bytesToRead = min(CACHE_BLOCK - blockOffset, count);

        auto it = cache.find(blockIndex);
        if (it == cache.end()) {
            // cout << "No cache block found, uploading in cache..." << endl;
            CacheBlock newBlock{};
            newBlock.fileOffset = blockIndex * CACHE_BLOCK;
            newBlock.dirty = false;

            LARGE_INTEGER filePos;
            filePos.QuadPart = newBlock.fileOffset;
            SetFilePointerEx(fileHandle, filePos, nullptr, FILE_BEGIN);

            DWORD bytesReadFromFile;
            if (!ReadFile(fileHandle, newBlock.data, CACHE_BLOCK, &bytesReadFromFile, nullptr)) {
                return -1;
            }

            currentCacheSize += CACHE_BLOCK;

            if (currentCacheSize > CACHE_SIZE) {
                size_t evictIndex = mruList.back();
                mruList.pop_back();
                if (cache[evictIndex].dirty) {
                    LARGE_INTEGER evictPos;
                    evictPos.QuadPart = cache[evictIndex].fileOffset;
                    SetFilePointerEx(fileHandle, evictPos, nullptr, FILE_BEGIN);
                    DWORD bytesWritten;
                    WriteFile(fileHandle, cache[evictIndex].data, CACHE_BLOCK, &bytesWritten, nullptr);
                }
                cache.erase(evictIndex);
                currentCacheSize -= CACHE_BLOCK;
            }

            cache[blockIndex] = newBlock;
            mruList.push_back(blockIndex);
        }

        const CacheBlock& block = cache[blockIndex];
        memcpy(dest, block.data + blockOffset, bytesToRead);

        if (auto el = ranges::find(mruList, blockIndex); el != mruList.end()) {
            mruList.splice(mruList.end(), mruList, el);
        }

        dest += bytesToRead;
        count -= bytesToRead;
        bytesRead += bytesToRead;
        currentOffset += bytesToRead;
    }

    return bytesRead;
}

// Запись данных в файл.
ssize_t MruCache::lab2_write(const void* buf, size_t count) {
    const char* src = static_cast<const char*>(buf);
    size_t bytesWritten = 0;

    while (count > 0) {
        size_t blockIndex = currentOffset / CACHE_BLOCK;
        size_t blockOffset = currentOffset % CACHE_BLOCK;
        size_t bytesToWrite = std::min(CACHE_BLOCK - blockOffset, count);

        auto it = cache.find(blockIndex);
        if (it == cache.end()) {
            // Загружаем блок в кэш
            CacheBlock newBlock{};
            newBlock.fileOffset = blockIndex * CACHE_BLOCK;
            newBlock.dirty = false;

            LARGE_INTEGER filePos;
            filePos.QuadPart = newBlock.fileOffset;
            SetFilePointerEx(fileHandle, filePos, nullptr, FILE_BEGIN);

            DWORD bytesReadFromFile;
            if (!ReadFile(fileHandle, newBlock.data, CACHE_BLOCK, &bytesReadFromFile, nullptr)) {
                return -1;
            }

            cache[blockIndex] = newBlock;

            if (currentCacheSize > CACHE_SIZE) {
                size_t evictIndex = mruList.back();
                mruList.pop_back();
                if (cache[evictIndex].dirty) {
                    LARGE_INTEGER evictPos;
                    evictPos.QuadPart = cache[evictIndex].fileOffset;
                    SetFilePointerEx(fileHandle, evictPos, nullptr, FILE_BEGIN);
                    DWORD bytesWrittenToFile;
                    WriteFile(fileHandle, cache[evictIndex].data, CACHE_BLOCK, &bytesWrittenToFile, nullptr);
                }
                cache.erase(evictIndex);
                currentCacheSize -= CACHE_BLOCK;
            }

            mruList.push_back(blockIndex);
            currentCacheSize += CACHE_BLOCK;
        }

        CacheBlock& block = cache[blockIndex];
        memcpy(block.data + blockOffset, src, bytesToWrite);
        block.dirty = true;

        if (auto el = ranges::find(mruList, blockIndex); el != mruList.end()) {
            mruList.splice(mruList.end(), mruList, el);
        }

        src += bytesToWrite;
        count -= bytesToWrite;
        bytesWritten += bytesToWrite;
        currentOffset += bytesToWrite;
    }

    return bytesWritten;
}

// Перестановка позиции указателя на данные файла. Достаточно поддержать только абсолютные координаты.
off_t MruCache::lab2_lseek(int fd, off_t offset, int whence) {
    LARGE_INTEGER newPos;
    LARGE_INTEGER moveOffset;
    moveOffset.QuadPart = offset;

    DWORD moveMethod;
    switch (whence) {
        case SEEK_SET: moveMethod = FILE_BEGIN; break;
        case SEEK_CUR: moveMethod = FILE_CURRENT; break;
        case SEEK_END: moveMethod = FILE_END; break;
        default: return -1;
    }

    if (!SetFilePointerEx(fileHandle, moveOffset, &newPos, moveMethod)) {
        return -1;
    }

    currentOffset = newPos.QuadPart;
    return currentOffset;
}

// Синхронизация данных из кэша с диском. Пример:
int MruCache::lab2_fsync(HANDLE handle) {
    for (auto& [blockIndex, block] : cache) {
        if (block.dirty) {
            LARGE_INTEGER blockPos;
            blockPos.QuadPart = block.fileOffset;
            SetFilePointerEx(handle, blockPos, nullptr, FILE_BEGIN);

            DWORD bytesWritten;
            if (!WriteFile(handle, block.data, CACHE_BLOCK, &bytesWritten, nullptr)) {
                return -1;
            }
            block.dirty = false;
        }
    }
    return 0;
}

void MruCache::print_stat() const {
    cout << "File stats:" << endl;
    cout << "currentOffset: " << currentOffset << endl;
    cout << "cache.size: " << cache.size() << endl;
    cout << "fileHandle: " << fileHandle << endl;
}

// #define CACHE_SIZE 100000
// #define CACHE_BLOCK 4096
//
// struct CacheBlock {
//     char data[CACHE_BLOCK]; // Данные блока
//     bool dirty;             // Флаг "грязности" (если были изменения)
//     off_t fileOffset;       // Смещение блока в файле
// };
//
// class MruCache {
//
//     public:
//         // Открытие файла по заданному пути файла, доступного для чтения. Процедура возвращает некоторый хэндл на файл.
//         HANDLE lab2_open(const char *path) {
//             HANDLE hFile = CreateFile(path,
//             GENERIC_READ | GENERIC_WRITE,
//             FILE_SHARE_READ,
//             nullptr,
//             OPEN_ALWAYS,
//             FILE_FLAG_NO_BUFFERING | FILE_FLAG_WRITE_THROUGH,
//             nullptr);
//
//             if (hFile == INVALID_HANDLE_VALUE) {
//                 cout << "Error opening file " << path << endl << GetLastError() <<  endl;
//                 return nullptr;
//             }
//
//             LARGE_INTEGER fileSize;
//             if (!GetFileSizeEx(hFile, &fileSize)) {
//                 cerr << "Can't get file size: " << GetLastError() << std::endl;
//                 return nullptr;
//             }
//
//             if (fileSize.QuadPart == 0) {
//                 cout << "File is empty" << endl;
//                 return nullptr;
//             }
//
//             if (fileSize.QuadPart > CACHE_SIZE * CACHE_BLOCK) {
//                 CloseHandle(hFile);
//                 cerr << "File " << path << " is too big: " << fileSize.QuadPart << " vs " << CACHE_SIZE * CACHE_BLOCK << endl;
//                 return nullptr;
//             }
//
//             fileHandle = hFile;
//             mruList.clear();
//             currentOffset = 0;
//             currentCacheSize = 0;
//
//             // const DWORD alignedFileSize = static_cast<DWORD>((fileSize.QuadPart + (CACHE_BLOCK - 1)) / CACHE_BLOCK) * CACHE_BLOCK;
//             // size_t block_index = 0;
//             // DWORD bytesRead;
//             //
//             // while (true) {
//             //     char buffer[CACHE_BLOCK];
//             //     if (!ReadFile(hFile, buffer, CACHE_BLOCK, &bytesRead, nullptr)) {
//             //         cerr << "Error reading file: " << GetLastError() << endl;
//             //         return false;
//             //     }
//             //
//             //     if (bytesRead == 0) {
//             //         break;
//             //     }
//             //
//             //     // Копируем блок в map
//             //     memcpy(data[block_index], buffer, bytesRead);
//             //     eviction_queue.push_back(block_index);
//             //     blocks_order.push_back(block_index);
//             //
//             //     ++block_index;
//             //
//             //     // Если считали меньше, чем BLOCK_SIZE, значит, файл закончился
//             //     if (bytesRead < CACHE_BLOCK) {
//             //         break;
//             //     }
//             // }
//
//             return hFile;
//         }
//
//         // Закрытие файла по хэндлу.
//         int lab2_close(HANDLE handle) {
//
//             if (handle != fileHandle) return -1;
//             lab2_fsync(handle);
//             cache.clear();
//             mruList.clear();
//             currentCacheSize = 0;
//             return CloseHandle(handle) ? 0 : -1;
//         }
//
//         // Чтение данных из файла.
//         size_t lab2_read(void* buf, size_t count) {
//             char* dest = static_cast<char*>(buf);
//             size_t bytesRead = 0;
//
//             LARGE_INTEGER fileSize;
//             if (!GetFileSizeEx(fileHandle, &fileSize)) {
//                 return -1; // Ошибка получения размера файла
//             }
//
//             while (count > 0) {
//                 if (currentOffset >= fileSize.QuadPart) {
//                     break; // Достигнут конец файла
//                 }
//
//                 size_t blockIndex = currentOffset / CACHE_BLOCK;
//                 size_t blockOffset = currentOffset % CACHE_BLOCK;
//                 size_t bytesToRead = min(CACHE_BLOCK - blockOffset, count);
//
//                 // Проверяем, есть ли блок в кэше
//                 auto it = cache.find(blockIndex);
//                 if (it == cache.end()) {
//                     // cout << "No cache block found, uploading in cache..." << endl;
//                     // Загружаем блок в кэш
//                     CacheBlock newBlock{};
//                     newBlock.fileOffset = blockIndex * CACHE_BLOCK;
//                     newBlock.dirty = false;
//
//                     LARGE_INTEGER filePos;
//                     filePos.QuadPart = newBlock.fileOffset;
//                     SetFilePointerEx(fileHandle, filePos, nullptr, FILE_BEGIN);
//
//                     DWORD bytesReadFromFile;
//                     if (!ReadFile(fileHandle, newBlock.data, CACHE_BLOCK, &bytesReadFromFile, nullptr)) {
//                         return -1;
//                     }
//
//                     cache[blockIndex] = newBlock;
//                     mruList.push_front(blockIndex);
//                     currentCacheSize += CACHE_BLOCK;
//
//                     // Если кэш заполнен, вытесняем MRU-блок
//                     if (currentCacheSize > CACHE_SIZE) {
//                         size_t evictIndex = mruList.back();
//                         mruList.pop_back();
//                         if (cache[evictIndex].dirty) {
//                             LARGE_INTEGER evictPos;
//                             evictPos.QuadPart = cache[evictIndex].fileOffset;
//                             SetFilePointerEx(fileHandle, evictPos, nullptr, FILE_BEGIN);
//                             DWORD bytesWritten;
//                             WriteFile(fileHandle, cache[evictIndex].data, CACHE_BLOCK, &bytesWritten, nullptr);
//                         }
//                         cache.erase(evictIndex);
//                         currentCacheSize -= CACHE_BLOCK;
//                     }
//                 }
//
//                 // Чтение данных из кэша
//                 const CacheBlock& block = cache[blockIndex];
//                 memcpy(dest, block.data + blockOffset, bytesToRead);
//
//                 dest += bytesToRead;
//                 count -= bytesToRead;
//                 bytesRead += bytesToRead;
//                 currentOffset += bytesToRead;
//             }
//
//             return bytesRead;
//         }
//
//         // Запись данных в файл.
//         ssize_t lab2_write(const void* buf, size_t count) {
//             const char* src = static_cast<const char*>(buf);
//             size_t bytesWritten = 0;
//
//             while (count > 0) {
//                 size_t blockIndex = currentOffset / CACHE_BLOCK;
//                 size_t blockOffset = currentOffset % CACHE_BLOCK;
//                 size_t bytesToWrite = std::min(CACHE_BLOCK - blockOffset, count);
//
//                 auto it = cache.find(blockIndex);
//                 if (it == cache.end()) {
//                     // Загружаем блок в кэш
//                     CacheBlock newBlock{};
//                     newBlock.fileOffset = blockIndex * CACHE_BLOCK;
//                     newBlock.dirty = false;
//
//                     LARGE_INTEGER filePos;
//                     filePos.QuadPart = newBlock.fileOffset;
//                     SetFilePointerEx(fileHandle, filePos, nullptr, FILE_BEGIN);
//
//                     DWORD bytesReadFromFile;
//                     if (!ReadFile(fileHandle, newBlock.data, CACHE_BLOCK, &bytesReadFromFile, nullptr)) {
//                         return -1;
//                     }
//
//                     cache[blockIndex] = newBlock;
//                     mruList.push_front(blockIndex);
//                     currentCacheSize += CACHE_BLOCK;
//
//                     if (currentCacheSize > CACHE_SIZE) {
//                         size_t evictIndex = mruList.back();
//                         mruList.pop_back();
//                         if (cache[evictIndex].dirty) {
//                             LARGE_INTEGER evictPos;
//                             evictPos.QuadPart = cache[evictIndex].fileOffset;
//                             SetFilePointerEx(fileHandle, evictPos, nullptr, FILE_BEGIN);
//                             DWORD bytesWrittenToFile;
//                             WriteFile(fileHandle, cache[evictIndex].data, CACHE_BLOCK, &bytesWrittenToFile, nullptr);
//                         }
//                         cache.erase(evictIndex);
//                         currentCacheSize -= CACHE_BLOCK;
//                     }
//                 }
//
//                 CacheBlock& block = cache[blockIndex];
//                 memcpy(block.data + blockOffset, src, bytesToWrite);
//                 block.dirty = true;
//
//                 src += bytesToWrite;
//                 count -= bytesToWrite;
//                 bytesWritten += bytesToWrite;
//                 currentOffset += bytesToWrite;
//             }
//
//             return bytesWritten;
//         }
//         // Перестановка позиции указателя на данные файла. Достаточно поддержать только абсолютные координаты. Пример:
//         off_t lab2_lseek(int fd, off_t offset, int whence) {
//             LARGE_INTEGER newPos;
//             LARGE_INTEGER moveOffset;
//             moveOffset.QuadPart = offset;
//
//             DWORD moveMethod;
//             switch (whence) {
//                 case SEEK_SET: moveMethod = FILE_BEGIN; break;
//                 case SEEK_CUR: moveMethod = FILE_CURRENT; break;
//                 case SEEK_END: moveMethod = FILE_END; break;
//                 default: return -1;
//             }
//
//             if (!SetFilePointerEx(fileHandle, moveOffset, &newPos, moveMethod)) {
//                 return -1;
//             }
//
//             currentOffset = newPos.QuadPart;
//             return currentOffset;
//         }
//         // Синхронизация данных из кэша с диском. Пример:
//         int lab2_fsync(HANDLE handle) {
//             for (auto& [blockIndex, block] : cache) {
//                 if (block.dirty) {
//                     LARGE_INTEGER blockPos;
//                     blockPos.QuadPart = block.fileOffset;
//                     SetFilePointerEx(handle, blockPos, nullptr, FILE_BEGIN);
//
//                     DWORD bytesWritten;
//                     if (!WriteFile(handle, block.data, CACHE_BLOCK, &bytesWritten, nullptr)) {
//                         return -1; // Ошибка записи
//                     }
//                     block.dirty = false;
//                 }
//             }
//             return 0;
//         }
//
//         void print_stat() const {
//             cout << "File stats:" << endl;
//             cout << "currentOffset: " << currentOffset << endl;
//             cout << "cache.size: " << cache.size() << endl;
//             cout << "fileHandle: " << fileHandle << endl;
//         }
//
//     HANDLE fileHandle = nullptr;
//
//     private:
//     unordered_map<size_t, CacheBlock> cache; // Кэш: блоки файла по индексу
//     list<size_t> mruList; // Очерёдность блоков для MRU
//     off_t currentOffset = 0;  // Текущее смещение указателя
//     size_t currentCacheSize = 0; // Текущий размер кэша в байтах
//
// };
