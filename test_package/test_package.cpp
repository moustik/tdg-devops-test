#include <calculator.h>
#include <cassert>

int main() {
    tdg::Calculator calc;
    assert(calc.add(2, 3) == 5);
    assert(calc.subtract(10, 4) == 6);
    assert(calc.multiply(3, 4) == 12);
    assert(calc.divide(10, 2) == 5.0);
    return 0;
}
