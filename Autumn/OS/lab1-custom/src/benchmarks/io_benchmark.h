//
// Created by arums on 22.11.2024.
//

#ifndef IO_BENCHMARK_H
#define IO_BENCHMARK_H

using namespace std;

bool validateArguments(int argc, char* argv[], int& repetitions, string& filePath);

int readFileByBlocks(int repetitions, const string& filePath);

#endif //IO_BENCHMARK_H
