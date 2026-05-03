package com.example.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the CalculatorController REST API.
 * Covers health, arithmetic, trig, logs, powers, and error handling.
 */
@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper mapper = new ObjectMapper();

  // ── Health ──

  @Test
  @DisplayName("GET /api/health returns ok")
  void healthCheck() throws Exception {
    mockMvc.perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ok"))
        .andExpect(jsonPath("$.service").value("calculator"));
  }

  // ── Basic Arithmetic ──

  @Test
  @DisplayName("POST /api/calculate — addition")
  void calculateAdd() throws Exception {
    var request = new CalculatorController.CalculationRequest("add", 10, 5);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(15.0))
        .andExpect(jsonPath("$.operation").value("add"));
  }

  @Test
  @DisplayName("POST /api/calculate — subtraction")
  void calculateSubtract() throws Exception {
    var request = new CalculatorController.CalculationRequest("subtract", 20, 8);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(12.0));
  }

  @Test
  @DisplayName("POST /api/calculate — multiplication")
  void calculateMultiply() throws Exception {
    var request = new CalculatorController.CalculationRequest("multiply", 6, 7);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(42.0));
  }

  @Test
  @DisplayName("POST /api/calculate — division")
  void calculateDivide() throws Exception {
    var request = new CalculatorController.CalculationRequest("divide", 20, 4);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(5.0));
  }

  // ── Error Handling ──

  @Test
  @DisplayName("POST /api/calculate — divide by zero returns 400")
  void divideByZero() throws Exception {
    var request = new CalculatorController.CalculationRequest("divide", 10, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Cannot divide by zero"));
  }

  @Test
  @DisplayName("POST /api/calculate — unknown operation returns 400")
  void unknownOperation() throws Exception {
    var request = new CalculatorController.CalculationRequest("unknown_op", 10, 2);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  @DisplayName("POST /api/calculate — ln of negative returns 400")
  void lnNegative() throws Exception {
    var request = new CalculatorController.CalculationRequest("ln", -5, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Logarithm input must be positive"));
  }

  // ── Powers & Roots ──

  @Test
  @DisplayName("POST /api/calculate — square root")
  void calculateSqrt() throws Exception {
    var request = new CalculatorController.CalculationRequest("sqrt", 144, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(12.0));
  }

  @Test
  @DisplayName("POST /api/calculate — cube root")
  void calculateCbrt() throws Exception {
    var request = new CalculatorController.CalculationRequest("cbrt", 27, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(3.0));
  }

  @Test
  @DisplayName("POST /api/calculate — square")
  void calculateSquare() throws Exception {
    var request = new CalculatorController.CalculationRequest("square", 7, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(49.0));
  }

  @Test
  @DisplayName("POST /api/calculate — reciprocal")
  void calculateReciprocal() throws Exception {
    var request = new CalculatorController.CalculationRequest("reciprocal", 5, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(0.2));
  }

  // ── Trig ──

  @Test
  @DisplayName("POST /api/calculate — sin degrees")
  void calculateSinDeg() throws Exception {
    var request = new CalculatorController.CalculationRequest("sindeg", 90, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(1.0));
  }

  @Test
  @DisplayName("POST /api/calculate — cos degrees")
  void calculateCosDeg() throws Exception {
    var request = new CalculatorController.CalculationRequest("cosdeg", 0, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(1.0));
  }

  // ── Logarithms ──

  @Test
  @DisplayName("POST /api/calculate — log10")
  void calculateLog10() throws Exception {
    var request = new CalculatorController.CalculationRequest("log10", 1000, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(3.0));
  }

  @Test
  @DisplayName("POST /api/calculate — log2")
  void calculateLog2() throws Exception {
    var request = new CalculatorController.CalculationRequest("log2", 16, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(4.0));
  }

  // ── Constants ──

  @Test
  @DisplayName("POST /api/calculate — pi")
  void calculatePi() throws Exception {
    var request = new CalculatorController.CalculationRequest("pi", 0, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(Math.PI));
  }

  @Test
  @DisplayName("POST /api/calculate — euler")
  void calculateEuler() throws Exception {
    var request = new CalculatorController.CalculationRequest("euler", 0, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(Math.E));
  }

  // ── Utility ──

  @Test
  @DisplayName("POST /api/calculate — percentage")
  void calculatePercentage() throws Exception {
    var request = new CalculatorController.CalculationRequest("percentage", 75, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(0.75));
  }

  @Test
  @DisplayName("POST /api/calculate — factorial")
  void calculateFactorial() throws Exception {
    var request = new CalculatorController.CalculationRequest("factorial", 6, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(720.0));
  }

  // ── Hyperbolic ──

  @Test
  @DisplayName("POST /api/calculate — sinh")
  void calculateSinh() throws Exception {
    var request = new CalculatorController.CalculationRequest("sinh", 0, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(0.0));
  }

  @Test
  @DisplayName("POST /api/calculate — exp")
  void calculateExp() throws Exception {
    var request = new CalculatorController.CalculationRequest("exp", 0, 0);
    mockMvc.perform(post("/api/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(1.0));
  }
}
