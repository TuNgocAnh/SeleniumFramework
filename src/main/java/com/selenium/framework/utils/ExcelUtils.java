package com.selenium.framework.utils;

import com.selenium.framework.exceptions.FrameworkException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelUtils {

  private ExcelUtils() {}

  /** Đọc 1 sheet → list of map (header -> value). Hỗ trợ data-driven cho TestNG. */
  public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
    List<Map<String, String>> rows = new ArrayList<>();
    DataFormatter fmt = new DataFormatter();

    try (FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook wb = new XSSFWorkbook(fis)) {

      Sheet sheet = wb.getSheet(sheetName);
      if (sheet == null) throw new FrameworkException("Không tìm thấy sheet: " + sheetName);

      Row header = sheet.getRow(0);
      if (header == null) return rows;

      int colCount = header.getLastCellNum();
      List<String> headers = new ArrayList<>();
      for (int c = 0; c < colCount; c++) {
        headers.add(fmt.formatCellValue(header.getCell(c)));
      }

      for (int r = 1; r <= sheet.getLastRowNum(); r++) {
        Row row = sheet.getRow(r);
        if (row == null) continue;
        Map<String, String> map = new LinkedHashMap<>();
        for (int c = 0; c < colCount; c++) {
          Cell cell = row.getCell(c);
          map.put(headers.get(c), fmt.formatCellValue(cell));
        }
        rows.add(map);
      }
    } catch (IOException e) {
      throw new FrameworkException("Lỗi đọc Excel: " + filePath, e);
    }
    return rows;
  }

  /** Convert sang Object[][] để dùng cho @DataProvider. */
  public static Object[][] toDataProvider(String filePath, String sheetName) {
    List<Map<String, String>> rows = readSheet(filePath, sheetName);
    Object[][] data = new Object[rows.size()][1];
    for (int i = 0; i < rows.size(); i++) data[i][0] = rows.get(i);
    return data;
  }
}
