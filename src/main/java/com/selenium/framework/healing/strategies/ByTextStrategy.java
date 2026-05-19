package com.selenium.framework.healing.strategies;

import com.selenium.framework.healing.HealStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

/**
 * Tìm element theo visible text khớp tuyệt đối (sau khi trim whitespace).
 * Chỉ chấp nhận khi tìm thấy đúng 1 element để tránh ambiguous.
 */
public class ByTextStrategy implements HealStrategy {

    private final String text;

    private ByTextStrategy(String text) {
        this.text = text;
    }

    public static ByTextStrategy of(String text) {
        return new ByTextStrategy(text);
    }

    @Override
    public Optional<WebElement> attempt(WebDriver driver) {
        String xpath = "//*[normalize-space(text())=" + xpathLiteral(text) + "]";
        List<WebElement> candidates = driver.findElements(By.xpath(xpath));
        return candidates.size() == 1 ? Optional.of(candidates.get(0)) : Optional.empty();
    }

    @Override
    public String describe() {
        return "byText(\"" + text + "\")";
    }

    /** Tạo XPath literal an toàn cho text có chứa cả " và '. */
    private static String xpathLiteral(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }
}
