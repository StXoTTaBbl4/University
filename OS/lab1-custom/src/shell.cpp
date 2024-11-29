#include <iostream>
#include <windows.h>
#include <wtsapi32.h>
#include <string>
#include <sstream>
#include <chrono>
#include <fstream>
#include <thread>
#include <vector>


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

    // Получаем текущую информацию о буфере
    CONSOLE_SCREEN_BUFFER_INFOEX csbiex;
    ZeroMemory(&csbiex, sizeof(csbiex));
    csbiex.cbSize = sizeof(CONSOLE_SCREEN_BUFFER_INFOEX);

    if (!GetConsoleScreenBufferInfoEx(hConsole, &csbiex)) {
        cerr << "Can not get buffer info: " << GetLastError() << std::endl;
        return false;
    }

    // Задаем оранжевый цвет (пример: заменим индекс 6 на RGB оранжевого)
    csbiex.ColorTable[6] = RGB(255, 165, 0); // Оранжевый

    // Устанавливаем обновленные настройки
    if (!SetConsoleScreenBufferInfoEx(hConsole, &csbiex)) {
        cerr << "Can not set buffer info: " << GetLastError() << std::endl;
        return false;
    }

    // Устанавливаем текст оранжевым цветом
    if (!SetConsoleTextAttribute(hConsole, FOREGROUND_RED | FOREGROUND_GREEN)) {
        cerr << "Can not set text color: " << GetLastError() << std::endl;
        return false;
    }

    return true;
}

vector<string> ParseCommand(const string& input) {
    stringstream ss(input);
    string segment;
    vector<string> tokens;

    while (ss >> segment) {
        tokens.push_back(segment);
    }

    return tokens;
}

bool RunCommand(LPSTR command) {
    const auto start = GetTickCount64();
    HANDLE hToken = nullptr;
    HANDLE hDuplicateToken = nullptr;

    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    HANDLE hStdOutRead = nullptr, hStdOutWrite = nullptr, hStdInput = nullptr;
    SECURITY_ATTRIBUTES sa = { sizeof(SECURITY_ATTRIBUTES), nullptr, TRUE };

    si.cb = sizeof(STARTUPINFO);

    // канал для перенаправления вывода
    if (!CreatePipe(&hStdOutRead, &hStdOutWrite, &sa, 0)) {
        cerr << "Failed to create pipe. Error: " << GetLastError() << endl;
        return false;
    }

    // запись в канал как неунаследованная
    if (!SetHandleInformation(hStdOutRead, HANDLE_FLAG_INHERIT, 0)) {
        cerr << "Failed to set handle information. Error: " << GetLastError() << endl;
        CloseHandle(hStdOutRead);
        CloseHandle(hStdOutWrite);
        return false;
    }

    // перенаправление стандартного вывода
    si.dwFlags = STARTF_USESTDHANDLES;
    si.hStdOutput = hStdOutWrite;
    si.hStdError = hStdOutWrite;
    si.hStdInput = hStdInput;
    si.wShowWindow = SW_SHOW;

    // токен процесса
    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_DUPLICATE | TOKEN_ASSIGN_PRIMARY | TOKEN_QUERY, &hToken)) {
        cerr << "Failed to get process token. Error: " << GetLastError() << endl;
        CloseHandle(hStdOutRead);
        CloseHandle(hStdOutWrite);
        return false;
    }

    if (!DuplicateTokenEx(hToken, MAXIMUM_ALLOWED, nullptr, SecurityImpersonation, TokenPrimary, &hDuplicateToken)) {
        cerr << "Failed to duplicate token. Error: " << GetLastError() << endl;
        CloseHandle(hStdOutRead);
        CloseHandle(hStdOutWrite);
        CloseHandle(hToken);
        return false;
    }

    if (!CreateProcessAsUser(
        hDuplicateToken,         // Токен пользователя
        nullptr,                    // Имя исполняемого файла
        command,                 // Команда
        nullptr,                    // Атрибуты безопасности процесса
        nullptr,                    // Атрибуты безопасности потока
        TRUE,                    // Унаследовать дескрипторы
        CREATE_NEW_CONSOLE,        // Не показывать окно
        nullptr,                    // Переменные окружения
        nullptr,                    // Текущий каталог
        &si,                     // Информация о старте
        &pi                      // Информация о процессе
    )) {
        cerr << "Failed to create process. Error: " << GetLastError() << endl;
        CloseHandle(hStdOutRead);
        CloseHandle(hStdOutWrite);
        CloseHandle(hToken);
        CloseHandle(hDuplicateToken);
        return false;
    }

    // WaitForSingleObject(pi.hProcess, INFINITE);

    CloseHandle(hStdOutWrite);
    CloseHandle(hToken);
    CloseHandle(hDuplicateToken);

    char buffer[4096];
    DWORD bytesRead;
    while (ReadFile(hStdOutRead, buffer, sizeof(buffer) - 1, &bytesRead, nullptr) && bytesRead > 0) {
        buffer[bytesRead] = '\0';
        cout << buffer;
    }

    WaitForSingleObject(pi.hProcess, INFINITE);
    CloseHandle(hStdOutRead);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    const auto end = GetTickCount64();

    cout<< "Execution time: " << end - start << " ms" << endl;

    return true;
}

bool RunThreads(int number_of_threads, LPSTR command) {
    vector<thread> threads;

    for (int i = 0; i < number_of_threads; ++i) {
        threads.emplace_back(RunCommand, command);
    }

    for (auto& thread : threads) {
        thread.join();
    }

    std::cout << "All threads have finished.\n";
    return true;
}

void RunShell() {
    SetConsoleStyle();
    PrintLogo(R"(D:\ITMO\3-re\University\OS\lab1-custom\src\logo.txt)");
    char current_directory[MAX_PATH];

    while (true) {

        GetCurrentDirectory(MAX_PATH, current_directory);
        cout <<"cstShell>" << current_directory << ">";

        string input;
        getline(cin, input);

        vector<string> tokens = ParseCommand(input);
        if (tokens.empty()) { continue;}

        string command = tokens[0];

        if (command == "exit") {
            break;
        }

        if (command == "cd") {
            if (tokens.size() == 2) {
                if (!SetCurrentDirectory(tokens[1].c_str())) {
                    cerr << "Failed to change directory: " << tokens[1] << '\n';
                }
            } else {
                cerr << "Usage: cd <directory>" << '\n';
            }
            continue;
        }

        if (command == "threads") {
            if (tokens.size() >= 3) {
                char* end;
                const int value = strtol(tokens[1].c_str(), &end, 10);
                if (*end != '\0' || value <= 0 || value > INT_MAX) {
                    cerr << "Error: value must be more than 0 and less than INT_MAX\n";
                    continue;
                }

                string target_command;
                for (int i = 2; i < tokens.size() ; i++ ) {
                    target_command += tokens[i] + " ";
                }

                RunThreads(value, &target_command[0]);
            } else {
                cerr << "Usage: threads amount<integer> command" << '\n';
            }

            continue;
        }

        string full_command;
        for (const auto& token : tokens) {
            full_command += token + " ";
        }

        RunCommand(&full_command[0]);
    }
}

int main() {
    RunShell();
    return 0;

}

