package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
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
@Feature("Products")
public class ProductsTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "SAUCE_INV_TC_02 — Thêm Backpack vào cart, badge hiển thị 1")
  @Story("Add to cart")
  @Severity(SeverityLevel.BLOCKER)
  public void addItemToCart() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    ProductsPage products = new ProductsPage();
    Assert.assertTrue(products.isLoaded(), "Products page phải load sau login");
    products.addBackpackToCart();
    Assert.assertEquals(products.getCartCount(), "1", "Cart badge phải là 1");
  }
}
