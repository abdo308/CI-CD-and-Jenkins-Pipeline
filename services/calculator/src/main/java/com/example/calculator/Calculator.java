package com.example.calculator;

/**
 * Full-featured calculator engine — pure functions, no Spring dependency.
 * Supports arithmetic, trigonometry, logarithms, roots, and more.
 * Easy to unit-test in isolation.
 */
public class Calculator {

  // ───────────────── Basic Arithmetic ─────────────────

  public double add(double a, double b) {
    return a + b;
  }

  public double subtract(double a, double b) {
    return a - b;
  }

  public double multiply(double a, double b) {
    return a * b;
  }

  public double divide(double a, double b) {
    if (b == 0) {
      throw new ArithmeticException("Cannot divide by zero");
    }
    return a / b;
  }

  public double modulo(double a, double b) {
    if (b == 0) {
      throw new ArithmeticException("Cannot modulo by zero");
    }
    return a % b;
  }

  // ───────────────── Powers & Roots ─────────────────

  public double power(double base, double exponent) {
    return Math.pow(base, exponent);
  }

  public double squareRoot(double a) {
    if (a < 0) {
      throw new ArithmeticException("Cannot take square root of a negative number");
    }
    return Math.sqrt(a);
  }

  public double cubeRoot(double a) {
    return Math.cbrt(a);
  }

  public double square(double a) {
    return a * a;
  }

  public double cube(double a) {
    return a * a * a;
  }

  public double reciprocal(double a) {
    if (a == 0) {
      throw new ArithmeticException("Cannot compute reciprocal of zero");
    }
    return 1.0 / a;
  }

  public double exp(double a) {
    return Math.exp(a);
  }

  public double tenPow(double a) {
    return Math.pow(10, a);
  }

  // ───────────────── Trigonometry (radians) ─────────────────

  public double sin(double a) {
    return Math.sin(a);
  }

  public double cos(double a) {
    return Math.cos(a);
  }

  public double tan(double a) {
    return Math.tan(a);
  }

  public double asin(double a) {
    if (a < -1 || a > 1) {
      throw new ArithmeticException("asin input must be between -1 and 1");
    }
    return Math.asin(a);
  }

  public double acos(double a) {
    if (a < -1 || a > 1) {
      throw new ArithmeticException("acos input must be between -1 and 1");
    }
    return Math.acos(a);
  }

  public double atan(double a) {
    return Math.atan(a);
  }

  // ───────────────── Trigonometry (degrees) ─────────────────

  public double sinDeg(double a) {
    return Math.sin(Math.toRadians(a));
  }

  public double cosDeg(double a) {
    return Math.cos(Math.toRadians(a));
  }

  public double tanDeg(double a) {
    return Math.tan(Math.toRadians(a));
  }

  public double asinDeg(double a) {
    if (a < -1 || a > 1) {
      throw new ArithmeticException("asin input must be between -1 and 1");
    }
    return Math.toDegrees(Math.asin(a));
  }

  public double acosDeg(double a) {
    if (a < -1 || a > 1) {
      throw new ArithmeticException("acos input must be between -1 and 1");
    }
    return Math.toDegrees(Math.acos(a));
  }

  public double atanDeg(double a) {
    return Math.toDegrees(Math.atan(a));
  }

  // ───────────────── Logarithms ─────────────────

  public double ln(double a) {
    if (a <= 0) {
      throw new ArithmeticException("Logarithm input must be positive");
    }
    return Math.log(a);
  }

  public double log10(double a) {
    if (a <= 0) {
      throw new ArithmeticException("Logarithm input must be positive");
    }
    return Math.log10(a);
  }

  public double log2(double a) {
    if (a <= 0) {
      throw new ArithmeticException("Logarithm input must be positive");
    }
    return Math.log(a) / Math.log(2);
  }

  // ───────────────── Utility ─────────────────

  public double absolute(double a) {
    return Math.abs(a);
  }

  public double negate(double a) {
    return -a;
  }

  public double percentage(double a) {
    return a / 100.0;
  }

  public double factorial(int n) {
    if (n < 0) {
      throw new ArithmeticException("Cannot compute factorial of a negative number");
    }
    if (n > 20) {
      throw new ArithmeticException("Input too large — max supported is 20");
    }
    long result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  // ───────────────── Hyperbolic ─────────────────

  public double sinh(double a) {
    return Math.sinh(a);
  }

  public double cosh(double a) {
    return Math.cosh(a);
  }

  public double tanh(double a) {
    return Math.tanh(a);
  }

  // ───────────────── Constants ─────────────────

  public double pi() {
    return Math.PI;
  }

  public double euler() {
    return Math.E;
  }
}
