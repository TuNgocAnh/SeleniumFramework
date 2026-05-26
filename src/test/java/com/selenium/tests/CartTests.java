package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.CartPage;
import com.selenium.framework.pages.CheckoutStepOnePage;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "SAUCE_CART_TC_05 — Checkout từ giỏ có sản phẩm chuyển sang step 1")
  public void checkoutFromCartWithItem() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));
    ProductsPage products = new ProductsPage();
    Assert.assertTrue(products.isLoaded(), "Products page phải load sau login");

    CartPage cart = products.addBackpackToCart().openCart();
    Assert.assertTrue(cart.isLoaded(), "Cart page phải load sau khi click giỏ");
    Assert.assertEquals(
        cart.getFirstItemName(), "Sauce Labs Backpack", "Sản phẩm trong giỏ phải là Backpack");

    CheckoutStepOnePage step1 = cart.checkout();
    Assert.assertTrue(
        step1.isLoaded(), "Phải chuyển sang Checkout Step 1 sau khi click Checkout");
  }
}
