cmake_minimum_required(VERSION 3.29)
project(lab2_iobench_cache)

set(CMAKE_CXX_STANDARD 20)

add_executable(lab2_iobench_cache main.cpp)
target_link_libraries(lab2_iobench_cache PRIVATE ${CMAKE_SOURCE_DIR}/lib/liblab2_lib.dll.a)
target_include_directories(lab2_iobench_cache PRIVATE ${CMAKE_SOURCE_DIR}/lib/headers)
target_include_directories(lab2_iobench_cache PRIVATE ${CMAKE_SOURCE_DIR}/lib)
