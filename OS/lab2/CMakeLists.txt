cmake_minimum_required(VERSION 3.29)
project(lab2)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(mru_cache SHARED library.cpp)
target_compile_definitions(mru_cache PRIVATE MRUCACHE_EXPORTS)
target_include_directories(mru_cache PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
# Создание цели для примера
add_executable(example example_usage.cpp)

# Связывание примера с библиотекой
target_link_libraries(example PRIVATE mru_cache)
