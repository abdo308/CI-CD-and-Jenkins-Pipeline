package com.example.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
  @Bean
  public DataSource dataSource() {
    String rawUrl = env(
        "DATABASE_URL",
        "postgres://appuser:apppass@db:5432/appdb?sslmode=disable"
    );
    String jdbcUrl = toJdbcUrl(rawUrl);

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(jdbcUrl);
    return dataSource;
  }

  private static String toJdbcUrl(String rawUrl) {
    if (rawUrl == null || rawUrl.isBlank()) {
      return "";
    }
    if (rawUrl.startsWith("jdbc:")) {
      return rawUrl;
    }

    try {
      URI uri = new URI(rawUrl);
      String host = uri.getHost();
      int port = uri.getPort() == -1 ? 5432 : uri.getPort();
      String db = "";
      if (uri.getPath() != null && uri.getPath().length() > 1) {
        db = uri.getPath().substring(1);
      }

      String user = null;
      String password = null;
      if (uri.getUserInfo() != null) {
        String[] parts = uri.getUserInfo().split(":", 2);
        user = parts[0];
        if (parts.length > 1) {
          password = parts[1];
        }
      }

      List<String> params = new ArrayList<>();
      if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
        params.add(uri.getQuery());
      }
      if (user != null && !user.isBlank()) {
        params.add("user=" + urlEncode(user));
      }
      if (password != null && !password.isBlank()) {
        params.add("password=" + urlEncode(password));
      }

      StringBuilder jdbc = new StringBuilder("jdbc:postgresql://");
      jdbc.append(host);
      if (port > 0) {
        jdbc.append(":").append(port);
      }
      jdbc.append("/").append(db);

      if (!params.isEmpty()) {
        jdbc.append("?").append(String.join("&", params));
      }

      return jdbc.toString();
    } catch (URISyntaxException ex) {
      return rawUrl;
    }
  }

  private static String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static String env(String key, String fallback) {
    String value = System.getenv(key);
    if (value == null || value.isBlank()) {
      return fallback;
    }
    return value;
  }
}
