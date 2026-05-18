package com.selenium.framework.pages;

import com.selenium.framework.driver.DriverFactory;
import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.Set;

public abstract class BasePage {

    protected static final Logger log = LogManager.getLogger(BasePage.class);
    protected final WebDriver driver;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    @Step("Click: {0}")
    protected void click(By locator) {
        log.debug("Click: {}", locator);
        WaitUtils.waitForClickable(locator).click();
    }

    @Step("Type vào: {0}")
    protected void type(By locator, String text) {
        log.debug("Type vào {}", locator);
        WebElement el = WaitUtils.waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    @Step("Lấy text: {0}")
    protected String getText(By locator) {
        return WaitUtils.waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return WaitUtils.waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Chọn dropdown {0} = {1}")
    protected void selectByVisibleText(By locator, String text) {
        new Select(WaitUtils.waitForVisible(locator)).selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(WaitUtils.waitForVisible(locator)).selectByValue(value);
    }

    @Step("Hover: {0}")
    protected void hover(By locator) {
        new Actions(driver).moveToElement(WaitUtils.waitForVisible(locator)).perform();
    }

    @Step("Upload file {1} vào {0}")
    protected void uploadFile(By inputLocator, String absolutePath) {
        WebElement el = WaitUtils.newWait()
                .until(ExpectedConditions.presenceOfElementLocated(inputLocator));
        el.sendKeys(new File(absolutePath).getAbsolutePath());
    }

    protected void scrollIntoView(By locator) {
        WebElement el = WaitUtils.waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    protected Object executeJs(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    // ---- iframe / window / alert ----

    @Step("Switch vào iframe: {0}")
    protected void switchToFrame(By locator) {
        WaitUtils.newWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    /** Switch sang window/tab mới (so với handle hiện tại). */
    protected void switchToNewWindow(String currentHandle) {
        WaitUtils.newWait().until(d -> d.getWindowHandles().size() > 1);
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            if (!h.equals(currentHandle)) {
                driver.switchTo().window(h);
                return;
            }
        }
    }

    protected Alert waitForAlert() {
        return WaitUtils.newWait().until(ExpectedConditions.alertIsPresent());
    }

    protected void acceptAlert() {
        try { waitForAlert().accept(); } catch (NoAlertPresentException ignored) {}
    }

    public String getCurrentUrl() { return driver.getCurrentUrl(); }
    public String getTitle() { return driver.getTitle(); }
}
