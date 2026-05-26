package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import com.selenium.framework.utils.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "Login thành công với standard_user")
  public void loginSuccess() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    Assert.assertTrue(new ProductsPage().isLoaded(), "Không vào được trang Products");
  }

  @Test(
      groups = {"regression"},
      description = "Login với password sai → hiển thị error")
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
  public void loginLockedUser() {
    LoginPage login =
        new LoginPage().login(CredentialsManager.user("locked"), CredentialsManager.pass("locked"));
    Assert.assertTrue(login.getErrorMessage().toLowerCase().contains("locked out"));
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "SAUCE_LOGIN_TC_03 — Username trống hiển thị validation")
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
