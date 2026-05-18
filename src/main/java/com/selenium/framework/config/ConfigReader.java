package com.selenium.framework.config;

import com.selenium.framework.exceptions.FrameworkException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigReader {

    private static final Logger LOG = LogManager.getLogger(ConfigReader.class);
    private static final Properties PROPS = new Properties();

    static {
        load(FrameworkConstants.DEFAULT_CONFIG, true);
        String env = System.getProperty("env", PROPS.getProperty("env", ""));
        if (env != null && !env.isBlank()) {
            String envFile = FrameworkConstants.CONFIG_DIR + env + ".properties";
            if (new File(envFile).exists()) {
                load(envFile, true);
            } else {
                LOG.warn("Không tìm thấy env config: " + envFile + " — bỏ qua, dùng config mặc định.");
            }
        }
    }

    private ConfigReader() {}

    private static void load(String path, boolean required) {
        try (FileInputStream fis = new FileInputStream(path)) {
            PROPS.load(fis);
        } catch (IOException e) {
            if (required) throw new FrameworkException("Không đọc được config: " + path, e);
            LOG.warn("Không đọc được config (bỏ qua): " + path);
        }
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String value = PROPS.getProperty(key);
        if (value == null) throw new FrameworkException("Không tìm thấy key '" + key + "' trong config");
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String value = PROPS.getProperty(key, defaultValue);
        return value == null ? null : value.trim();
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }
}
