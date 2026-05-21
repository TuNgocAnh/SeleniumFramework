package com.selenium.framework.driver;

import com.selenium.framework.config.ConfigReader;
import com.selenium.framework.exceptions.FrameworkException;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class DriverFactory {

  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  private DriverFactory() {}

  public static WebDriver getDriver() {
    WebDriver d = DRIVER.get();
    if (d == null) throw new FrameworkException("WebDriver chưa được khởi tạo cho thread hiện tại");
    return d;
  }

  public static void initDriver(String browser) {
    if (DRIVER.get() != null) return;

    boolean headless = ConfigReader.getBool("headless", false);
    boolean mobile = ConfigReader.getBool("mobileEmulation", false);
    String gridUrl = ConfigReader.get("gridUrl", "").trim();

    WebDriver driver;
    if (!gridUrl.isBlank()) {
      driver = createRemote(browser, headless, mobile, gridUrl);
    } else {
      driver =
          switch (browser.toLowerCase()) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless, mobile));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            default -> throw new FrameworkException("Browser không hỗ trợ: " + browser);
          };
      if (browser.equalsIgnoreCase("chrome"))
        setupWdm(WebDriverManager.chromedriver(), "chromeVersion");
      if (browser.equalsIgnoreCase("edge")) setupWdm(WebDriverManager.edgedriver(), "edgeVersion");
      if (browser.equalsIgnoreCase("firefox"))
        setupWdm(WebDriverManager.firefoxdriver(), "firefoxVersion");
    }

    if (!mobile) driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    driver
        .manage()
        .timeouts()
        .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("pageLoadWait", 30)));
    driver
        .manage()
        .timeouts()
        .scriptTimeout(Duration.ofSeconds(ConfigReader.getInt("scriptTimeout", 30)));
    DRIVER.set(driver);
  }

  public static void quitDriver() {
    WebDriver d = DRIVER.get();
    if (d != null) {
      d.quit();
      DRIVER.remove();
    }
  }

  private static void setupWdm(WebDriverManager wdm, String versionKey) {
    String v = ConfigReader.get(versionKey, "");
    if (!v.isBlank()) wdm.browserVersion(v);
    wdm.setup();
  }

  private static ChromeOptions chromeOptions(boolean headless, boolean mobile) {
    ChromeOptions opts = new ChromeOptions();
    opts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
    opts.addArguments("--remote-allow-origins=*", "--disable-notifications");
    if (headless)
      opts.addArguments(
          "--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
    if (mobile) {
      Map<String, String> mobileEmulation = new HashMap<>();
      mobileEmulation.put("deviceName", ConfigReader.get("mobileDevice", "Pixel 7"));
      opts.setExperimentalOption("mobileEmulation", mobileEmulation);
    }
    return opts;
  }

  private static EdgeOptions edgeOptions(boolean headless) {
    EdgeOptions opts = new EdgeOptions();
    opts.addArguments("--remote-allow-origins=*", "--disable-notifications");
    if (headless) opts.addArguments("--headless=new", "--window-size=1920,1080");
    return opts;
  }

  private static FirefoxOptions firefoxOptions(boolean headless) {
    FirefoxOptions opts = new FirefoxOptions();
    if (headless) opts.addArguments("-headless");
    return opts;
  }

  private static WebDriver createRemote(
      String browser, boolean headless, boolean mobile, String gridUrl) {
    MutableCapabilities caps =
        switch (browser.toLowerCase()) {
          case "chrome" -> chromeOptions(headless, mobile);
          case "edge" -> edgeOptions(headless);
          case "firefox" -> firefoxOptions(headless);
          default -> throw new FrameworkException("Browser không hỗ trợ: " + browser);
        };
    try {
      return new RemoteWebDriver(URI.create(gridUrl).toURL(), caps);
    } catch (MalformedURLException | IllegalArgumentException e) {
      throw new FrameworkException("gridUrl không hợp lệ: " + gridUrl, e);
    }
  }
}
