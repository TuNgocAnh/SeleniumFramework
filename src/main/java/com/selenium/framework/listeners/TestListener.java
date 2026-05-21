package com.selenium.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.selenium.framework.reports.ExtentManager;
import com.selenium.framework.reports.ExtentTestManager;
import com.selenium.framework.reports.ReportRetention;
import com.selenium.framework.utils.ScreenshotUtils;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener, ISuiteListener {

  private static final Logger log = LogManager.getLogger(TestListener.class);

  @Override
  public void onStart(ISuite suite) {
    log.info("===== START SUITE: {} =====", suite.getName());
    ReportRetention.cleanup();
    ExtentManager.getInstance();
  }

  @Override
  public void onFinish(ISuite suite) {
    ExtentManager.flush();
    log.info("===== FINISH SUITE: {} =====", suite.getName());
  }

  @Override
  public void onTestStart(ITestResult result) {
    log.info(">>> START TEST: {}", result.getMethod().getMethodName());
    ExtentTest test =
        ExtentManager.getInstance()
            .createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
    ExtentTestManager.setTest(test);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    log.info("PASS: {}", result.getMethod().getMethodName());
    ExtentTest test = ExtentTestManager.getTest();
    if (test != null) test.pass("Test passed");
    ExtentTestManager.remove();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    String name = result.getMethod().getMethodName();
    log.error(
        "FAIL: {} — {}",
        name,
        result.getThrowable() != null ? result.getThrowable().getMessage() : "");
    ExtentTest test = ExtentTestManager.getTest();
    if (test != null) {
      try {
        String base64 = ScreenshotUtils.captureBase64();
        test.fail(
            result.getThrowable(),
            MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
      } catch (Exception e) {
        test.fail(result.getThrowable());
      }
    }
    attachAllureScreenshot();
    ExtentTestManager.remove();
  }

  @Attachment(value = "Failure screenshot", type = "image/png")
  private byte[] attachAllureScreenshot() {
    try {
      return ScreenshotUtils.captureBytes();
    } catch (Exception e) {
      return new byte[0];
    }
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    log.warn("SKIP: {}", result.getMethod().getMethodName());
    ExtentTest test = ExtentTestManager.getTest();
    if (test != null)
      test.skip(result.getThrowable() != null ? result.getThrowable() : new Throwable("Skipped"));
    ExtentTestManager.remove();
  }
}
