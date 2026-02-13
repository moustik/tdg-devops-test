#include <gtest/gtest.h>
#include "calculator.h"

class CalculatorTest : public ::testing::Test {
protected:
    tdg::Calculator calc;
};

// --- Addition Tests ---

TEST_F(CalculatorTest, AddPositiveNumbers) {
    EXPECT_EQ(calc.add(3, 4), 7);
}

TEST_F(CalculatorTest, AddNegativeNumbers) {
    EXPECT_EQ(calc.add(-2, -3), -5);
}

TEST_F(CalculatorTest, AddZero) {
    EXPECT_EQ(calc.add(0, 5), 5);
}

// --- Subtraction Tests ---

TEST_F(CalculatorTest, SubtractPositiveNumbers) {
    EXPECT_EQ(calc.subtract(10, 4), 6);
}

TEST_F(CalculatorTest, SubtractResultNegative) {
    EXPECT_EQ(calc.subtract(3, 7), -4);
}

// --- Multiplication Tests ---

TEST_F(CalculatorTest, MultiplyPositiveNumbers) {
    EXPECT_EQ(calc.multiply(5, 3), 15);
}

TEST_F(CalculatorTest, MultiplyByZero) {
    EXPECT_EQ(calc.multiply(5, 0), 0);
}

// --- Division Tests ---

TEST_F(CalculatorTest, DivideExact) {
    EXPECT_DOUBLE_EQ(calc.divide(10, 2), 5.0);
}

TEST_F(CalculatorTest, DivideFraction) {
    EXPECT_NEAR(calc.divide(10, 3), 3.3333, 0.001);
}

TEST_F(CalculatorTest, DivideByZeroThrows) {
    EXPECT_THROW(calc.divide(10, 0), std::invalid_argument);
}

// --- Factorial Tests ---

TEST_F(CalculatorTest, FactorialOfZero) {
    EXPECT_EQ(calc.factorial(0), 1);
}

TEST_F(CalculatorTest, FactorialOfOne) {
    EXPECT_EQ(calc.factorial(1), 1);
}

// THIS TEST WILL FAIL — factorial has an intentional bug for n > 2
TEST_F(CalculatorTest, FactorialOfFive) {
    EXPECT_EQ(calc.factorial(5), 120);
}

TEST_F(CalculatorTest, FactorialNegativeThrows) {
    EXPECT_THROW(calc.factorial(-1), std::invalid_argument);
}
