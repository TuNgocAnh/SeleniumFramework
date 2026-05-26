# Quy Tắc Chung cho QA Automation

> Áp dụng cho mọi tác vụ automation testing, bất kể framework (Playwright, Selenium, Appium).

## 1. Kiến Trúc & Framework

- Bắt buộc sử dụng mô hình **Page Object Model (POM)**.
- Phân tách rõ ràng:
  - **Page classes:** Khai báo locators + methods tương tác UI
  - **Test classes:** Chứa logic kiểm thử + assertions
  - **Test data:** Tách riêng khỏi code chức năng (JSON, DataProvider, Utils)
- Assertions chỉ đặt trong Test classes, KHÔNG đặt trong Page classes.

## 2. Sinh Dữ Liệu Test (Test Data)

- Tất cả trường yêu cầu unique (Email, Username, Mã KH...) **phải sinh động**, không hardcode.
- **BẮT BUỘC dùng `DataGenerator` utility** của framework — không tự viết logic random rời rạc:
  ```java
  // ✅ ĐÚNG
  String email = DataGenerator.generateEmail("login");
  String firstName = DataGenerator.generateFirstName("checkout");
  String zip = DataGenerator.generatePostalCode();

  // ❌ SAI — hardcoded
  String email = "test@email.com";
  String firstName = "John";
  ```
- Dữ liệu phải **traceable** — nhìn vào DB biết ngay test nào tạo ra:
  ```
  Format: auto_<testNameHint>_<timestamp>[_<random4>]
  Ví dụ:  auto_createCustomer_1748313600123@test.com
  ```
- Hỗ trợ chạy parallel: mỗi test method có data riêng biệt, không conflict.
- **Code review checklist:** grep `@test.com`, `@gmail.com`, `"John"`, `"Test User"` hardcoded → reject PR.

## 3. Chất Lượng Code

- Không logic trùng lặp — tạo helper methods cho các hành động lặp đi lặp lại.
- Code phải đơn giản, dễ đọc, dễ bảo trì.
- Trước khi deliver code:
  - Xóa toàn bộ `console.log`, `System.out.println`, `print()` sinh ra khi debug
  - Xóa code bị comment (`//`, `/* */`)
  - Xóa locator / biến không sử dụng (unused code)

## 4. Quản Lý File & Thư Mục

- KHÔNG tự động xóa file source khi chưa xác nhận với user.
- Kiểm tra cấu trúc thư mục hiện có trước khi tạo file mới — tránh duplicate.
- Đặt file đúng thư mục theo kiến trúc project (xem `plan/automation/0_project_architecture`).

## 5. Quy Tắc Đặt Tên

### Java

| Thành phần | Quy tắc | Ví dụ |
|---|---|---|
| Page class | PascalCase + hậu tố `Page` | `LoginPage.java`, `CartPage.java` |
| Test class | PascalCase + hậu tố `Test` | `LoginTest.java`, `CartTest.java` |
| Test method | Bắt đầu bằng `test` + mô tả hành vi | `testLoginWithValidCredentials()` |
| Locator biến | lowerCamelCase + hậu tố mô tả element | `loginButton`, `usernameInput` |
| Utils class | PascalCase + mô tả chức năng | `DataGenerator.java`, `WaitHelper.java` |

### TypeScript / Playwright

| Thành phần | Quy tắc | Ví dụ |
|---|---|---|
| Page class | PascalCase + hậu tố `Page` | `LoginPage.ts`, `CartPage.ts` |
| Test file | kebab-case + `.spec.ts` | `login.spec.ts`, `cart.spec.ts` |
| Test block | `test('mô tả hành vi')` | `test('đăng nhập thành công')` |
| Locator biến | lowerCamelCase hoặc readonly | `readonly loginButton` |
| Utils | PascalCase hoặc kebab-case | `DataGenerator.ts`, `data-generator.ts` |

## 6. Assertions (Kiểm Tra Kết Quả)

- Mỗi test case **BẮT BUỘC** có ít nhất 1 assertion ở cuối.
- Nên có assertion xen kẽ ở các bước quan trọng.
- Assert phải mô tả rõ expected behavior:
  ```java
  // Java/TestNG
  Assert.assertTrue(dashboardPage.isDisplayed(), "Dashboard phải hiển thị sau khi đăng nhập");
  ```
  ```typescript
  // Playwright
  await expect(page.getByText('Đăng nhập thành công')).toBeVisible();
  ```

### 6.1. Hard vs Soft Assertion — khi nào dùng cái nào?

| Tình huống | Loại | Lý do |
|---|---|---|
| Setup/precondition (page load, login, navigate) | **Hard** `Assert.assertX(...)` | Fail là test sau vô nghĩa → stop ngay |
| Verify nhiều field trên cùng 1 trạng thái (subtotal + tax + total) | **Soft** `Assertions.assertX(...)` | Gom info, fix 1 lần thay vì nhiều run |
| Verify URL chuyển trang sau action | **Hard** | Sai URL → các step sau sai page |
| Multi-element visibility check cuối test | **Soft** | Báo cáo đầy đủ trong 1 run |

### 6.2. Soft Assertion — Pattern bắt buộc (Java)

```java
Assertions.assertEquals(actual1, expected1, "msg1");
Assertions.assertEquals(actual2, expected2, "msg2");
Assertions.assertEquals(actual3, expected3, "msg3");
Assertions.assertAll();  // ← BẮT BUỘC ở cuối, raise mọi failure tích luỹ
```

**Quên `assertAll()` → test PASS sai dù có fail tích luỹ.**

Framework đã có safety net trong `BaseTest.tearDown()`:
```java
@AfterMethod(alwaysRun = true)
public void tearDown() {
  try { Assertions.assertAll(); }
  finally { DriverFactory.quitDriver(); }
}
```
→ Mọi test class extend `BaseTest` tự động được bảo vệ khỏi pitfall "quên assertAll".

## 7. Reporting — Allure Metadata (BẮT BUỘC)

Mọi test class + method **PHẢI** có annotation Allure để report nhóm đúng hierarchy. Test không có metadata sẽ thành **orphan** ngoài hierarchy → khó filter/review.

### 7.1. Class level

```java
@Epic("<Project name>")      // luôn cùng giá trị toàn project, vd "Saucedemo"
@Feature("<Module name>")    // tên module, vd "Login" / "Cart" / "Checkout"
public class LoginTests extends BaseTest { ... }
```

### 7.2. Method level

```java
@Test(...)
@Story("<scenario nhóm con>")        // vd "Happy login" / "Invalid credentials"
@Severity(SeverityLevel.BLOCKER)     // ánh xạ Priority test case → Severity
public void loginSuccess() { ... }
```

### 7.3. Mapping Priority ↔ Severity

| TC Priority | `SeverityLevel` | Khi dùng |
|---|---|---|
| P1 (smoke, blocker) | `BLOCKER` | Fail = không release |
| P1 critical path | `CRITICAL` | Fail = feature chính hỏng |
| P2 (regression) | `CRITICAL` / `NORMAL` | — |
| P3 (weekly) | `NORMAL` / `MINOR` | — |
| P4 (on-demand) | `MINOR` / `TRIVIAL` | — |

### 7.4. Code review checklist

- ❌ Test class thiếu `@Epic` + `@Feature` → reject
- ❌ Test method thiếu `@Severity` → warn (mặc định Allure = NORMAL, không filter chính xác)
- ❌ Tất cả test cùng `@Severity(NORMAL)` → không có priority → reject

## 8. Tính Độc Lập Của Test (Test Independence)

- Mỗi test case phải **độc lập** — không phụ thuộc kết quả test khác.
- Setup/teardown rõ ràng (`@BeforeMethod/@AfterMethod` hoặc `beforeEach/afterEach`).
- Không chia sẻ state giữa các test methods.