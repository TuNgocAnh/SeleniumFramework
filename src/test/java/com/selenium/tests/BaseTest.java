package com.selenium.tests;

import com.selenium.framework.config.ConfigReader;
import com.selenium.framework.driver.DriverFactory;
import com.selenium.framework.utils.Assertions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public abstract class BaseTest {

  protected static final Logger log = LogManager.getLogger(BaseTest.class);

  @BeforeMethod(alwaysRun = true)
  @Parameters({"browser"})
  public void setUp(@Optional String browserParam) {
    String browser =
        (browserParam != null && !browserParam.isBlank())
            ? browserParam
            : ConfigReader.get("browser", "chrome");
    log.info("Khởi tạo driver: {}", browser);
    DriverFactory.initDriver(browser);
    DriverFactory.getDriver().get(ConfigReader.get("baseUrl"));
  }

  /**
   * Teardown — gồm 2 việc theo thứ tự:
   *
   * <ol>
   *   <li>Safety net cho soft assertion: tự gọi {@link Assertions#assertAll()} để raise mọi soft
   *       failure đã tích luỹ. Nếu test QUÊN gọi {@code assertAll()} ở cuối, các failure sẽ bị
   *       nuốt và test PASS sai. Method này bảo vệ team khỏi pitfall đó.
   *   <li>Đóng driver — luôn chạy trong {@code finally} dù soft assertion fail.
   * </ol>
   *
   * <p>Nếu test KHÔNG dùng soft assertion → {@code assertAll()} là no-op, không ảnh hưởng.
   */
  @AfterMethod(alwaysRun = true)
  public void tearDown() {
    try {
      Assertions.assertAll();
    } finally {
      DriverFactory.quitDriver();
    }
  }
}
