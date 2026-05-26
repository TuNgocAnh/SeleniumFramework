package com.selenium.framework.pages;

import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutStepOnePage extends BasePage {

  private final By firstNameInput = By.cssSelector("[data-test='firstName']");
  private final By lastNameInput = By.cssSelector("[data-test='lastName']");
  private final By postalCodeInput = By.cssSelector("[data-test='postalCode']");
  private final By continueBtn = By.cssSelector("[data-test='continue']");
  private final By cancelBtn = By.cssSelector("[data-test='cancel']");
  private final By errorMsg = By.cssSelector("h3[data-test='error']");

  @Step("Kiểm tra trang Checkout Step 1 đã load")
  public boolean isLoaded() {
    WaitUtils.waitForUrlContains("checkout-step-one");
    return isDisplayed(firstNameInput);
  }

  @Step("Điền thông tin: {0} {1}, zip {2}")
  public CheckoutStepOnePage fillInfo(String firstName, String lastName, String zip) {
    type(firstNameInput, firstName);
    type(lastNameInput, lastName);
    type(postalCodeInput, zip);
    return this;
  }

  @Step("Click Continue")
  public CheckoutStepTwoPage clickContinue() {
    click(continueBtn);
    return new CheckoutStepTwoPage();
  }

  /** Click Continue mà KHÔNG chuyển trang (dùng cho test validation). */
  @Step("Click Continue (expect validation error)")
  public CheckoutStepOnePage clickContinueExpectError() {
    click(continueBtn);
    return this;
  }

  @Step("Lấy nội dung error")
  public String getErrorMessage() {
    return getText(errorMsg);
  }

  public boolean isErrorDisplayed() {
    return isDisplayed(errorMsg);
  }

  @Step("Cancel checkout")
  public CartPage cancel() {
    click(cancelBtn);
    return new CartPage();
  }
}
