#include <windows.h>
#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include "lib/headers/library.h"

using namespace std;

constexpr DWORD BLOCK_SIZE = 2048; // 2K block size

bool validateArguments(const int argc, char* argv[], int& repetitions, string& filePath) {
    if (argc < 3) {
        cerr << "Usage: repetitions<integer> path<string>\n";
        return false;
    }

    char* end;

    const long rep = strtol(argv[1], &end, 10);
    if (*end != '\0' || rep <= 0 || rep > INT_MAX) {
        cerr << "Error: repetitions must be a positive integer less than or equal to INT_MAX\n";
        return false;
    }

    repetitions = static_cast<int>(rep);

    filePath = argv[2];
    if (const ifstream file(filePath); !file) {
        cerr << "Error: file '" << filePath << "' does not exist or cannot be opened\n";
        return false;
    }

    // Если все проверки пройдены, возвращаем true
    return true;
}

int readFileByBlocks(const int iterations, const string& filePath) {

    uint64_t fd = lab2_open(filePath.c_str());

    LARGE_INTEGER frequency, startTime, endTime;
    QueryPerformanceFrequency(&frequency);

    for (int iteration = 0; iteration < iterations; ++iteration) {
        lab2_lseek(fd, 0, FILE_BEGIN);

        char buffer[cache.GetFileSize(fd)];

        DWORD bytesRead = QueryPerformanceCounter(&startTime);

        lab2_read(fd, cache.GetFileSize(fd), &bytesRead);

        QueryPerformanceCounter(&endTime);

        double elapsedTime = static_cast<double>(endTime.QuadPart - startTime.QuadPart) / frequency.QuadPart;

        cout << "Iteration " << (iteration + 1) << ":\n";
        cout << "  Bytes read: " << bytesRead << endl;
        cout << "  Time: " << elapsedTime << " seconds" << endl;
    }

    // Очистка ресурсов
    cache.evict();

    return 0;
}

// int main()
// {
// #ifdef CACHEDIOTEST_EXPORTS
//     printf("\nCACHEDIOTEST_EXPORTS found\n");
// #else
//     printf("\nCACHEDIOTEST_EXPORTS not found\n");
// #endif
//
// #ifdef CACHEDIOTEST_API
//     printf("\nCACHEDIOTEST_API found\n");
// #else
//     printf("\nCACHEDIOTEST_API not found\n");
// #endif
//     return 0;
// }

int main(const int argc, char* argv[]) {
    int repetitions;
    string filePath;

    if (!validateArguments(argc, argv, repetitions, filePath)) {
        return 1;
    }

    return readFileByBlocks(repetitions ,filePath);
}

