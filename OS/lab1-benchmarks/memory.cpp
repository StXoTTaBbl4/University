#include <windows.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <string>

namespace {
    constexpr DWORD BLOCK_SIZE = 2048; // 2K block size

    void readFileByBlocks(int repetitions, const std::string& filePath) {
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
                std::cerr << "Can't open file " << filePath << std::endl;
                return;
            }

            std::vector<char> buffer(BLOCK_SIZE);
            DWORD bytesRead = 0;
            DWORD totalBytesRead = 0;
            auto start = GetTickCount64();

            while (ReadFile(hFile, buffer.data(), BLOCK_SIZE, &bytesRead, NULL) && bytesRead > 0) {
                totalBytesRead += bytesRead;
            }

            auto end = GetTickCount64();
            double duration = (end - start) / 1000.0;

            std::cout << "Bytes read: " << totalBytesRead << '\n';
            std::cout << "Execution time: " << duration << " seconds" << '\n';
            std::cout << "Byte/sec: " << totalBytesRead / duration << '\n';
            std::cout << '\n';
            CloseHandle(hFile);
        }
    }
}

int main(int argc, char* argv[]) {
    if(argc < 1) {
        std::cerr << "Usage: " << "path to file" << '\n';
    } else {
        readFileByBlocks(atoi(argv[1]) ,argv[2]);
    }

}
