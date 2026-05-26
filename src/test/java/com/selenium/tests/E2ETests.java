package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.CheckoutCompletePage;
import com.selenium.framework.pages.CheckoutStepTwoPage;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class E2ETests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description =
          "SAUCE_E2E_TC_01 — Full happy path: Login → Add Backpack → Checkout → Finish")
  public void fullPurchaseHappyPath() {
    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));

    CheckoutStepTwoPage step2 =
        new ProductsPage()
            .addBackpackToCart()
            .openCart()
            .checkout()
            .fillInfo("John", "Doe", "10000")
            .clickContinue();
    Assert.assertTrue(step2.isLoaded(), "Phải vào Checkout Overview (step 2)");
    Assert.assertEquals(step2.getSubtotal(), "Item total: $29.99", "Subtotal phải đúng");
    Assert.assertEquals(step2.getTax(), "Tax: $2.40", "Tax phải đúng 8% giá trị");
    Assert.assertEquals(step2.getTotal(), "Total: $32.39", "Total phải = subtotal + tax");

    CheckoutCompletePage complete = step2.finish();
    Assert.assertTrue(complete.isLoaded(), "Phải vào trang Checkout Complete");
    Assert.assertEquals(
        complete.getCompleteHeader(),
        "Thank you for your order!",
        "Header xác nhận đơn hàng phải hiển thị đúng");
  }
}
