package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.pages.CheckoutCompletePage;
import com.selenium.framework.pages.CheckoutStepTwoPage;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import com.selenium.framework.utils.Assertions;
import com.selenium.framework.utils.DataGenerator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

@Epic("Saucedemo")
@Feature("End-to-end purchase")
public class E2ETests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description =
          "SAUCE_E2E_TC_01 — Full happy path: Login → Add Backpack → Checkout → Finish")
  @Story("Full purchase flow")
  @Severity(SeverityLevel.BLOCKER)
  public void fullPurchaseHappyPath() {
    // Data traceable — DB query `WHERE first_name LIKE 'AutoE2e%'` để cleanup
    String firstName = DataGenerator.generateFirstName("e2e");
    String lastName = DataGenerator.generateLastName("e2e");
    String zip = DataGenerator.generatePostalCode();

    new LoginPage().login(CredentialsManager.user("standard"), CredentialsManager.pass("standard"));

    CheckoutStepTwoPage step2 =
        new ProductsPage()
            .addBackpackToCart()
            .openCart()
            .checkout()
            .fillInfo(firstName, lastName, zip)
            .clickContinue();

    // Soft assert — check cả subtotal/tax/total trong 1 lần chạy, không stop ở fail đầu tiên
    Assertions.assertTrue(step2.isLoaded(), "Phải vào Checkout Overview (step 2)");
    Assertions.assertEquals(step2.getSubtotal(), "Item total: $29.99", "Subtotal phải đúng");
    Assertions.assertEquals(step2.getTax(), "Tax: $2.40", "Tax phải đúng 8% giá trị");
    Assertions.assertEquals(step2.getTotal(), "Total: $32.39", "Total phải = subtotal + tax");

    CheckoutCompletePage complete = step2.finish();
    Assertions.assertTrue(complete.isLoaded(), "Phải vào trang Checkout Complete");
    Assertions.assertEquals(
        complete.getCompleteHeader(),
        "Thank you for your order!",
        "Header xác nhận đơn hàng phải hiển thị đúng");

    Assertions.assertAll(); // raise tất cả lỗi tích luỹ — nếu có 3 fail thì report cả 3
  }
}
