package com.selenium.framework.pages;

import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutStepTwoPage extends BasePage {

  private final By title = By.cssSelector("[data-test='title']");
  private final By subtotalLabel = By.cssSelector("[data-test='subtotal-label']");
  private final By taxLabel = By.cssSelector("[data-test='tax-label']");
  private final By totalLabel = By.cssSelector("[data-test='total-label']");
  private final By finishBtn = By.cssSelector("[data-test='finish']");
  private final By cancelBtn = By.cssSelector("[data-test='cancel']");

  @Step("Kiểm tra trang Checkout Step 2 đã load")
  public boolean isLoaded() {
    WaitUtils.waitForUrlContains("checkout-step-two");
    return getText(title).toLowerCase().contains("overview");
  }

  public String getSubtotal() {
    return getText(subtotalLabel);
  }

  public String getTax() {
    return getText(taxLabel);
  }

  public String getTotal() {
    return getText(totalLabel);
  }

  @Step("Hoàn tất đơn hàng")
  public CheckoutCompletePage finish() {
    click(finishBtn);
    return new CheckoutCompletePage();
  }

  @Step("Cancel checkout")
  public ProductsPage cancel() {
    click(cancelBtn);
    return new ProductsPage();
  }
}
