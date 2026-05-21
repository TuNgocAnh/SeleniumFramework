package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "Thêm Backpack vào cart, badge hiển thị 1")
  public void addItemToCart() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    ProductsPage products = new ProductsPage();
    Assert.assertTrue(products.isLoaded());
    products.addBackpackToCart();
    Assert.assertEquals(products.getCartCount(), "1", "Cart badge phải là 1");
  }
}
