package com.selenium.framework.utils;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sinh test data unique và traceable.
 *
 * <p>Format chuẩn: {@code <prefix>_<testName?>_<timestamp>[_<random>]}
 *
 * <p>Mục tiêu:
 *
 * <ul>
 *   <li><b>Unique</b> — timestamp millis + random suffix → tránh collision khi parallel
 *   <li><b>Traceable</b> — nhìn data biết ngay test nào tạo ra (debug DB nhanh)
 *   <li><b>No real PII</b> — domain `@test.com` cố định, không dùng dữ liệu thật
 * </ul>
 *
 * <p>Ví dụ:
 *
 * <pre>
 *   DataGenerator.generateEmail("login")   → "auto_login_1748313600123@test.com"
 *   DataGenerator.generateFirstName("e2e") → "AutoE2eFn1748313600123"
 *   DataGenerator.generatePostalCode()     → "12345"
 * </pre>
 */
public final class DataGenerator {

  private static final String EMAIL_DOMAIN = "@test.com";

  private DataGenerator() {}

  private static long timestamp() {
    return Instant.now().toEpochMilli();
  }

  private static String random(int length) {
    String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
    }
    return sb.toString();
  }

  /** Email format: {@code auto_<prefix>_<timestamp>@test.com}. */
  public static String generateEmail(String prefix) {
    return "auto_" + prefix + "_" + timestamp() + EMAIL_DOMAIN;
  }

  /** Username format: {@code auto_<prefix>_<timestamp>}. */
  public static String generateUsername(String prefix) {
    return "auto_" + prefix + "_" + timestamp();
  }

  /** First name traceable: {@code Auto<Prefix>Fn<timestamp>}. */
  public static String generateFirstName(String prefix) {
    return "Auto" + capitalize(prefix) + "Fn" + timestamp();
  }

  /** Last name traceable: {@code Auto<Prefix>Ln<timestamp>}. */
  public static String generateLastName(String prefix) {
    return "Auto" + capitalize(prefix) + "Ln" + timestamp();
  }

  /** 5-digit postal code (random, không phải format ZIP thật của country nào). */
  public static String generatePostalCode() {
    int code = 10000 + ThreadLocalRandom.current().nextInt(90000);
    return String.valueOf(code);
  }

  /** 10-digit phone bắt đầu bằng 0 (theo format VN — đổi nếu locale khác). */
  public static String generatePhone() {
    StringBuilder sb = new StringBuilder("0");
    for (int i = 0; i < 9; i++) {
      sb.append(ThreadLocalRandom.current().nextInt(10));
    }
    return sb.toString();
  }

  /** Random alphanumeric với độ dài chỉ định. */
  public static String generateText(int length) {
    return random(length);
  }

  /** Traceable code/ID: {@code <PREFIX>_<timestamp>_<rand4>}. Ví dụ {@code ORDER_1748313600123_a1b2}. */
  public static String traceableId(String prefix) {
    return prefix.toUpperCase() + "_" + timestamp() + "_" + random(4);
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty()) return "";
    return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
  }
}
