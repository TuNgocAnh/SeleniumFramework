package com.selenium.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By userInput = By.id("user-name");
    private final By passInput = By.id("password");
    private final By loginBtn  = By.id("login-button");
    private final By errorMsg  = By.cssSelector("[data-test='error']");

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
}
