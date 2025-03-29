#include <cstdint>
#include <iostream>
#include <ostream>
#include <windows.h>
#include "cpu_benchmark.h"

using namespace std;

bool ValidateArguments(const int argc, char* argv[], long& value, long& repetitions) {
  if (argc < 3) {
    // cerr << "Usage: value<number> repetitions<number>" << '\n';
    return false;
  }

  char* end;
  value = strtol(argv[1], &end, 10);
  if (*end != '\0' || value <= 0 || value > INT_MAX) {
    cerr << "Error: value must be more than 0 and less than INT_MAX\n";
    return false;
  }

  repetitions = strtol(argv[2], &end, 10);
  if (*end != '\0' || repetitions <= 0 || repetitions > INT_MAX) {
    cerr << "Error: repetitions must be more than 0 and less than INT_MAX\n";
    return false;
  }

  return true;
}

int Factorial(const int number, const int repetitions) {
  const auto start = GetTickCount64();

  for (int i = 1; i <= repetitions; i++) {
    unsigned long long res = 1;
    for (int j = 1; j <= number; j++) {
      res *= j;
    }

  }

  const auto end = GetTickCount64();
  // const double duration = (end - start) / 1000.0;

  cout << "Duration: " << end - start << "milliseconds" << endl;
  return 0;
}