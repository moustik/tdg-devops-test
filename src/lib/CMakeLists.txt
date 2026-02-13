add_library(calculator
    calculator.cpp
    calculator.h
)

target_include_directories(calculator
    PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}
)
