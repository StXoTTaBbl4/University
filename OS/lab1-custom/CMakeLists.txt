cmake_minimum_required(VERSION 3.29)
project(lab1_custom VERSION 0.5 LANGUAGES CXX)
set(SRC_DIR ${CMAKE_SOURCE_DIR}/src)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static -static-libgcc -static-libstdc++")

include(cmake/compileOptions.cmake)
option(MONOLITH_DEVELOPER "Enable developer-specific features" OFF)
if(MONOLITH_DEVELOPER)
    add_compile_options(-Werror)
    set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
endif()

#include(cmake/FindGBenchmark.cmake)
#include(cmake/FindGTest.cmake)

include(cmake/IncludeGtest.cmake)
add_subdirectory(${SRC_DIR}/tests)

add_library(shell_lib ${SRC_DIR}/shell.cpp)
add_library(io_bench_lib ${SRC_DIR}/benchmarks/io_benchmark.cpp)
add_library(cpu_bench_lib ${SRC_DIR}/benchmarks/cpu_benchmark.cpp)

add_executable(shell ${SRC_DIR}/shell.cpp ${SRC_DIR}/utility/shell_styles.cpp)
add_executable(io_benchmark ${SRC_DIR}/benchmarks/io_benchmark.cpp)
add_executable(cpu_benchmark ${SRC_DIR}/benchmarks/cpu_benchmark.cpp)
add_executable(in_out ${SRC_DIR}/in_out_test.cpp)