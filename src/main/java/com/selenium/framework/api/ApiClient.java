package com.selenium.framework.api;

import static io.restassured.config.HttpClientConfig.httpClientConfig;

import com.selenium.framework.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.concurrent.TimeUnit;

/**
 * Singleton REST Assured client configured from framework properties. Reads {@code apiBaseUrl},
 * {@code apiTimeoutMs} from config.properties (with sensible defaults).
 */
public final class ApiClient {

  private static final int DEFAULT_TIMEOUT_MS = 30_000;

  private static final RequestSpecification SPEC = buildSpec();

  private ApiClient() {}

  public static RequestSpecification spec() {
    return RestAssured.given().spec(SPEC);
  }

  private static RequestSpecification buildSpec() {
    String baseUrl = ConfigReader.get("apiBaseUrl", "");
    int timeoutMs = ConfigReader.getInt("apiTimeoutMs", DEFAULT_TIMEOUT_MS);

    RestAssuredConfig config =
        RestAssuredConfig.config()
            .httpClient(
                httpClientConfig()
                    .setParam("http.connection.timeout", timeoutMs)
                    .setParam("http.socket.timeout", timeoutMs)
                    .setParam("http.connection-manager.timeout", (long) timeoutMs));

    RequestSpecBuilder builder =
        new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setConfig(config)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter());

    if (!baseUrl.isBlank()) {
      builder.setBaseUri(baseUrl);
    }
    return builder.build();
  }

  public static long timeoutSeconds() {
    return TimeUnit.MILLISECONDS.toSeconds(ConfigReader.getInt("apiTimeoutMs", DEFAULT_TIMEOUT_MS));
  }
}
