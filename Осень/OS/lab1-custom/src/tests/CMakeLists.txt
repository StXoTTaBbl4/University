#set(SRC_FILES ${SRC_DIR}/benchmarks/cpu_benchmark.cpp ${SRC_DIR}/benchmarks/io_benchmark.cpp)
#set(TEST_FILES ${SRC_DIR}/tests/cpu_benchmark_test.cpp ${SRC_DIR}/tests/io_benchmark_test.cpp)
#
#list(LENGTH SRC_FILES SRC_COUNT)
#list(LENGTH TEST_FILES TEST_COUNT)
#if(NOT SRC_COUNT EQUAL TEST_COUNT)
#    message(FATAL_ERROR "Number of source files and test files must match")
#endif()
#
## Добавляем тесты
#math(EXPR LAST_INDEX "${SRC_COUNT} - 1")
#foreach(INDEX RANGE 0 ${LAST_INDEX})
#
#    list(GET SRC_FILES ${INDEX} SRC_FILE)
#    list(GET TEST_FILES ${INDEX} TEST_FILE)
#
#    get_filename_component(TEST_NAME ${TEST_FILE} NAME_WE)
#
#    add_executable(${TEST_NAME} ${TEST_FILE} ${SRC_FILE})
#
#    target_link_libraries(${TEST_NAME} PRIVATE GTest::gtest GTest::gtest_main)
#
#    add_test(NAME ${TEST_NAME} COMMAND ${TEST_NAME})
#endforeach()


#add_executable(shell_test shell_test.cpp)
#target_link_libraries(shell_test gtest_main shell_lib)

# Добавляем тесты для file2
add_executable(cpu_benchmark_test cpu_benchmark_test.cpp)
target_link_libraries(cpu_benchmark_test gtest_main cpu_bench_lib)

# Добавляем тесты для file3
add_executable(io_benchmark_test io_benchmark_test.cpp)
target_link_libraries(io_benchmark_test gtest_main io_bench_lib)

# Регистрируем тесты
#add_test(NAME ShellTest COMMAND shell_test)
add_test(NAME CpuBenchTest COMMAND cpu_benchmark_test)
add_test(NAME IoBenchTest COMMAND io_benchmark_test)
