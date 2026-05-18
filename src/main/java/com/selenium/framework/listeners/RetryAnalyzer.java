package com.selenium.framework.listeners;

import com.selenium.framework.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Map<String, Integer> COUNTS = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigReader.getInt("retryCount", 2);
        String key = key(result);
        int current = COUNTS.getOrDefault(key, 0);
        if (current < max) {
            COUNTS.put(key, current + 1);
            return true;
        }
        COUNTS.remove(key);
        return false;
    }

    private static String key(ITestResult result) {
        return result.getInstanceName() + "#" + result.getMethod().getMethodName();
    }
}
