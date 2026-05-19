package com.selenium.framework.healing.strategies;

import com.selenium.framework.healing.HealStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

/**
 * Tìm element theo (role + accessible name).
 * Hỗ trợ:
 *  - tag native tương ứng role (button, link, input)
 *  - explicit attribute role="..."
 *  - accessible name match qua aria-label / value / text
 */
public class ByRoleStrategy implements HealStrategy {

    private final String role;
    private final String accessibleName;

    private ByRoleStrategy(String role, String accessibleName) {
        this.role = role;
        this.accessibleName = accessibleName;
    }

    public static ByRoleStrategy of(String role, String accessibleName) {
        return new ByRoleStrategy(role, accessibleName);
    }

    @Override
    public Optional<WebElement> attempt(WebDriver driver) {
        String name = literal(accessibleName);
        String xpath = switch (role.toLowerCase()) {
            case "button" -> String.format(
                    "//button[@aria-label=%1$s or normalize-space(text())=%1$s or @value=%1$s] | " +
                    "//input[(@type='button' or @type='submit') and @value=%1$s] | " +
                    "//*[@role='button' and (@aria-label=%1$s or normalize-space(text())=%1$s)]",
                    name);
            case "link" -> String.format(
                    "//a[@aria-label=%1$s or normalize-space(text())=%1$s] | " +
                    "//*[@role='link' and (@aria-label=%1$s or normalize-space(text())=%1$s)]",
                    name);
            case "textbox" -> String.format(
                    "//input[(@type='text' or @type='email' or @type='password' or not(@type)) " +
                    "and (@aria-label=%1$s or @placeholder=%1$s or @name=%1$s)] | " +
                    "//*[@role='textbox' and (@aria-label=%1$s or @placeholder=%1$s)]",
                    name);
            default -> String.format(
                    "//*[@role=%2$s and (@aria-label=%1$s or normalize-space(text())=%1$s)]",
                    name, literal(role));
        };

        List<WebElement> candidates = driver.findElements(By.xpath(xpath));
        return candidates.size() == 1 ? Optional.of(candidates.get(0)) : Optional.empty();
    }

    @Override
    public String describe() {
        return "byRole(" + role + ", \"" + accessibleName + "\")";
    }

    private static String literal(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }
}
