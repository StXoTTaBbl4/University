#include "library.h"
#include <chrono>
#include <fstream>

using namespace std;

void windowsCache(const string& filePath) {
    char buffer[CACHE_BLOCK];

    ifstream file(filePath, ios::binary);
    if (!file.is_open()) {
        throw runtime_error("Failed to open file: " + filePath);
    }

    cout << "\n=====WINDOWS CACHE::STREAM=====" << endl;
    for (int i = 0; i < 2; i++) {
        cout << "Iteration: " << i << endl;
        const auto t1 = chrono::high_resolution_clock::now();

        while (file.read(buffer, CACHE_BLOCK) || file.gcount() > 0) {
            // Здесь могла быть ваша реклама
        }

        const auto t2 = chrono::high_resolution_clock::now();
        auto ms_int = duration_cast<chrono::milliseconds>(t2 - t1);
        cout << "   " << ms_int.count() << "ms\n";
    }
}

void windowsCacheHandle(const string& filePath) {
    HANDLE hFile = CreateFile(
        filePath.c_str(),
        GENERIC_READ,
        FILE_SHARE_READ,
        nullptr,
        OPEN_EXISTING,
        FILE_ATTRIBUTE_NORMAL,
        nullptr
    );

    if (hFile == INVALID_HANDLE_VALUE) {
        cerr << "File open error: " << GetLastError() << endl;
        return;
    }

    char buffer[CACHE_BLOCK];
    DWORD bytesRead = 0;

    cout << "\n=====WINDOWS CACHE::HANDLE=====" << endl;
    for (int i = 0; i < 2; i++) {
        cout << "Iteration: " << i << endl;
        const auto t1 = chrono::high_resolution_clock::now();
        while (true) {
            if (!ReadFile(hFile, buffer, CACHE_BLOCK, &bytesRead, nullptr)) {
                cerr << "File read error: " << GetLastError() << endl;
                break;
            }
            if (bytesRead == 0) {
                break;
            }
        }
        const auto t2 = chrono::high_resolution_clock::now();
        auto ms_int = duration_cast<chrono::milliseconds>(t2 - t1);
        cout << "   " << ms_int.count() << "ms\n";
    }

    // Закрываем файл
    CloseHandle(hFile);
}

int main() {
    MruCache mru_cache;
    HANDLE handle = mru_cache.lab2_open(R"(D:\ITMO\3-re\testfile)");
    // mru_cache.print_stat();
    //
    // mru_cache.lab2_close(handle);

    char buffer[CACHE_BLOCK];
    ssize_t bytesRead;

    cout << "=====OWN CACHE=====" << endl;
    for (int i = 0; i < 2; i++) {
        cout << "Iteration: " << i << endl;
        auto t1 = chrono::high_resolution_clock::now();
        while ((bytesRead = mru_cache.lab2_read(buffer, CACHE_BLOCK)) > 0) {
            // cout << "Bytes read: " << bytesRead << endl;
        }
        auto t2 = chrono::high_resolution_clock::now();
        auto ms_int = duration_cast<chrono::milliseconds>(t2 - t1);
        cout << "   " << ms_int.count() << "ms\n";
        if (bytesRead == 0) {
            // std::cout << "Finished reading: EOF reached!" << std::endl;
        } else if (bytesRead < 0) {
            std::cerr << "Error reading file" << std::endl;
        }
    }

    char to_write[8] = {'h', 'e', 'l', 'l', 'o',',','m','e'};

    mru_cache.lab2_write(to_write, 5);

    mru_cache.lab2_close(handle, true);

    windowsCache(R"(D:\ITMO\3-re\testfile)");

    windowsCacheHandle(R"(D:\ITMO\3-re\testfile)");

    return 0;
 }
