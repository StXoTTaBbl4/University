#include <cstdint>
#include <iostream>
#include <ostream>
namespace {
  void Factorial(const int number, const int repetitions) {
    unsigned long long res = 1;
    for (int i = 1; i <= repetitions; i++) {
      res = 1;
      for (int j = 1; j <= number; j++) {
        res *= j;
      }
      std::cout << res << '\n';
    }
  }
}


int main(int argc, char* argv[]) {
  if(argc < 2) {
    std::cerr << "Usage: " << "<number> <number>" << '\n';
  }
  Factorial(atoi(argv[1]), atoi(argv[2]));
}