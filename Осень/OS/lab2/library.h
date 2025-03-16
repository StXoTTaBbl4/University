#ifndef MRU_CACHE_H
#define MRU_CACHE_H

#include <iostream>
#include <list>
#include <windows.h>
#include <memory>
#include <unordered_map>
#include <algorithm>
#include <_mingw_off_t.h>

// Макросы для DLL экспорта/импорта
#ifdef MRUCACHE_EXPORTS
#define MRUCACHE_API __declspec(dllexport)
#else
#define MRUCACHE_API __declspec(dllimport)
#endif

#define CACHE_SIZE 100000
#define CACHE_BLOCK 4096

class MRUCACHE_API MruCache {
public:
    MruCache();
    ~MruCache();

    // Открытие файла по заданному пути, доступного для чтения
    HANDLE lab2_open(const char *path);

    // Закрытие файла по хэндлу
    int lab2_close(HANDLE handle, bool synchronize);

    // Чтение данных из файла
    size_t lab2_read(void *buf, size_t count);

    // Запись данных в файл
    ssize_t lab2_write(const void *buf, size_t count);

    // Перестановка позиции указателя на данные файла
    off_t lab2_lseek(int fd, off_t offset, int whence);

    // Синхронизация данных из кэша с файлом
    int lab2_fsync(HANDLE handle);

    // Вывод статистики
    void print_stat() const;

private:
    struct CacheBlock {
        char data[4096];
        bool dirty;
        off_t fileOffset;
    };

    HANDLE fileHandle = nullptr;
    std::unordered_map<size_t, CacheBlock> cache;
    std::list<size_t> mruList;
    size_t currentOffset = 0;
    size_t currentCacheSize = 0;

    std::mutex access_mutex;
};

#endif // MRU_CACHE_H
