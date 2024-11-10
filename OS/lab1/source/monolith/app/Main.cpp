#include <iostream>
#include <windows.h>
#include <wtsapi32.h>
#include <string>
#include <sstream>
#include <chrono>
#include <vector>
#pragma comment(lib, "wtsapi32.lib")


namespace monolith::app {

namespace {

std::vector<std::string> ParseCommand(const std::string& input) {
    std::stringstream ss(input);
    std::string segment;
    std::vector<std::string> tokens;

    while (ss >> segment) {
        tokens.push_back(segment);
    }

    return tokens;
}

bool GetInteractiveUserToken(HANDLE &user_token) {
  DWORD const session_id = WTSGetActiveConsoleSessionId(); // Получаем ID активной сессии
  if (session_id == 0xFFFFFFFF) {
    std::cerr << "Failed to get active session ID" << '\n';
    return false;
  }

  // ПОСМОТРЕТЬ ПРО ЛИНКОВКУ
  // Получаем токен пользователя, вошедшего в интерактивную сессию
  if (!WTSQueryUserToken(session_id, &user_token)) {
    std::cerr << "WTSQueryUserToken failed with error: " << GetLastError() << '\n';
    return false;
  }

  return true;
}

bool RunTestAsUser(const std::vector<std::string> command, int processes) {
  HANDLE user_token;
  if (!GetInteractiveUserToken(user_token)) {
    return false;
  }

  STARTUPINFO si = { sizeof(si) };
  PROCESS_INFORMATION pi;

  for (int i = 0; i < processes; i++) {

  }
}



void RunShell() {
    char current_directory[MAX_PATH];

    while (true) {
        GetCurrentDirectory(MAX_PATH, current_directory);
        std::cout <<"cstShell>" << current_directory << "> ";

        std::string input;
        std::getline(std::cin, input);

        std::vector<std::string> tokens = ParseCommand(input);
        if (tokens.empty()) { continue;}

        std::string command = tokens[0];

        if (command == "exit") {
            break;
        }

        if (command == "cd") {
            if (tokens.size() == 2) {
                if (!SetCurrentDirectory(tokens[1].c_str())) {
                    std::cerr << "Failed to change directory: " << tokens[1] << '\n';
                }
            } else {
                std::cerr << "Usage: cd <directory>" << '\n';
            }
            continue;
        }

      // if (command == "runTestAsUser") {
      //   tokens.erase(tokens.begin());
      //   int processes = atoi(tokens[0].c_str());
      //   tokens.erase(tokens.begin());
      //   RunTestAsUser(tokens, processes);
      //   return;
      // }

        std::string full_command;
        for (const auto& token : tokens) {
            full_command += token + " ";
        }



        STARTUPINFO si;
        PROCESS_INFORMATION pi;
        ZeroMemory(&si, sizeof(si));
        si.cb = sizeof(si);
        ZeroMemory(&pi, sizeof(pi));

        auto start = std::chrono::high_resolution_clock::now();

        if (CreateProcess(nullptr,
          const_cast<LPSTR>(full_command.c_str()),
          nullptr,
          nullptr,
          FALSE,
          0,
          nullptr,
          nullptr,
          &si,
          &pi)) {
            WaitForSingleObject(pi.hProcess, INFINITE);

            auto end = std::chrono::high_resolution_clock::now();

            CloseHandle(pi.hProcess);
            CloseHandle(pi.hThread);

            std::chrono::duration<double> const duration = end - start;
            std::cout << "Program executed in " << duration.count() << " seconds.\n";
        } else {
            std::cerr << "Failed to execute command: " << full_command << '\n';
        }
    }
}

int Main() {
  std::cout << "Close me and never open again!\n";
    RunShell();
    return 0;
}

}  // namespace

}  // namespace monolith::app

int main() {
  monolith::app::Main();
}
