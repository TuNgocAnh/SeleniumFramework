package com.selenium.tests;

import com.selenium.framework.pages.LoginPage;
import com.selenium.framework.pages.ProductsPage;
import com.selenium.framework.utils.ExcelDataBootstrap;
import com.selenium.framework.utils.ExcelUtils;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginExcelTests extends BaseTest {

  @DataProvider(name = "excelLogin")
  public Object[][] excelLogin() {
    String path = ExcelDataBootstrap.ensureLoginSample();
    List<Map<String, String>> rows = ExcelUtils.readSheet(path, "login");
    Object[][] data = new Object[rows.size()][1];
    for (int i = 0; i < rows.size(); i++) data[i][0] = rows.get(i);
    return data;
  }

  @Test(
      groups = {"regression"},
      dataProvider = "excelLogin",
      description = "Data-driven login từ Excel")
  public void loginFromExcel(Map<String, String> row) {
    boolean expectSuccess = Boolean.parseBoolean(row.get("expectSuccess"));
    LoginPage login = new LoginPage().login(row.get("username"), row.get("password"));

    if (expectSuccess) {
      Assert.assertTrue(new ProductsPage().isLoaded(), "Login đúng nhưng không vào Products");
    } else {
      Assert.assertTrue(login.isErrorDisplayed(), "Mong đợi error nhưng không có");
      String expectedError = row.getOrDefault("expectedErrorContains", "");
      if (!expectedError.isBlank()) {
        Assert.assertTrue(
            login.getErrorMessage().toLowerCase().contains(expectedError.toLowerCase()),
            "Error không khớp. Thực tế: " + login.getErrorMessage());
      }
    }
  }
}
