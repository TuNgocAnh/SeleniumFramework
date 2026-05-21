package com.selenium.tests;

import com.selenium.framework.config.FrameworkConstants;
import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import com.selenium.framework.utils.JsonUtils;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginDataDrivenTests extends BaseTest {

  @DataProvider(name = "loginData")
  public Object[][] loginData() {
    List<Map<String, Object>> rows =
        JsonUtils.readList(FrameworkConstants.TESTDATA_DIR + "login_data.json");
    Object[][] data = new Object[rows.size()][1];
    for (int i = 0; i < rows.size(); i++) data[i][0] = rows.get(i);
    return data;
  }

  @Test(
      groups = {"regression"},
      dataProvider = "loginData",
      description = "Data-driven login từ JSON")
  public void loginWithData(Map<String, Object> row) {
    String user = String.valueOf(row.get("username"));
    String pass = String.valueOf(row.get("password"));
    boolean expectSuccess = Boolean.TRUE.equals(row.get("expectSuccess"));
    String expectedError = String.valueOf(row.getOrDefault("expectedErrorContains", ""));

    LoginPage login = new LoginPage().login(user, pass);

    if (expectSuccess) {
      Assert.assertTrue(
          new ProductsPage().isLoaded(), "Login đúng nhưng không vào Products: " + user);
    } else {
      Assert.assertTrue(login.isErrorDisplayed(), "Mong đợi error nhưng không hiển thị: " + user);
      if (!expectedError.isBlank()) {
        Assert.assertTrue(
            login.getErrorMessage().toLowerCase().contains(expectedError.toLowerCase()),
            "Error không khớp. Thực tế: " + login.getErrorMessage());
      }
    }
  }
}
