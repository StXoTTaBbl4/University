#ifndef SHELL_STYLES_H
#define SHELL_STYLES_H

#include <iostream>
#include <windows.h>
#include <wtsapi32.h>
#include <string>
#include <sstream>
#include <chrono>
#include <fstream>
using namespace std;
void PrintLogo(const string& filePath);
bool SetConsoleStyle();

#endif //SHELL_STYLES_H
