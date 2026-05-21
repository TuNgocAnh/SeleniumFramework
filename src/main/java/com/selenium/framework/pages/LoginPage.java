package com.selenium.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By userInput = By.id("user-name");
    private final By passInput = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By errorMsg = By.cssSelector("h3[data-test='error']");
    private final By errorCloseBtn = By.cssSelector("[data-test='error-button']");
    private final By loginLogo = By.className("login_logo");

    @Step("Login với user: {0}")
    public LoginPage login(String user, String pass) {
        type(userInput, user);
        type(passInput, pass);
        click(loginBtn);
        return this;
    }

    @Step("Lấy nội dung error")
    public String getErrorMessage() {
        return getText(errorMsg);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMsg);
    }

    @Step("Đóng error banner")
    public LoginPage dismissError() {
        click(errorCloseBtn);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(loginLogo) && isDisplayed(loginBtn);
    }
}
