package com.selenium.framework.healing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

/**
 * Strategy thử tìm element thay thế khi locator chính fail.
 * Mỗi strategy chỉ trả về element khi tìm được CHÍNH XÁC 1 ứng viên,
 * để tránh heal nhầm sang element khác.
 */
public interface HealStrategy {

    Optional<WebElement> attempt(WebDriver driver);

    /** Mô tả ngắn dùng cho log / Allure (vd: "byText(Login)"). */
    String describe();
}
