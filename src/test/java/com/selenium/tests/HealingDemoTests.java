package com.selenium.tests;

import com.selenium.framework.config.CredentialsManager;
import com.selenium.framework.driver.DriverFactory;
import com.selenium.framework.healing.HealableElement;
import com.selenium.framework.healing.strategies.ByAttributeContainsStrategy;
import com.selenium.framework.healing.strategies.ByRoleStrategy;
import com.selenium.framework.healing.strategies.ByTextStrategy;
import com.selenium.framework.pages.ProductsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Demo cho cơ chế self-healing locator.
 *
 * <p>Mỗi test cố tình dùng locator chính SAI để buộc cơ chế heal kick-in. Test PASS = healing đã
 * recover thành công element thực tế trên trang.
 */
public class HealingDemoTests extends BaseTest {

  @Test(
      groups = {"regression"},
      description = "Heal primary id sai → recover bằng visible text 'LOGIN'")
  public void healByVisibleText() {
    // Trên saucedemo.com, button thực tế là id="login-button" với value="Login".
    // Cố tình truyền id sai để demo heal.
    HealableElement loginBtn =
        HealableElement.builder()
            .primary(By.id("login-button-DOES-NOT-EXIST"))
            .fallback(ByTextStrategy.of("LOGIN")) // sẽ match value="Login" qua role
            .fallback(ByRoleStrategy.of("button", "Login")) // safety net
            .build();

    // Điền form trước
    DriverFactory.getDriver()
        .findElement(By.id("user-name"))
        .sendKeys(CredentialsManager.user("standard"));
    DriverFactory.getDriver()
        .findElement(By.id("password"))
        .sendKeys(CredentialsManager.pass("standard"));

    // Click qua HealableElement — primary fail → fallback recover
    loginBtn.find().click();

    Assert.assertTrue(new ProductsPage().isLoaded(), "Healing không recover được login button");
  }

  @Test(
      groups = {"regression"},
      description = "Heal primary css sai → recover bằng attribute fuzzy 'login_credentials'")
  public void healByAttributeContains() {
    HealableElement credentialsBox =
        HealableElement.builder()
            .primary(By.cssSelector(".login_credentials_OLD_CLASS"))
            .fallback(ByAttributeContainsStrategy.of("class", "login_credentials"))
            .build();

    Assert.assertTrue(
        credentialsBox.find().isDisplayed(), "Healing không tìm được credentials box");
  }

  @Test(
      groups = {"regression"},
      description = "Tất cả strategy fail → đúng kỳ vọng throw NoSuchElementException")
  public void healExhaustedShouldThrow() {
    HealableElement nonExistent =
        HealableElement.builder()
            .primary(By.id("absolutely-not-on-page"))
            .fallback(ByTextStrategy.of("Definitely Not Here Either"))
            .fallback(ByRoleStrategy.of("button", "Phantom"))
            .build();

    Assert.assertThrows(NoSuchElementException.class, nonExistent::find);
  }

  @Test(
      groups = {"regression"},
      description = "findWithWait() chờ DOM render xong trước khi quyết định heal")
  public void healWithWaitForDomRender() {
    // Sau khi click login, ProductsPage chưa render xong ngay lập tức.
    // findWithWait sẽ poll mỗi 500ms — primary có thể xuất hiện ở tick thứ 2-3.
    DriverFactory.getDriver()
        .findElement(By.id("user-name"))
        .sendKeys(CredentialsManager.user("standard"));
    DriverFactory.getDriver()
        .findElement(By.id("password"))
        .sendKeys(CredentialsManager.pass("standard"));
    DriverFactory.getDriver().findElement(By.id("login-button")).click();

    HealableElement productsTitle =
        HealableElement.builder()
            .primary(By.cssSelector(".title"))
            .fallback(ByTextStrategy.of("Products"))
            .build();

    // findWithWait() — chờ DOM render, không vội heal sai
    Assert.assertTrue(
        productsTitle.findWithWait().isDisplayed(), "Không lấy được products title sau khi chờ");
  }
}
