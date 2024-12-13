#include <iostream>
#include <windows.h>
#include <wtsapi32.h>
#include <string>
#include <sstream>
#include <fstream>
#include <thread>
#include <vector>
#include "bits/stdc++.h"

#include "./utility/shell_styles.h"

using namespace std;

/**
 * Структура, описывающая обработанную команду, с выставленными флагами перенаправления вывода и дублирования потоков
 */
struct ParsedCommand {
    /// Команда, которою нужновыполнить
    string full_command;
    /// Символ перенаправления `<<` | `>>` (ввод | вывод)
    string redirectKey;
    /// Путь до файла перенаправления IO
    string filepath;
    /// Количество потоков для создания (если используется)
    size_t thread_count = 0;
    /// Флаг использования потоков
    boolean threads = false;
    /// Флаг присутсвия перенаправления
    boolean redirected = false;
    /// Флаг корректности переданной функции
    boolean valid = false;
};

mutex write_mutex;

/**
 * Функция для обработки команды
 * @param input Команда
 * @return Команда в виде массива
 */
vector<string> ParseInput(const string& input) {
    stringstream ss(input);
    string segment;
    vector<string> tokens;

    while (ss >> segment) {
        tokens.push_back(segment);
    }

    return tokens;
}

/**
 * Функция возвращает команду в виде структуры с проставленными флагами и параметрами
 * @param input Команда в виде массива
 * @return Структура `ParsedCommand` с командой
 */
ParsedCommand ParseCommand(const vector<string>& input) {
    ParsedCommand result;
    const size_t input_size = input.size();

    if (input_size == 0) {
        cout << "Input command is empty" << endl;
        return result;
    }

    size_t i = 0;

    if (input[i] == "threads") {
        result.threads = true;
        if (input_size >= 3) {
            char* end;
            const int value = strtol(input[i+1].c_str(), &end, 10);
            if (*end != '\0' || value <= 1 || value > INT_MAX) {
                cerr << "Error: thread count must be more than 1 and less than INT_MAX\n";
                return result;
            }
            i = 2;
            result.thread_count = value;
        } else {
            cout <<  "No command to run specified" << endl;
            return result;
        }
    }

    for (;i < input_size; ++i) {
        const string& token = input[i];

        if (token == ">>" || token == "<<") {
            result.redirectKey = token;

            if (i + 1 < input_size) {
                result.filepath = input[i + 1];
                result.redirected = true;
                break;
            } else {
                cout <<  "File path is missing after redirection operator" << endl;
                return result;
            }
        }

        result.full_command += token + " ";
    }

    if (!result.redirectKey.empty() && result.filepath.empty()) {
        cout << "Redirection operator found without a file path" << endl;
        return result;
    }

    result.valid = true;
    return result;
}

/**
 * Функция для записи вывода программ в файл
 * @param filePath Путь к файлу для записи
 * @param buffer Буфер с данными
 */
void SynchronizedWriteToFile(const string& filePath, const vector<char>& buffer) {
    lock_guard lock(write_mutex);

    ofstream file(filePath, ios::binary | ios::app);
    if (!file.is_open()) {
        throw runtime_error("Unable to open file: " + filePath);
    }

    file.write(buffer.data(), static_cast<long long>(buffer.size()));

    if (!file) {
        throw runtime_error("Error writing to file: " + filePath);
    }

}

/**
 * Функция для запуска программы с помощью `CreateProcessAsUser`
 * @param parsed_command Структура `ParsedCommand` с командой
 * @return true в случае успешного запуска программы, иначе - false
 */
bool RunCommand(const ParsedCommand& parsed_command) {

    const auto start = GetTickCount64();
    HANDLE hToken = nullptr;
    HANDLE hDuplicateToken = nullptr;
    HANDLE hReadPipe, hWritePipe;

    SECURITY_ATTRIBUTES sa = { sizeof(SECURITY_ATTRIBUTES), nullptr, TRUE };
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    si.cb = sizeof(STARTUPINFO);
    si.dwFlags = STARTF_USESTDHANDLES;
    si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
    si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);


    if (parsed_command.redirected && parsed_command.redirectKey == ">>") {
        if (!CreatePipe(&hReadPipe, &hWritePipe, &sa, 0)) {
            cerr << "Failed to create pipe.\n";
            return false;
        }

        if (!SetHandleInformation(hWritePipe, HANDLE_FLAG_INHERIT, HANDLE_FLAG_INHERIT)) {
            cerr << "Failed to set handle information.\n";
            CloseHandle(hReadPipe);
            CloseHandle(hWritePipe);
            return false;
        }

        si.hStdOutput = hWritePipe;
    } else {
        si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
    }

    auto command = const_cast<LPSTR>(parsed_command.full_command.c_str());

    // токен процесса
    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_DUPLICATE | TOKEN_ASSIGN_PRIMARY | TOKEN_QUERY, &hToken)) {
        cerr << "Failed to get process token. Error: " << GetLastError() << endl;
        return false;
    }

    if (!DuplicateTokenEx(hToken, MAXIMUM_ALLOWED, nullptr, SecurityImpersonation, TokenPrimary, &hDuplicateToken)) {
        cerr << "Failed to duplicate token. Error: " << GetLastError() << endl;
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
        0,
        nullptr,                    // Переменные окружения
        nullptr,                    // Текущий каталог
        &si,                     // Информация о старте
        &pi                      // Информация о процессе
    )) {
        cerr << "Failed to create process. Error: " << GetLastError() << endl;
        CloseHandle(hToken);
        CloseHandle(hDuplicateToken);
        return false;
    }

    cout << "PID: " << pi.dwProcessId << endl;

    WaitForSingleObject(pi.hProcess, INFINITE);
    TerminateProcess(pi.hProcess, 0);

    const auto end = GetTickCount64();

    cout<< "Execution time: "<< end-start << endl;

    if (parsed_command.redirected) {
        if (!CloseHandle(hWritePipe)) {
            cerr << "Failed to close pipe. Error: " << GetLastError() << endl;
            return false;
        }

        DWORD bytesAvailable = 0;
        vector<char> buffer(bytesAvailable);
        DWORD bytesRead;

        if (!PeekNamedPipe(hReadPipe, nullptr, 0, nullptr, &bytesAvailable, nullptr)) {
            cerr << "Failed to peek into pipe. Error: " << GetLastError() << "\n";
        }

        if (bytesAvailable > 0) {
            buffer.resize(bytesAvailable);
            if (ReadFile(hReadPipe, buffer.data(), bytesAvailable, &bytesRead, nullptr) && bytesRead > 0) {
                SynchronizedWriteToFile(parsed_command.filepath, vector<char>(buffer.begin(), buffer.begin() + bytesRead));
            }
        }
    }

    CloseHandle(hReadPipe);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    return true;
}

/**
 * Функция для запуска нескольких экземпляров программы с помощью `CreateProcessAsUser`
 * @param parsed_command Структура `ParsedCommand` с командой
 * @return true в случае успешного запуска потоков, иначе - false
 */
bool RunThreads(const ParsedCommand& parsed_command) {
    vector<thread> threads;

    if (!parsed_command.valid) {
        return false;
    }

    for (size_t i = 0; i < parsed_command.thread_count; ++i) {
        threads.emplace_back(RunCommand, parsed_command);
    }

    for (auto& thread : threads) {
        thread.join();
    }

    cout << "All threads have finished.\n";
    return true;
}

/**
 * Метод отвечает за работу окна командной строки, обрабатывая пользовательский ввод до тех пор,
 * пока не будет введена команда `exit`
 */
void RunShell() {
    SetConsoleStyle();
    PrintLogo("../src/logo.txt");

    while (true) {
        char current_directory[MAX_PATH];

        GetCurrentDirectory(MAX_PATH, current_directory);
        cout <<"cstShell>" << current_directory << ">";

        string input;
        getline(cin, input);

        vector<string> tokens = ParseInput(input);
        if (tokens.empty()) { continue;}

        const string& command = tokens[0];

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

        ParsedCommand parsed_command = ParseCommand(tokens);

        if (parsed_command.valid) {
            if (parsed_command.redirected && parsed_command.redirectKey == "<<") {
                if (freopen(parsed_command.filepath.c_str(), "r", stdin) != nullptr) {
                    freopen("CONIN$", "r", stdin);
                    continue;
                }
                cerr << "Ошибка при открытии файла для ввода\n";
                continue;
            }

            if (parsed_command.threads) {
                RunThreads(parsed_command);
            }
            RunCommand(parsed_command);
        }

    }
}

/**
 * Метод запускает шелл
 * @return Код завершения
 */
int main() {
    RunShell();
    return 0;
}

