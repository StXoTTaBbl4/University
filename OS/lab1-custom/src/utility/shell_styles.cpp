#include <iostream>
#include <windows.h>
#include <wtsapi32.h>
#include <string>
#include <sstream>
#include <chrono>
#include <fstream>
#include <thread>
#include "../utility/shell_styles.h"


using namespace std;

void PrintLogo(const string& filePath) {
    ifstream file(filePath);

    if (!file.is_open()) {
        cerr << "Can not open the file: " << filePath << std::endl;
        return;
    }

    string line;
    while (getline(file, line)) {
        cout << line << endl;
    }

    file.close();
}

bool SetConsoleStyle() {
    SetConsoleTitle("Custom shell");
    HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
    if (hConsole == INVALID_HANDLE_VALUE) {
        std::cerr << "Can not get console descriptor: " << GetLastError() << std::endl;
        return false;
    }

    CONSOLE_SCREEN_BUFFER_INFOEX csbiex;
    ZeroMemory(&csbiex, sizeof(csbiex));
    csbiex.cbSize = sizeof(CONSOLE_SCREEN_BUFFER_INFOEX);

    if (!GetConsoleScreenBufferInfoEx(hConsole, &csbiex)) {
        cerr << "Can not get buffer info: " << GetLastError() << std::endl;
        return false;
    }

    csbiex.ColorTable[6] = RGB(255, 165, 0);

    if (!SetConsoleScreenBufferInfoEx(hConsole, &csbiex)) {
        cerr << "Can not set buffer info: " << GetLastError() << std::endl;
        return false;
    }

    if (!SetConsoleTextAttribute(hConsole, FOREGROUND_RED | FOREGROUND_GREEN)) {
        cerr << "Can not set text color: " << GetLastError() << std::endl;
        return false;
    }

    return true;
}