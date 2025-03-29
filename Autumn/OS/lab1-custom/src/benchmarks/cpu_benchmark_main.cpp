#include "cpu_benchmark.h"

int main(const int argc, char* argv[]) {
    long value;
    long repetitions;

    if (const auto validatedArgs = ValidateArguments(argc, argv, value, repetitions); !validatedArgs) {
        return 1;
    }

    Factorial(value, repetitions);

    return 0;
}