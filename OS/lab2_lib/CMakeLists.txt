cmake_minimum_required(VERSION 3.29)
project(lab2_lib)

# Устанавливаем стандарт C++
set(CMAKE_CXX_STANDARD 20)

# Создаём библиотеку SHARED (DLL)
add_library(lab2_lib SHARED library.cpp)
add_executable(main library.cpp)

# Устанавливаем экспортируемый макрос для правильного импорта/экспорта
target_compile_definitions(lab2_lib PRIVATE CACHEDIOTEST_EXPORTS)
set_target_properties(lab2_lib PROPERTIES
        ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib # Папка для .lib
        LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin # Папка для .dll
        RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin # Папка для исполняемых файлов
)