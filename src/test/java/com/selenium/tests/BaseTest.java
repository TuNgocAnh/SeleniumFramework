package com.selenium.tests;

import com.selenium.framework.config.ConfigReader;
import com.selenium.framework.driver.DriverFactory;
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
        String browser = (browserParam != null && !browserParam.isBlank())
                ? browserParam
                : ConfigReader.get("browser", "chrome");
        log.info("Khởi tạo driver: {}", browser);
        DriverFactory.initDriver(browser);
        DriverFactory.getDriver().get(ConfigReader.get("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
