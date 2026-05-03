package com.example.api;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
  private final JdbcTemplate jdbcTemplate;
  private final AuthClient authClient;

  public ApiController(JdbcTemplate jdbcTemplate, AuthClient authClient) {
    this.jdbcTemplate = jdbcTemplate;
    this.authClient = authClient;
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }

  @GetMapping("/products")
  public ResponseEntity<?> products(
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    String token = extractBearerToken(authorization);
    if (!StringUtils.hasText(token)) {
      return error(HttpStatus.UNAUTHORIZED, "missing token");
    }

    String email;
    try {
      email = authClient.validate(token);
    } catch (RuntimeException ex) {
      return error(HttpStatus.UNAUTHORIZED, "invalid token");
    }

    List<Product> items;
    try {
      items = jdbcTemplate.query(
          "SELECT id, name, price FROM products ORDER BY id",
          (rs, rowNum) -> new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"))
      );
    } catch (DataAccessException ex) {
      return error(HttpStatus.INTERNAL_SERVER_ERROR, "database error");
    }

    return ResponseEntity.ok(new ProductsResponse(email, items));
  }

  private String extractBearerToken(String header) {
    if (!StringUtils.hasText(header)) {
      return null;
    }
    String[] parts = header.split(" ", 2);
    if (parts.length != 2 || !"Bearer".equalsIgnoreCase(parts[0])) {
      return null;
    }
    return parts[1].trim();
  }

  private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(Map.of("error", message));
  }

  private record Product(int id, String name, double price) {}

  private record ProductsResponse(String user, List<Product> items) {}
}
