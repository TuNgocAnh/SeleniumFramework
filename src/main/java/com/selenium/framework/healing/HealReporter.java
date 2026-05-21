package com.selenium.framework.healing;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

/**
 * Báo cáo các heal event lên log + Allure để dev nhìn thấy ngay. Test PASS nhờ heal vẫn ghi WARNING
 * — không nuốt lỗi âm thầm.
 */
public final class HealReporter {

  private static final Logger log = LogManager.getLogger(HealReporter.class);

  private HealReporter() {}

  public static void reportHealed(By primary, HealStrategy strategy) {
    String msg =
        String.format(
            "LOCATOR HEALED — primary `%s` failed, recovered via `%s`. Update test code!",
            primary, strategy.describe());
    log.warn(msg);
    Allure.addAttachment("⚠️ Locator healed", "text/plain", msg);
  }

  public static void reportExhausted(By primary, int strategiesTried) {
    String msg =
        String.format(
            "HEAL EXHAUSTED — primary `%s` failed, %d fallback strategies tried, none matched.",
            primary, strategiesTried);
    log.error(msg);
    Allure.addAttachment("❌ Heal exhausted", "text/plain", msg);
  }
}
