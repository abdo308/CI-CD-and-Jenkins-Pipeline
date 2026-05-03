package com.example.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Calculator class.
 * Covers arithmetic, powers/roots, trigonometry, logarithms,
 * hyperbolic functions, utility, and constants.
 */
class CalculatorTest {

  private Calculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new Calculator();
  }

  // ══════════════════════ BASIC ARITHMETIC ══════════════════════

  @Nested
  @DisplayName("Addition")
  class AdditionTests {
    @Test @DisplayName("adds two positive numbers")
    void addPositives() { assertEquals(5.0, calculator.add(2.0, 3.0)); }

    @Test @DisplayName("adds a positive and a negative number")
    void addMixed() { assertEquals(-1.0, calculator.add(2.0, -3.0)); }

    @Test @DisplayName("adds zeros")
    void addZeros() { assertEquals(0.0, calculator.add(0, 0)); }

    @Test @DisplayName("adds decimal numbers")
    void addDecimals() { assertEquals(0.3, calculator.add(0.1, 0.2), 1e-9); }

    @Test @DisplayName("adds large numbers")
    void addLarge() { assertEquals(2_000_000.0, calculator.add(1_000_000, 1_000_000)); }
  }

  @Nested
  @DisplayName("Subtraction")
  class SubtractionTests {
    @Test @DisplayName("subtracts two positive numbers")
    void subtractPositives() { assertEquals(2.0, calculator.subtract(5.0, 3.0)); }

    @Test @DisplayName("subtracts resulting in negative")
    void subtractNegativeResult() { assertEquals(-3.0, calculator.subtract(2.0, 5.0)); }

    @Test @DisplayName("subtracts zero")
    void subtractZero() { assertEquals(7.0, calculator.subtract(7.0, 0)); }
  }

  @Nested
  @DisplayName("Multiplication")
  class MultiplicationTests {
    @Test @DisplayName("multiplies two positive numbers")
    void multiplyPositives() { assertEquals(12.0, calculator.multiply(3.0, 4.0)); }

    @Test @DisplayName("multiplies by zero")
    void multiplyByZero() { assertEquals(0.0, calculator.multiply(5.0, 0)); }

    @Test @DisplayName("multiplies negative numbers")
    void multiplyNegatives() { assertEquals(15.0, calculator.multiply(-3.0, -5.0)); }

    @Test @DisplayName("multiplies positive by negative")
    void multiplyMixed() { assertEquals(-20.0, calculator.multiply(4.0, -5.0)); }
  }

  @Nested
  @DisplayName("Division")
  class DivisionTests {
    @Test @DisplayName("divides evenly")
    void divideEvenly() { assertEquals(5.0, calculator.divide(10.0, 2.0)); }

    @Test @DisplayName("divides with decimal result")
    void divideDecimal() { assertEquals(2.5, calculator.divide(5.0, 2.0)); }

    @Test @DisplayName("throws on divide by zero")
    void divideByZero() {
      ArithmeticException ex = assertThrows(ArithmeticException.class, () -> calculator.divide(10.0, 0));
      assertEquals("Cannot divide by zero", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("Modulo")
  class ModuloTests {
    @Test @DisplayName("computes modulo correctly")
    void moduloBasic() { assertEquals(1.0, calculator.modulo(10.0, 3.0)); }

    @Test @DisplayName("throws on modulo by zero")
    void moduloByZero() { assertThrows(ArithmeticException.class, () -> calculator.modulo(10.0, 0)); }
  }

  // ══════════════════════ POWERS & ROOTS ══════════════════════

  @Nested
  @DisplayName("Power")
  class PowerTests {
    @Test @DisplayName("computes integer power")
    void powerInteger() { assertEquals(8.0, calculator.power(2.0, 3.0)); }

    @Test @DisplayName("anything to the power of zero is 1")
    void powerZero() { assertEquals(1.0, calculator.power(99.0, 0)); }

    @Test @DisplayName("computes negative exponent")
    void powerNegative() { assertEquals(0.25, calculator.power(2.0, -2.0), 1e-9); }
  }

  @Nested
  @DisplayName("Square Root")
  class SquareRootTests {
    @Test @DisplayName("square root of perfect square")
    void sqrtPerfect() { assertEquals(5.0, calculator.squareRoot(25.0)); }

    @Test @DisplayName("square root of zero")
    void sqrtZero() { assertEquals(0.0, calculator.squareRoot(0)); }

    @Test @DisplayName("throws on negative input")
    void sqrtNegative() { assertThrows(ArithmeticException.class, () -> calculator.squareRoot(-4.0)); }
  }

  @Nested
  @DisplayName("Cube Root")
  class CubeRootTests {
    @Test @DisplayName("cube root of 27")
    void cbrtPositive() { assertEquals(3.0, calculator.cubeRoot(27.0), 1e-9); }

    @Test @DisplayName("cube root of negative number")
    void cbrtNegative() { assertEquals(-2.0, calculator.cubeRoot(-8.0), 1e-9); }

    @Test @DisplayName("cube root of zero")
    void cbrtZero() { assertEquals(0.0, calculator.cubeRoot(0)); }
  }

  @Nested
  @DisplayName("Square & Cube")
  class SquareCubeTests {
    @Test @DisplayName("squares a number")
    void square() { assertEquals(25.0, calculator.square(5.0)); }

    @Test @DisplayName("squares a negative")
    void squareNeg() { assertEquals(9.0, calculator.square(-3.0)); }

    @Test @DisplayName("cubes a number")
    void cubePositive() { assertEquals(125.0, calculator.cube(5.0)); }

    @Test @DisplayName("cubes a negative")
    void cubeNeg() { assertEquals(-27.0, calculator.cube(-3.0)); }
  }

  @Nested
  @DisplayName("Reciprocal")
  class ReciprocalTests {
    @Test @DisplayName("reciprocal of 4 is 0.25")
    void reciprocalNormal() { assertEquals(0.25, calculator.reciprocal(4.0)); }

    @Test @DisplayName("reciprocal of -2 is -0.5")
    void reciprocalNegative() { assertEquals(-0.5, calculator.reciprocal(-2.0)); }

    @Test @DisplayName("throws on reciprocal of zero")
    void reciprocalZero() { assertThrows(ArithmeticException.class, () -> calculator.reciprocal(0)); }
  }

  @Nested
  @DisplayName("Exponential & 10^x")
  class ExpTests {
    @Test @DisplayName("e^0 is 1")
    void expZero() { assertEquals(1.0, calculator.exp(0)); }

    @Test @DisplayName("e^1 is e")
    void expOne() { assertEquals(Math.E, calculator.exp(1), 1e-9); }

    @Test @DisplayName("10^3 is 1000")
    void tenPow() { assertEquals(1000.0, calculator.tenPow(3)); }

    @Test @DisplayName("10^0 is 1")
    void tenPowZero() { assertEquals(1.0, calculator.tenPow(0)); }
  }

  // ══════════════════════ TRIGONOMETRY (RADIANS) ══════════════════════

  @Nested
  @DisplayName("Trigonometry (Radians)")
  class TrigRadTests {
    @Test @DisplayName("sin(0) = 0")
    void sinZero() { assertEquals(0.0, calculator.sin(0), 1e-9); }

    @Test @DisplayName("sin(π/2) = 1")
    void sinHalfPi() { assertEquals(1.0, calculator.sin(Math.PI / 2), 1e-9); }

    @Test @DisplayName("cos(0) = 1")
    void cosZero() { assertEquals(1.0, calculator.cos(0), 1e-9); }

    @Test @DisplayName("cos(π) = -1")
    void cosPi() { assertEquals(-1.0, calculator.cos(Math.PI), 1e-9); }

    @Test @DisplayName("tan(0) = 0")
    void tanZero() { assertEquals(0.0, calculator.tan(0), 1e-9); }

    @Test @DisplayName("tan(π/4) = 1")
    void tanQuarterPi() { assertEquals(1.0, calculator.tan(Math.PI / 4), 1e-9); }

    @Test @DisplayName("asin(1) = π/2")
    void asinOne() { assertEquals(Math.PI / 2, calculator.asin(1), 1e-9); }

    @Test @DisplayName("asin out of range throws")
    void asinOutOfRange() { assertThrows(ArithmeticException.class, () -> calculator.asin(2.0)); }

    @Test @DisplayName("acos(1) = 0")
    void acosOne() { assertEquals(0.0, calculator.acos(1), 1e-9); }

    @Test @DisplayName("acos out of range throws")
    void acosOutOfRange() { assertThrows(ArithmeticException.class, () -> calculator.acos(-2.0)); }

    @Test @DisplayName("atan(1) = π/4")
    void atanOne() { assertEquals(Math.PI / 4, calculator.atan(1), 1e-9); }
  }

  // ══════════════════════ TRIGONOMETRY (DEGREES) ══════════════════════

  @Nested
  @DisplayName("Trigonometry (Degrees)")
  class TrigDegTests {
    @Test @DisplayName("sin(90°) = 1")
    void sin90() { assertEquals(1.0, calculator.sinDeg(90), 1e-9); }

    @Test @DisplayName("sin(0°) = 0")
    void sin0() { assertEquals(0.0, calculator.sinDeg(0), 1e-9); }

    @Test @DisplayName("cos(0°) = 1")
    void cos0() { assertEquals(1.0, calculator.cosDeg(0), 1e-9); }

    @Test @DisplayName("cos(180°) = -1")
    void cos180() { assertEquals(-1.0, calculator.cosDeg(180), 1e-9); }

    @Test @DisplayName("tan(45°) = 1")
    void tan45() { assertEquals(1.0, calculator.tanDeg(45), 1e-9); }

    @Test @DisplayName("asin°(1) = 90")
    void asinDeg1() { assertEquals(90.0, calculator.asinDeg(1), 1e-9); }

    @Test @DisplayName("acos°(0) = 90")
    void acosDeg0() { assertEquals(90.0, calculator.acosDeg(0), 1e-9); }

    @Test @DisplayName("atan°(1) = 45")
    void atanDeg1() { assertEquals(45.0, calculator.atanDeg(1), 1e-9); }
  }

  // ══════════════════════ HYPERBOLIC ══════════════════════

  @Nested
  @DisplayName("Hyperbolic")
  class HyperbolicTests {
    @Test @DisplayName("sinh(0) = 0")
    void sinhZero() { assertEquals(0.0, calculator.sinh(0), 1e-9); }

    @Test @DisplayName("cosh(0) = 1")
    void coshZero() { assertEquals(1.0, calculator.cosh(0), 1e-9); }

    @Test @DisplayName("tanh(0) = 0")
    void tanhZero() { assertEquals(0.0, calculator.tanh(0), 1e-9); }

    @Test @DisplayName("sinh is odd function")
    void sinhOdd() { assertEquals(-calculator.sinh(2.0), calculator.sinh(-2.0), 1e-9); }
  }

  // ══════════════════════ LOGARITHMS ══════════════════════

  @Nested
  @DisplayName("Logarithms")
  class LogarithmTests {
    @Test @DisplayName("ln(1) = 0")
    void lnOne() { assertEquals(0.0, calculator.ln(1), 1e-9); }

    @Test @DisplayName("ln(e) = 1")
    void lnE() { assertEquals(1.0, calculator.ln(Math.E), 1e-9); }

    @Test @DisplayName("ln of negative throws")
    void lnNegative() { assertThrows(ArithmeticException.class, () -> calculator.ln(-1)); }

    @Test @DisplayName("ln of zero throws")
    void lnZero() { assertThrows(ArithmeticException.class, () -> calculator.ln(0)); }

    @Test @DisplayName("log10(100) = 2")
    void log10Hundred() { assertEquals(2.0, calculator.log10(100), 1e-9); }

    @Test @DisplayName("log10(1) = 0")
    void log10One() { assertEquals(0.0, calculator.log10(1), 1e-9); }

    @Test @DisplayName("log10 of negative throws")
    void log10Negative() { assertThrows(ArithmeticException.class, () -> calculator.log10(-5)); }

    @Test @DisplayName("log2(8) = 3")
    void log2Eight() { assertEquals(3.0, calculator.log2(8), 1e-9); }

    @Test @DisplayName("log2(1) = 0")
    void log2One() { assertEquals(0.0, calculator.log2(1), 1e-9); }

    @Test @DisplayName("log2 of negative throws")
    void log2Negative() { assertThrows(ArithmeticException.class, () -> calculator.log2(-1)); }
  }

  // ══════════════════════ UTILITY ══════════════════════

  @Nested
  @DisplayName("Absolute Value")
  class AbsoluteTests {
    @Test @DisplayName("absolute of negative")
    void absNegative() { assertEquals(7.0, calculator.absolute(-7.0)); }

    @Test @DisplayName("absolute of positive")
    void absPositive() { assertEquals(3.0, calculator.absolute(3.0)); }
  }

  @Nested
  @DisplayName("Negate")
  class NegateTests {
    @Test @DisplayName("negates positive")
    void negatePositive() { assertEquals(-5.0, calculator.negate(5.0)); }

    @Test @DisplayName("negates negative")
    void negateNegative() { assertEquals(5.0, calculator.negate(-5.0)); }
  }

  @Nested
  @DisplayName("Percentage")
  class PercentageTests {
    @Test @DisplayName("50 as percentage is 0.5")
    void percentFifty() { assertEquals(0.5, calculator.percentage(50)); }

    @Test @DisplayName("100 as percentage is 1.0")
    void percentHundred() { assertEquals(1.0, calculator.percentage(100)); }

    @Test @DisplayName("0 as percentage is 0.0")
    void percentZero() { assertEquals(0.0, calculator.percentage(0)); }
  }

  @Nested
  @DisplayName("Factorial")
  class FactorialTests {
    @Test @DisplayName("factorial of 0 is 1")
    void factorialZero() { assertEquals(1.0, calculator.factorial(0)); }

    @Test @DisplayName("factorial of 5 is 120")
    void factorialFive() { assertEquals(120.0, calculator.factorial(5)); }

    @Test @DisplayName("factorial of 10 is 3628800")
    void factorialTen() { assertEquals(3628800.0, calculator.factorial(10)); }

    @Test @DisplayName("factorial of 20 is max supported")
    void factorialTwenty() { assertEquals(2432902008176640000.0, calculator.factorial(20), 1e3); }

    @Test @DisplayName("throws on negative input")
    void factorialNegative() { assertThrows(ArithmeticException.class, () -> calculator.factorial(-1)); }

    @Test @DisplayName("throws on input > 20")
    void factorialTooLarge() { assertThrows(ArithmeticException.class, () -> calculator.factorial(21)); }
  }

  // ══════════════════════ CONSTANTS ══════════════════════

  @Nested
  @DisplayName("Constants")
  class ConstantTests {
    @Test @DisplayName("pi returns Math.PI")
    void piValue() { assertEquals(Math.PI, calculator.pi(), 1e-15); }

    @Test @DisplayName("euler returns Math.E")
    void eulerValue() { assertEquals(Math.E, calculator.euler(), 1e-15); }
  }
}
