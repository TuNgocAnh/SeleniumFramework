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
}
