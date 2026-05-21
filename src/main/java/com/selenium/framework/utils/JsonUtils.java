package com.selenium.framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selenium.framework.exceptions.FrameworkException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JsonUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonUtils() {}

  public static <T> T read(String filePath, Class<T> clazz) {
    try {
      return MAPPER.readValue(new File(filePath), clazz);
    } catch (IOException e) {
      throw new FrameworkException("Lỗi đọc JSON: " + filePath, e);
    }
  }

  public static List<Map<String, Object>> readList(String filePath) {
    try {
      return MAPPER.readValue(new File(filePath), new TypeReference<>() {});
    } catch (IOException e) {
      throw new FrameworkException("Lỗi đọc JSON list: " + filePath, e);
    }
  }
}
