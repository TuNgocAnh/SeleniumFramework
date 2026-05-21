package com.selenium.framework.healing;

import com.selenium.framework.config.ConfigReader;
import com.selenium.framework.driver.DriverFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Element có khả năng tự "heal" khi locator chính fail.
 *
 * <p>Cách dùng:
 *
 * <pre>
 *   HealableElement btn = HealableElement.builder()
 *       .primary(By.id("login-button"))
 *       .fallback(ByTextStrategy.of("Login"))
 *       .fallback(ByRoleStrategy.of("button", "Login"))
 *       .build();
 *
 *   btn.find().click();
 * </pre>
 *
 * <p>Khi heal xảy ra, sự kiện được log + đính kèm vào Allure report nhưng KHÔNG nuốt fail — dev sẽ
 * nhìn thấy cảnh báo để fix locator.
 */
public final class HealableElement {

  private final By primary;
  private final List<HealStrategy> fallbacks;

  private HealableElement(Builder b) {
    this.primary = b.primary;
    this.fallbacks = List.copyOf(b.fallbacks);
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Tìm element không chờ — thử primary, rồi lần lượt fallback. Throw ngay nếu hết. Dùng khi chắc
   * chắn DOM đã render xong.
   */
  public WebElement find() {
    return tryFind(DriverFactory.getDriver())
        .orElseThrow(
            () -> {
              HealReporter.reportExhausted(primary, fallbacks.size());
              return new NoSuchElementException(
                  "Primary " + primary + " và " + fallbacks.size() + " fallback đều fail");
            });
  }

  /**
   * Tìm element có chờ — poll mỗi 500ms, mỗi lần thử cả primary + fallback. Dùng cho phần lớn case
   * (DOM có thể chưa render xong).
   *
   * @param timeoutSeconds tổng thời gian tối đa chờ
   */
  public WebElement findWithWait(int timeoutSeconds) {
    WebDriver driver = DriverFactory.getDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    try {
      return wait.until(d -> tryFind(d).orElse(null));
    } catch (TimeoutException e) {
      HealReporter.reportExhausted(primary, fallbacks.size());
      throw new NoSuchElementException(
          "Sau "
              + timeoutSeconds
              + "s, primary "
              + primary
              + " và "
              + fallbacks.size()
              + " fallback vẫn không match");
    }
  }

  /** Dùng explicitWait từ config (mặc định 15s). */
  public WebElement findWithWait() {
    return findWithWait(ConfigReader.getInt("explicitWait", 15));
  }

  /** Một lần thử primary → fallback, trả về Optional, KHÔNG throw. */
  private Optional<WebElement> tryFind(WebDriver driver) {
    try {
      return Optional.of(driver.findElement(primary));
    } catch (NoSuchElementException ignored) {
      for (HealStrategy strategy : fallbacks) {
        Optional<WebElement> healed = strategy.attempt(driver);
        if (healed.isPresent()) {
          HealReporter.reportHealed(primary, strategy);
          return healed;
        }
      }
      return Optional.empty();
    }
  }

  public By primary() {
    return primary;
  }

  public static final class Builder {
    private By primary;
    private final List<HealStrategy> fallbacks = new ArrayList<>();

    private Builder() {}

    public Builder primary(By by) {
      this.primary = by;
      return this;
    }

    public Builder fallback(HealStrategy strategy) {
      this.fallbacks.add(strategy);
      return this;
    }

    public HealableElement build() {
      if (primary == null) {
        throw new IllegalStateException("HealableElement requires a primary locator");
      }
      return new HealableElement(this);
    }
  }
}
