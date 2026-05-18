package com.selenium.framework.pages;

import com.selenium.framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductsPage extends BasePage {

    private final By title       = By.cssSelector(".title");
    private final By cartBadge   = By.cssSelector(".shopping_cart_badge");
    private final By addBackpack = By.id("add-to-cart-sauce-labs-backpack");

    @Step("Kiểm tra trang Products đã load")
    public boolean isLoaded() {
        WaitUtils.waitForUrlContains("inventory");
        return getText(title).toLowerCase().contains("products");
    }

    @Step("Thêm Backpack vào giỏ hàng")
    public ProductsPage addBackpackToCart() {
        click(addBackpack);
        return this;
    }

    @Step("Lấy số lượng cart badge")
    public String getCartCount() {
        return getText(cartBadge);
    }
}
