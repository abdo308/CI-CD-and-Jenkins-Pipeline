package com.example.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
  private final JdbcTemplate jdbcTemplate;
  private final TokenService tokenService;

  public AuthController(JdbcTemplate jdbcTemplate, TokenService tokenService) {
    this.jdbcTemplate = jdbcTemplate;
    this.tokenService = tokenService;
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    if (request == null || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())) {
      return error(HttpStatus.BAD_REQUEST, "email and password required");
    }

    String email = request.email().trim();
    String storedPassword;
    try {
      storedPassword = jdbcTemplate.queryForObject(
          "SELECT password FROM users WHERE email = ?",
          String.class,
          email
      );
    } catch (EmptyResultDataAccessException ex) {
      return error(HttpStatus.UNAUTHORIZED, "invalid credentials");
    } catch (DataAccessException ex) {
      return error(HttpStatus.INTERNAL_SERVER_ERROR, "database error");
    }

    if (!request.password().equals(storedPassword)) {
      return error(HttpStatus.UNAUTHORIZED, "invalid credentials");
    }

    TokenService.TokenPair token = tokenService.createToken(email);
    return ResponseEntity.ok(new LoginResponse(token.token(), token.expiresAt()));
  }

  @PostMapping("/validate")
  public ResponseEntity<?> validate(@RequestBody ValidateRequest request) {
    if (request == null || !StringUtils.hasText(request.token())) {
      return error(HttpStatus.BAD_REQUEST, "token required");
    }

    try {
      TokenService.TokenClaims claims = tokenService.parseToken(request.token());
      return ResponseEntity.ok(new ValidateResponse(claims.email(), claims.expiresAt()));
    } catch (IllegalArgumentException ex) {
      return error(HttpStatus.UNAUTHORIZED, "invalid token");
    }
  }

  private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(Map.of("error", message));
  }

  private record LoginRequest(String email, String password) {}

  private record LoginResponse(String token, @JsonProperty("expires_at") long expiresAt) {}

  private record ValidateRequest(String token) {}

  private record ValidateResponse(String email, @JsonProperty("expires_at") long expiresAt) {}
}
