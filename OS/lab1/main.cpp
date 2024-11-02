#include <windows.h>
#include <iostream>
#include <string>
#include <sstream>
#include <chrono>
#include <vector>

//notepad D:\ITMO\3-re\OS\lab1\main.cpp

std::vector<std::string> parseCommand(const std::string& input) {
    std::stringstream ss(input);
    std::string segment;
    std::vector<std::string> tokens;

    while (ss >> segment) {
        tokens.push_back(segment);
    }

    return tokens;
}

void runShell() {
    char currentDirectory[MAX_PATH];

    while (true) {
        GetCurrentDirectory(MAX_PATH, currentDirectory);
        std::cout <<"cstShell>" << currentDirectory << "> ";

        std::string input;
        std::getline(std::cin, input);

        std::vector<std::string> tokens = parseCommand(input);
        if (tokens.empty()) continue;

        std::string command = tokens[0];

        if (command == "exit") {
            break;
        }

        if (command == "cd") {
            if (tokens.size() == 2) {
                if (!SetCurrentDirectory(tokens[1].c_str())) {
                    std::cerr << "Failed to change directory: " << tokens[1] << std::endl;
                }
            } else {
                std::cerr << "Usage: cd <directory>" << std::endl;
            }
            continue;
        }

        std::string fullCommand;
        for (const auto& token : tokens) {
            fullCommand += token + " ";
        }

        STARTUPINFO si;
        PROCESS_INFORMATION pi;
        ZeroMemory(&si, sizeof(si));
        si.cb = sizeof(si);
        ZeroMemory(&pi, sizeof(pi));

        auto start = std::chrono::high_resolution_clock::now();

        if (CreateProcess(NULL, const_cast<LPSTR>(fullCommand.c_str()), NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
            WaitForSingleObject(pi.hProcess, INFINITE);

            auto end = std::chrono::high_resolution_clock::now();

            CloseHandle(pi.hProcess);
            CloseHandle(pi.hThread);

            std::chrono::duration<double> duration = end - start;
            std::cout << "Program executed in " << duration.count() << " seconds.\n";
        } else {
            std::cerr << "Failed to execute command: " << fullCommand << std::endl;
        }
    }
}

int main() {
    runShell();
    return 0;
}

