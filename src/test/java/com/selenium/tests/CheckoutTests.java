package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.CheckoutStepOnePage;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Saucedemo")
@Feature("Checkout")
public class CheckoutTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description =
          "SAUCE_CHK1_TC_02 — Bỏ trống First Name khi checkout hiển thị validation error")
  @Story("Step 1 field validation")
  @Severity(SeverityLevel.CRITICAL)
  public void firstNameRequiredAtCheckoutStepOne() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    CheckoutStepOnePage step1 =
        new ProductsPage().addBackpackToCart().openCart().checkout();
    Assert.assertTrue(step1.isLoaded(), "Phải đang ở Checkout Step 1");

    step1.fillInfo("", "Doe", "10000").clickContinueExpectError();
    Assert.assertTrue(
        step1.isErrorDisplayed(), "Phải hiển thị error banner khi First Name trống");
    Assert.assertEquals(
        step1.getErrorMessage(),
        "Error: First Name is required",
        "Nội dung error phải đúng quy định nghiệp vụ");
  }
}
