#include <windows.h>
#include <iostream>
#include <fstream>
#include <vector>
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

int readFileByBlocks(const int repetitions, const string& filePath) {
    for (int i = 0; i < repetitions; i++) {
        HANDLE hFile = CreateFile(
            filePath.c_str(),
            GENERIC_READ,
            FILE_SHARE_READ,
            nullptr,
            OPEN_EXISTING,
            FILE_FLAG_NO_BUFFERING | FILE_FLAG_SEQUENTIAL_SCAN,
            nullptr
            );
        if (hFile == INVALID_HANDLE_VALUE) {
            cerr << "Can't open file " << filePath << endl;
            return 1;
        }

        vector<char> buffer(BLOCK_SIZE);
        DWORD bytesRead = 0;
        DWORD totalBytesRead = 0;
        const auto start = GetTickCount64();

        while (ReadFile(hFile, buffer.data(), BLOCK_SIZE, &bytesRead, nullptr) && bytesRead > 0) {
            totalBytesRead += bytesRead;
            cout << totalBytesRead << endl;
        }

        const auto end = GetTickCount64();
        const double duration = (end - start) / 1000.0;

        string report;
        report += "Bytes read: " + totalBytesRead + '\n';
        report += "Execution time: " + to_string(duration) + " seconds" + '\n';
        report += "Byte/sec: " + to_string(totalBytesRead / duration) + '\n';
        cout << report;
        CloseHandle(hFile);
    }
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
