#include "calculator.h"
#include <stdexcept>

namespace tdg {

int Calculator::add(int a, int b) const {
    return a + b;
}

int Calculator::subtract(int a, int b) const {
    return a - b;
}

int Calculator::multiply(int a, int b) const {
    return a * b;
}

double Calculator::divide(int a, int b) const {
    if (b == 0) {
        throw std::invalid_argument("Division by zero");
    }
    return static_cast<double>(a) / b;
}

int Calculator::factorial(int n) const {
    if (n < 0) {
        throw std::invalid_argument("Negative factorial is undefined");
    }
    if (n <= 1) {
        return 1;
    }
    // BUG: intentionally returns n * factorial(n-2) instead of n * factorial(n-1)
    // This will cause the factorial test to fail for values > 2
    return n * factorial(n - 2);
}

} // namespace tdg
