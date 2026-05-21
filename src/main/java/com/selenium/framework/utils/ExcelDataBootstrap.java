package com.selenium.framework.utils;

import com.selenium.framework.config.FrameworkConstants;
import com.selenium.framework.exceptions.FrameworkException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Tự sinh file Excel mẫu nếu chưa tồn tại — để demo data-driven mà không cần commit binary. */
public final class ExcelDataBootstrap {

  private ExcelDataBootstrap() {}

  public static synchronized String ensureLoginSample() {
    String path = FrameworkConstants.TESTDATA_DIR + "login_data.xlsx";
    File f = new File(path);
    if (f.exists()) return path;
    f.getParentFile().mkdirs();

    try (XSSFWorkbook wb = new XSSFWorkbook();
        FileOutputStream fos = new FileOutputStream(f)) {
      Sheet sheet = wb.createSheet("login");
      String[] headers = {"username", "password", "expectSuccess", "expectedErrorContains"};
      Row header = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

      Object[][] data = {
        {"standard_user", "secret_sauce", "true", ""},
        {"locked_out_user", "secret_sauce", "false", "locked out"},
        {"standard_user", "wrong_password", "false", "Username and password do not match"}
      };
      for (int r = 0; r < data.length; r++) {
        Row row = sheet.createRow(r + 1);
        for (int c = 0; c < data[r].length; c++) {
          row.createCell(c).setCellValue(String.valueOf(data[r][c]));
        }
      }
      wb.write(fos);
    } catch (IOException e) {
      throw new FrameworkException("Không sinh được Excel mẫu: " + path, e);
    }
    return path;
  }
}
