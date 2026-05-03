package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {
  private final RestTemplate restTemplate;
  private final String authUrl;

  public AuthClient(RestTemplateBuilder builder, @Value("${AUTH_URL:http://auth:8081}") String authUrl) {
    this.restTemplate = builder
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(5))
        .build();
    this.authUrl = stripTrailingSlash(authUrl);
  }

  public String validate(String token) {
    try {
      ResponseEntity<AuthValidateResponse> response = restTemplate.postForEntity(
          authUrl + "/validate",
          Map.of("token", token),
          AuthValidateResponse.class
      );

      AuthValidateResponse body = response.getBody();
      if (!response.getStatusCode().is2xxSuccessful() || body == null || !StringUtils.hasText(body.email())) {
        throw new IllegalStateException("unauthorized");
      }

      return body.email();
    } catch (RestClientException ex) {
      throw new IllegalStateException("unauthorized", ex);
    }
  }

  private String stripTrailingSlash(String url) {
    if (!StringUtils.hasText(url)) {
      return "";
    }
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  private record AuthValidateResponse(String email, @JsonProperty("expires_at") long expiresAt) {}
}
