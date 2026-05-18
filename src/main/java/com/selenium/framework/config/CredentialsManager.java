package com.selenium.framework.config;

import com.selenium.framework.exceptions.FrameworkException;
import com.selenium.framework.utils.CryptoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Đọc credentials.properties (mỗi user 2 dòng: <alias>.user, <alias>.pass).
 * Mật khẩu có thể là plaintext, "b64:..." (obfuscation) hoặc "enc:..." (AES, cần CRED_KEY).
 */
public final class CredentialsManager {

    private static final Properties PROPS = new Properties();
    private static final String FILE = FrameworkConstants.CONFIG_DIR + "credentials.properties";

    static {
        File f = new File(FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                PROPS.load(fis);
            } catch (IOException e) {
                throw new FrameworkException("Không đọc được credentials: " + FILE, e);
            }
        }
    }

    private CredentialsManager() {}

    public static String user(String alias) {
        return require(alias + ".user");
    }

    public static String pass(String alias) {
        return CryptoUtils.decrypt(require(alias + ".pass"));
    }

    private static String require(String key) {
        String v = PROPS.getProperty(key);
        if (v == null || v.isBlank())
            throw new FrameworkException("Thiếu credential '" + key + "' trong " + FILE);
        return v.trim();
    }
}
