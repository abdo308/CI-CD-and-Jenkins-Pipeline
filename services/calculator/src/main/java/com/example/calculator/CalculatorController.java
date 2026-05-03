package com.example.calculator;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CalculatorController {

  private final Calculator calculator = new Calculator();

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok", "service", "calculator");
  }

  @PostMapping("/calculate")
  public ResponseEntity<?> calculate(@RequestBody CalculationRequest request) {
    if (request == null || request.operation() == null) {
      return error(HttpStatus.BAD_REQUEST, "operation is required");
    }

    try {
      double result = switch (request.operation().toLowerCase()) {
        // basic arithmetic
        case "add"        -> calculator.add(request.a(), request.b());
        case "subtract"   -> calculator.subtract(request.a(), request.b());
        case "multiply"   -> calculator.multiply(request.a(), request.b());
        case "divide"     -> calculator.divide(request.a(), request.b());
        case "modulo"     -> calculator.modulo(request.a(), request.b());

        // powers & roots
        case "power"      -> calculator.power(request.a(), request.b());
        case "sqrt"       -> calculator.squareRoot(request.a());
        case "cbrt"       -> calculator.cubeRoot(request.a());
        case "square"     -> calculator.square(request.a());
        case "cube"       -> calculator.cube(request.a());
        case "reciprocal" -> calculator.reciprocal(request.a());
        case "exp"        -> calculator.exp(request.a());
        case "tenpow"     -> calculator.tenPow(request.a());

        // trig (radians)
        case "sin"        -> calculator.sin(request.a());
        case "cos"        -> calculator.cos(request.a());
        case "tan"        -> calculator.tan(request.a());
        case "asin"       -> calculator.asin(request.a());
        case "acos"       -> calculator.acos(request.a());
        case "atan"       -> calculator.atan(request.a());

        // trig (degrees)
        case "sindeg"     -> calculator.sinDeg(request.a());
        case "cosdeg"     -> calculator.cosDeg(request.a());
        case "tandeg"     -> calculator.tanDeg(request.a());
        case "asindeg"    -> calculator.asinDeg(request.a());
        case "acosdeg"    -> calculator.acosDeg(request.a());
        case "atandeg"    -> calculator.atanDeg(request.a());

        // hyperbolic
        case "sinh"       -> calculator.sinh(request.a());
        case "cosh"       -> calculator.cosh(request.a());
        case "tanh"       -> calculator.tanh(request.a());

        // logarithms
        case "ln"         -> calculator.ln(request.a());
        case "log10"      -> calculator.log10(request.a());
        case "log2"       -> calculator.log2(request.a());

        // utility
        case "abs"        -> calculator.absolute(request.a());
        case "negate"     -> calculator.negate(request.a());
        case "percentage" -> calculator.percentage(request.a());
        case "factorial"  -> calculator.factorial((int) request.a());

        // constants
        case "pi"         -> calculator.pi();
        case "euler"      -> calculator.euler();

        default -> throw new IllegalArgumentException("Unknown operation: " + request.operation());
      };

      return ResponseEntity.ok(new CalculationResponse(
          request.operation(),
          request.a(),
          request.b(),
          result
      ));
    } catch (ArithmeticException | IllegalArgumentException ex) {
      return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
  }

  private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(Map.of("error", message));
  }

  public record CalculationRequest(String operation, double a, double b) {}

  public record CalculationResponse(String operation, double a, double b, double result) {}
}
