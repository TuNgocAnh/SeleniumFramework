package com.selenium.framework.utils;

import org.testng.asserts.SoftAssert;

/**
 * Wrapper SoftAssert per-thread. Gọi {@link #assertAll()} ở cuối test để raise tất cả lỗi tích luỹ.
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
}
