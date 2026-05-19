package com.selenium.framework.healing;

import com.selenium.framework.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Element có khả năng tự "heal" khi locator chính fail.
 * <p>
 * Cách dùng:
 * <pre>
 *   HealableElement btn = HealableElement.builder()
 *       .primary(By.id("login-button"))
 *       .fallback(ByTextStrategy.of("Login"))
 *       .fallback(ByRoleStrategy.of("button", "Login"))
 *       .build();
 *
 *   btn.find().click();
 * </pre>
 * <p>
 * Khi heal xảy ra, sự kiện được log + đính kèm vào Allure report nhưng KHÔNG nuốt fail —
 * dev sẽ nhìn thấy cảnh báo để fix locator.
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

    /** Tìm element — thử primary trước, sau đó lần lượt fallback. */
    public WebElement find() {
        WebDriver driver = DriverFactory.getDriver();

        try {
            return driver.findElement(primary);
        } catch (NoSuchElementException primaryFail) {
            for (HealStrategy strategy : fallbacks) {
                Optional<WebElement> healed = strategy.attempt(driver);
                if (healed.isPresent()) {
                    HealReporter.reportHealed(primary, strategy);
                    return healed.get();
                }
            }
            HealReporter.reportExhausted(primary, fallbacks.size());
            throw primaryFail;
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
