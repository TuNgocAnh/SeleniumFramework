package com.selenium.framework.healing.strategies;

import com.selenium.framework.healing.HealStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

/**
 * Fuzzy match: tìm element có attribute (vd: class, id, data-test) CHỨA fragment.
 * Sau khi tìm bằng CSS, lọc tiếp chỉ giữ element visible.
 * Đây là strategy yếu nhất — chỉ nên dùng làm last-resort fallback.
 */
public class ByAttributeContainsStrategy implements HealStrategy {

    private final String attribute;
    private final String fragment;

    private ByAttributeContainsStrategy(String attribute, String fragment) {
        this.attribute = attribute;
        this.fragment = fragment;
    }

    public static ByAttributeContainsStrategy of(String attribute, String fragment) {
        return new ByAttributeContainsStrategy(attribute, fragment);
    }

    @Override
    public Optional<WebElement> attempt(WebDriver driver) {
        String css = String.format("[%s*='%s']", attribute, fragment);
        List<WebElement> candidates = driver.findElements(By.cssSelector(css)).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        return candidates.size() == 1 ? Optional.of(candidates.get(0)) : Optional.empty();
    }

    @Override
    public String describe() {
        return "byAttrContains(" + attribute + "*=" + fragment + ")";
    }
}
