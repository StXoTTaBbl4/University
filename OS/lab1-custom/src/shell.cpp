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

struct ParsedCommand {
    string full_command;
    string redirectKey;
    string filepath;
    boolean valid = false;
    boolean redirected = false;
};

vector<string> ParseInput(const string& input) {
    stringstream ss(input);
    string segment;
    vector<string> tokens;

    while (ss >> segment) {
        tokens.push_back(segment);
    }

    return tokens;
}


ParsedCommand ParseCommand(const vector<string>& input) {
    ParsedCommand result;
    size_t input_size = input.size();

    if (input_size == 0) {
        cout << "Input command is empty" << endl;
        return result;
    }

    for (size_t i = 0; i < input_size; ++i) {
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

        if (!result.redirectKey.empty()) {
            break;
        }

        if (!result.full_command.empty()) {
            result.full_command += " ";
        }
        result.full_command += token;
    }

    if (!result.redirectKey.empty() && result.filepath.empty()) {
        cout << "Redirection operator found without a file path" << endl;
        return result;
    }

    result.valid = true;
    return result;
}


bool RunCommand(const ParsedCommand& parsed_command) {

    const auto start = GetTickCount64();
    HANDLE hToken = nullptr;
    HANDLE hDuplicateToken = nullptr;

    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    si.cb = sizeof(STARTUPINFO);
    si.dwFlags = STARTF_USESTDHANDLES;


    if (parsed_command.redirected == true) {
        if(parsed_command.redirectKey == "<<") {
            HANDLE hInput = CreateFile(parsed_command.filepath.c_str(), GENERIC_READ, FILE_SHARE_READ, nullptr, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, nullptr);
            if (hInput == INVALID_HANDLE_VALUE) {
                cerr << "Failed to open file for redirection: " << parsed_command.filepath << endl;
                return false;
            }
            si.hStdInput = hInput;
            si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
            si.hStdError = GetStdHandle(STD_ERROR_HANDLE);
        }
        if (parsed_command.redirectKey == ">>") {
            HANDLE hOutput = CreateFile(parsed_command.filepath.c_str(), GENERIC_WRITE, FILE_SHARE_WRITE, nullptr, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, nullptr);
            if (hOutput == INVALID_HANDLE_VALUE) {
                cerr << "Failed to open file for redirection." << parsed_command.filepath << endl;
                return false;
            }
            si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
            si.hStdOutput = hOutput;
            si.hStdError = hOutput;
        }
    } else {
        si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
        si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
        si.hStdError = GetStdHandle(STD_ERROR_HANDLE);
        // si.wShowWindow = SW_SHOW;
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
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    if (parsed_command.redirectKey == ">>") {
        CloseHandle(si.hStdOutput);
        CloseHandle(si.hStdError);
    }
    if (parsed_command.redirectKey == "<<") {
        CloseHandle(si.hStdInput);
    }

    const auto end = GetTickCount64();

    cout<< "Execution time: "<< end-start << endl;

    return true;
}

bool RunThreads(const int number_of_threads, const string& command) {
    vector<thread> threads;
    ParsedCommand parsed_command = ParseCommand(ParseInput(command));

    if (parsed_command.valid == false) {
        return false;
    }

    for (int i = 0; i < number_of_threads; ++i) {
        threads.emplace_back(RunCommand, parsed_command);
    }

    for (auto& thread : threads) {
        thread.join();
    }

    std::cout << "All threads have finished.\n";
    return true;
}

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

                RunThreads(value, target_command);
            } else {
                cerr << "Usage: threads amount<integer> command" << '\n';
            }
            continue;
        }

        ParsedCommand parsed_command = ParseCommand(tokens);

        if (parsed_command.valid == true) {
            RunCommand(parsed_command);
        }

    }
}

int main() {
    RunShell();
    return 0;
}

