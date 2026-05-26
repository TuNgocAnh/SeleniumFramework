package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import com.selenium.framework.utils.Assertions;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Saucedemo")
@Feature("Login")
public class LoginTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "Login thành công với standard_user")
  @Story("Happy login")
  @Severity(SeverityLevel.BLOCKER)
  public void loginSuccess() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    Assert.assertTrue(new ProductsPage().isLoaded(), "Không vào được trang Products");
  }

  @Test(
      groups = {"regression"},
      description = "Login với password sai → hiển thị error")
  @Story("Invalid credentials")
  @Severity(SeverityLevel.CRITICAL)
  public void loginWrongPassword() {
    LoginPage login = new LoginPage().login(CredentialsManager.user("standard"), "wrong_password");
    Assertions.soft().assertTrue(login.isErrorDisplayed(), "Không hiển thị error");
    Assertions.soft()
        .assertTrue(
            login.getErrorMessage().contains("Username and password do not match"),
            "Nội dung error không khớp");
    Assertions.assertAll();
  }

  @Test(
      groups = {"regression"},
      description = "Locked user không login được")
  @Story("Locked user")
  @Severity(SeverityLevel.CRITICAL)
  public void loginLockedUser() {
    LoginPage login =
        new LoginPage().login(CredentialsManager.user("locked"), CredentialsManager.pass("locked"));
    Assert.assertTrue(login.getErrorMessage().toLowerCase().contains("locked out"));
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "SAUCE_LOGIN_TC_03 — Username trống hiển thị validation")
  @Story("Field validation")
  @Severity(SeverityLevel.CRITICAL)
  public void loginEmptyUsername() {
    LoginPage login = new LoginPage().login("", "secret_sauce");
    Assert.assertTrue(
        login.isErrorDisplayed(), "Phải hiển thị error banner khi bỏ trống username");
    Assert.assertEquals(
        login.getErrorMessage(),
        "Epic sadface: Username is required",
        "Nội dung error phải đúng quy định nghiệp vụ");
  }
}
