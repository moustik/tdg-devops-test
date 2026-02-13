#pragma once

namespace tdg {

class Calculator {
public:
    int add(int a, int b) const;
    int subtract(int a, int b) const;
    int multiply(int a, int b) const;
    double divide(int a, int b) const;
    int factorial(int n) const;
};

} // namespace tdg
