#include <windows.h>
#include <iostream>
#include <fstream>
#include <string>
#include "io_benchmark.h"


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

// int readFileByBlocks(const int repetitions, const string& filePath) {
//     for (int i = 0; i < repetitions; i++) {
//         HANDLE hFile = CreateFile(
//             filePath.c_str(),
//             GENERIC_READ,
//             FILE_SHARE_READ,
//             nullptr,
//             OPEN_EXISTING,
//             FILE_FLAG_NO_BUFFERING | FILE_FLAG_SEQUENTIAL_SCAN,
//             nullptr
//             );
//         if (hFile == INVALID_HANDLE_VALUE) {
//             cerr << "Can't open file " << filePath << endl;
//             return 1;
//         }
//
//         vector<char> buffer(BLOCK_SIZE);
//         DWORD bytesRead = 0;
//         DWORD totalBytesRead = 0;
//         const auto start = GetTickCount64();
//
//         while (ReadFile(hFile, buffer.data(), BLOCK_SIZE, &bytesRead, nullptr) && bytesRead > 0) {
//             totalBytesRead += bytesRead;
//             cout << totalBytesRead << endl;
//         }
//
//         const auto end = GetTickCount64();
//         const double duration = (end - start) / 1000.0;
//
//         string report;
//         report += "Bytes read: " + totalBytesRead + '\n';
//         report += "Execution time: " + to_string(duration) + " seconds" + '\n';
//         report += "Byte/sec: " + to_string(totalBytesRead / duration) + '\n';
//         cout << report;
//         CloseHandle(hFile);
//     }
//     return 0;
// }

int readFileByBlocks(const int iterations, const string& filePath) {

    HANDLE file = CreateFileA(
        filePath.c_str(),
        GENERIC_READ,
        FILE_SHARE_READ,
        nullptr,
        OPEN_EXISTING,
        FILE_FLAG_NO_BUFFERING,
        nullptr
    );

    if (file == INVALID_HANDLE_VALUE) {
        std::cerr << "Не удалось открыть файл: " << GetLastError() << std::endl;
        return 1;
    }

    // Выделение выровненного буфера с помощью VirtualAlloc
    void* buffer = VirtualAlloc(nullptr, BLOCK_SIZE, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (!buffer) {
        std::cerr << "Не удалось выделить буфер: " << GetLastError() << std::endl;
        CloseHandle(file);
        return 1;
    }

    LARGE_INTEGER frequency, startTime, endTime;
    QueryPerformanceFrequency(&frequency);

    for (int iteration = 0; iteration < iterations; ++iteration) {
        // Сброс указателя файла в начало
        if (SetFilePointer(file, 0, nullptr, FILE_BEGIN) == INVALID_SET_FILE_POINTER) {
            std::cerr << "Не удалось переместить указатель файла: " << GetLastError() << std::endl;
            VirtualFree(buffer, 0, MEM_RELEASE);
            CloseHandle(file);
            return 1;
        }

        DWORD bytesRead;
        DWORD64 totalBytesRead = 0;

        // Измерение времени чтения
        QueryPerformanceCounter(&startTime);

        // Чтение файла блоками
        while (ReadFile(file, buffer, BLOCK_SIZE, &bytesRead, nullptr) && bytesRead > 0) {
            totalBytesRead += bytesRead;
        }

        QueryPerformanceCounter(&endTime);

        // Вычисление времени чтения
        double elapsedTime = static_cast<double>(endTime.QuadPart - startTime.QuadPart) / frequency.QuadPart;

        std::cout << "Iteration " << (iteration + 1) << ":\n";
        std::cout << "  Bytes read: " << totalBytesRead << std::endl;
        std::cout << "  Time: " << elapsedTime << " seconds" << std::endl;
    }

    // Очистка ресурсов
    VirtualFree(buffer, 0, MEM_RELEASE);
    CloseHandle(file);

    return 0;
}


int main(const int argc, char* argv[]) {
    int repetitions;
    string filePath;

    if (!validateArguments(argc, argv, repetitions, filePath)) {
        return 1;
    }

    return readFileByBlocks(repetitions ,filePath);
}
