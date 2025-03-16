#include <gtest/gtest.h>
#include "../benchmarks/cpu_benchmark.h"
using namespace std;

TEST(RunApplicationTest, InvalidArgs1) {
    long value;
    long repetitions;

    char* argv[3];
    argv[0] = strdup("something");
    argv[1] = strdup("2");
    argv[2] = strdup("2");

    // EXPECT_FALSE();
    EXPECT_FALSE(ValidateArguments(2, argv, value, repetitions));

    free(argv[0]);
    free(argv[1]);
    free(argv[2]);
}

TEST(RunApplicationTest, InvalidArgs2) {
    long value;
    long repetitions;

    char* argv[3];
    argv[0] = strdup("something");
    argv[1] = strdup("2");
    argv[2] = strdup("2");

    EXPECT_TRUE(ValidateArguments(3, argv, value, repetitions));

    free(argv[0]);
    free(argv[1]);
    free(argv[2]);
}
