cmake_minimum_required(VERSION 3.14)
project(tdg-devops-test
    VERSION 1.0.0
    LANGUAGES CXX
    DESCRIPTION "TDG DevOps Technical Test Project"
)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Export compile_commands.json for IDE support
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

add_subdirectory(src/lib)
add_subdirectory(src/app)

# Testing
option(BUILD_TESTS "Build unit tests" ON)
if(BUILD_TESTS)
    enable_testing()
    add_subdirectory(tests)
endif()
