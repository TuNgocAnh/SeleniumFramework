package com.selenium.framework.config;

import java.io.File;

public final class FrameworkConstants {

  private FrameworkConstants() {}

  public static final String USER_DIR = System.getProperty("user.dir");

  public static final String CONFIG_DIR = USER_DIR + "/src/test/resources/config/";
  public static final String DEFAULT_CONFIG = CONFIG_DIR + "config.properties";

  public static final String TESTDATA_DIR = USER_DIR + "/src/test/resources/testdata/";
  public static final String SCREENSHOT_DIR = USER_DIR + "/screenshots/";
  public static final String REPORT_DIR = USER_DIR + "/reports/";

  /** Trả về testdata theo env nếu có (testdata/<env>/), fallback về TESTDATA_DIR. */
  public static String testdataDirForEnv(String env) {
    if (env == null || env.isBlank()) return TESTDATA_DIR;
    String envDir = TESTDATA_DIR + env + "/";
    return new File(envDir).isDirectory() ? envDir : TESTDATA_DIR;
  }
}
