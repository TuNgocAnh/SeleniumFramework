package com.selenium.framework.utils;

import com.selenium.framework.exceptions.FrameworkException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256/GCM. Khoá lấy từ env CRED_KEY (hoặc system property cred.key). Nếu không có khoá,
 * fallback sang Base64 (chỉ obfuscation — không bảo mật thật). Định dạng chuỗi đã mã hoá:
 * "enc:<base64(iv||ciphertext)>" hoặc "b64:<base64>".
 */
public final class CryptoUtils {

  private static final String AES = "AES/GCM/NoPadding";
  private static final int IV_LEN = 12;
  private static final int TAG_BITS = 128;
  private static final String ENC_PREFIX = "enc:";
  private static final String B64_PREFIX = "b64:";

  private CryptoUtils() {}

  public static String encrypt(String plain) {
    byte[] key = readKey();
    if (key == null)
      return B64_PREFIX
          + Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    try {
      byte[] iv = new byte[IV_LEN];
      new SecureRandom().nextBytes(iv);
      Cipher c = Cipher.getInstance(AES);
      c.init(
          Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_BITS, iv));
      byte[] ct = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
      byte[] out = new byte[iv.length + ct.length];
      System.arraycopy(iv, 0, out, 0, iv.length);
      System.arraycopy(ct, 0, out, iv.length, ct.length);
      return ENC_PREFIX + Base64.getEncoder().encodeToString(out);
    } catch (Exception e) {
      throw new FrameworkException("Encrypt thất bại", e);
    }
  }

  public static String decrypt(String value) {
    if (value == null) return null;
    if (value.startsWith(B64_PREFIX)) {
      return new String(
          Base64.getDecoder().decode(value.substring(B64_PREFIX.length())), StandardCharsets.UTF_8);
    }
    if (!value.startsWith(ENC_PREFIX)) return value;
    byte[] key = readKey();
    if (key == null) throw new FrameworkException("Giá trị đã mã hoá nhưng thiếu CRED_KEY");
    try {
      byte[] raw = Base64.getDecoder().decode(value.substring(ENC_PREFIX.length()));
      byte[] iv = new byte[IV_LEN];
      byte[] ct = new byte[raw.length - IV_LEN];
      System.arraycopy(raw, 0, iv, 0, IV_LEN);
      System.arraycopy(raw, IV_LEN, ct, 0, ct.length);
      Cipher c = Cipher.getInstance(AES);
      c.init(
          Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_BITS, iv));
      return new String(c.doFinal(ct), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new FrameworkException("Decrypt thất bại — sai CRED_KEY?", e);
    }
  }

  private static byte[] readKey() {
    String k = System.getProperty("cred.key", System.getenv("CRED_KEY"));
    if (k == null || k.isBlank()) return null;
    try {
      return MessageDigest.getInstance("SHA-256").digest(k.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new FrameworkException("SHA-256 không khả dụng", e);
    }
  }

  /** Tiện ích CLI: java ... CryptoUtils encrypt <plain> | decrypt <cipher> */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: encrypt <text> | decrypt <text>");
      return;
    }
    switch (args[0]) {
      case "encrypt" -> System.out.println(encrypt(args[1]));
      case "decrypt" -> System.out.println(decrypt(args[1]));
      default -> System.out.println("Unknown command: " + args[0]);
    }
  }
}
