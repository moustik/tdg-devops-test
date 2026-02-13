#include "calculator.h"
#include <iostream>

int main() {
    tdg::Calculator calc;

    std::cout << "=== TDG DevOps Test - Calculator ===" << std::endl;
    std::cout << "add(3, 4)       = " << calc.add(3, 4) << std::endl;
    std::cout << "subtract(10, 6) = " << calc.subtract(10, 6) << std::endl;
    std::cout << "multiply(5, 3)  = " << calc.multiply(5, 3) << std::endl;
    std::cout << "divide(10, 3)   = " << calc.divide(10, 3) << std::endl;
    std::cout << "factorial(5)    = " << calc.factorial(5) << std::endl;

    return 0;
}
