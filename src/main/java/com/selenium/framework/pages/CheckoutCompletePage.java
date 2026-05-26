package com.selenium.framework.pages;

import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutCompletePage extends BasePage {

  private final By header = By.cssSelector("[data-test='complete-header']");
  private final By backHomeBtn = By.cssSelector("[data-test='back-to-products']");

  @Step("Kiểm tra trang Checkout Complete đã load")
  public boolean isLoaded() {
    WaitUtils.waitForUrlContains("checkout-complete");
    return isDisplayed(header);
  }

  public String getCompleteHeader() {
    return getText(header);
  }

  @Step("Quay về Products")
  public ProductsPage backHome() {
    click(backHomeBtn);
    return new ProductsPage();
  }
}
