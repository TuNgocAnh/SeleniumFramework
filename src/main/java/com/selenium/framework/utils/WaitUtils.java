package com.selenium.framework.utils;

import com.selenium.framework.config.ConfigReader;
import com.selenium.framework.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private WaitUtils() {}

    public static WebDriverWait newWait() {
        int sec = ConfigReader.getInt("explicitWait", 15);
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(sec));
    }

    public static WebDriverWait newWait(int seconds) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(seconds));
    }

    public static WebElement waitForVisible(By locator) {
        return newWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(By locator) {
        return newWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitForInvisible(By locator) {
        return newWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(String fragment) {
        return newWait().until(ExpectedConditions.urlContains(fragment));
    }
}
