package com.selenium.framework.utils;

import org.testng.asserts.SoftAssert;

/**
 * Wrapper SoftAssert per-thread. Gọi {@link #assertAll()} ở cuối test để raise tất cả lỗi tích luỹ.
 * Cung cấp các helper tiện lợi để gọi trực tiếp mà không cần lấy instance soft().
 */
public final class Assertions {

  private static final ThreadLocal<SoftAssert> SOFT = ThreadLocal.withInitial(SoftAssert::new);

  private Assertions() {}

  public static SoftAssert soft() {
    return SOFT.get();
  }

  public static void assertAll() {
    try {
      SOFT.get().assertAll();
    } finally {
      SOFT.remove();
    }
  }

  public static void assertEquals(Object actual, Object expected, String message) {
    SOFT.get().assertEquals(actual, expected, message);
  }

  public static void assertEquals(Object actual, Object expected) {
    SOFT.get().assertEquals(actual, expected);
  }

  public static void assertTrue(boolean condition, String message) {
    SOFT.get().assertTrue(condition, message);
  }

  public static void assertFalse(boolean condition, String message) {
    SOFT.get().assertFalse(condition, message);
  }

  public static void assertNotNull(Object value, String message) {
    SOFT.get().assertNotNull(value, message);
  }

  public static void assertNull(Object value, String message) {
    SOFT.get().assertNull(value, message);
  }

  public static void fail(String message) {
    SOFT.get().fail(message);
  }
}
