package com.selenium.framework.utils;

import com.selenium.framework.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Base64;

public final class ScreenshotUtils {

    private ScreenshotUtils() {}

    public static String captureBase64() {
        WebDriver driver = DriverFactory.getDriver();
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] captureBytes() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
