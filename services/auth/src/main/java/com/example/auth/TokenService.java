package com.example.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenService {
  private final String secret;
  private final Duration ttl;

  public TokenService(
      @Value("${AUTH_SECRET:devsecret}") String secret,
      @Value("${TOKEN_TTL_MINUTES:60}") long ttlMinutes
  ) {
    this.secret = secret;
    this.ttl = Duration.ofMinutes(ttlMinutes);
  }

  public TokenPair createToken(String email) {
    if (!StringUtils.hasText(secret)) {
      throw new IllegalArgumentException("missing secret");
    }

    long exp = Instant.now().plus(ttl).getEpochSecond();
    String payload = email + "|" + exp;
    String signature = sign(payload);
    String token = Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString((payload + "|" + signature).getBytes(StandardCharsets.UTF_8));

    return new TokenPair(token, exp);
  }

  public TokenClaims parseToken(String token) {
    if (!StringUtils.hasText(token)) {
      throw new IllegalArgumentException("empty token");
    }

    String raw;
    try {
      raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("invalid token");
    }

    String[] parts = raw.split("\\|");
    if (parts.length != 3) {
      throw new IllegalArgumentException("invalid token");
    }

    String email = parts[0];
    long exp;
    try {
      exp = Long.parseLong(parts[1]);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("invalid token");
    }

    String payload = email + "|" + exp;
    String expected = sign(payload);
    if (!MessageDigest.isEqual(
        expected.getBytes(StandardCharsets.UTF_8),
        parts[2].getBytes(StandardCharsets.UTF_8)
    )) {
      throw new IllegalArgumentException("invalid token");
    }

    if (Instant.now().getEpochSecond() > exp) {
      throw new IllegalArgumentException("expired token");
    }

    return new TokenClaims(email, exp);
  }

  private String sign(String payload) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      return toHex(digest);
    } catch (Exception ex) {
      throw new IllegalStateException("token error", ex);
    }
  }

  private String toHex(byte[] bytes) {
    StringBuilder hex = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  public record TokenPair(String token, long expiresAt) {}

  public record TokenClaims(String email, long expiresAt) {}
}
