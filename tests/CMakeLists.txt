include(FetchContent)

FetchContent_Declare(
    googletest
    GIT_REPOSITORY https://github.com/google/googletest.git
    GIT_TAG        v1.14.0
)

# Prevent GoogleTest from overriding compiler/linker options on Windows
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)

FetchContent_MakeAvailable(googletest)

add_executable(calculator_tests
    calculator_test.cpp
)

target_link_libraries(calculator_tests
    PRIVATE
        calculator
        GTest::gtest_main
)

include(GoogleTest)
gtest_discover_tests(calculator_tests)
