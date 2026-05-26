package com.selenium.framework.pages;

import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CartPage extends BasePage {

  private final By title = By.cssSelector("[data-test='title']");
  private final By cartList = By.cssSelector("[data-test='cart-list']");
  private final By itemName = By.cssSelector("[data-test='inventory-item-name']");
  private final By checkoutBtn = By.cssSelector("[data-test='checkout']");
  private final By continueShoppingBtn = By.cssSelector("[data-test='continue-shopping']");

  @Step("Kiểm tra trang Cart đã load")
  public boolean isLoaded() {
    WaitUtils.waitForUrlContains("cart");
    return getText(title).toLowerCase().contains("your cart");
  }

  @Step("Lấy tên sản phẩm đầu tiên trong giỏ")
  public String getFirstItemName() {
    return getText(itemName);
  }

  public boolean hasItems() {
    return isDisplayed(cartList) && !getFirstItemName().isEmpty();
  }

  @Step("Bắt đầu checkout")
  public CheckoutStepOnePage checkout() {
    click(checkoutBtn);
    return new CheckoutStepOnePage();
  }

  @Step("Tiếp tục mua sắm")
  public ProductsPage continueShopping() {
    click(continueShoppingBtn);
    return new ProductsPage();
  }
}
