cmake_minimum_required(VERSION 3.29)
project(lab2)

set(CMAKE_CXX_STANDARD 20)

add_library(lab2 SHARED library.cpp)
add_executable(la2_ex library.cpp)
