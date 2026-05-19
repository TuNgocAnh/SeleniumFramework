# Selenium Test Framework

> 🌐 **English version:** [README.en.md](README.en.md) — _Khi sửa file này, vui lòng update cả `README.en.md` để giữ đồng bộ._

Framework tự động kiểm thử web viết bằng **Java 21 + Maven + TestNG**, áp dụng mô hình **Page Object (POM)**. Hỗ trợ chạy đa trình duyệt, song song, data-driven (JSON/Excel), Selenium Grid, mobile emulation, báo cáo Extent + Allure, log file và mã hoá thông tin đăng nhập.

---

## 1. Yêu cầu môi trường

| Thành phần | Phiên bản |
|------------|-----------|
| JDK        | 21 |
| Maven      | 3.9+ |
| Trình duyệt | Chrome / Edge / Firefox (cài sẵn) |
| Allure CLI | (tuỳ chọn — để xem report Allure dạng web) |

Kiểm tra nhanh:
```powershell
java -version
mvn -version
```

---

## 2. Cấu trúc thư mục

```
SeleniumFramework/
├─ src/
│  ├─ main/java/com/selenium/framework/
│  │  ├─ config/        # ConfigReader, CredentialsManager, FrameworkConstants
│  │  ├─ driver/        # DriverFactory (Chrome/Edge/Firefox, Grid, mobile)
│  │  ├─ pages/         # BasePage + các Page Object (LoginPage, ProductsPage…)
│  │  ├─ listeners/     # TestListener, RetryAnalyzer, RetryListener
│  │  ├─ reports/       # ExtentManager, ExtentTestManager, ReportRetention
│  │  ├─ utils/         # Wait/Screenshot/Excel/Json/Crypto/Assertions
│  │  └─ exceptions/
│  └─ test/
│     ├─ java/com/selenium/tests/   # Test class (BaseTest, LoginTests, …)
│     └─ resources/
│        ├─ config/      # config.properties, dev/stg/prod, credentials, log4j2.xml
│        └─ testdata/    # login_data.json (+ Excel auto-gen). Có thể tách theo env: testdata/<env>/
├─ testng.xml             # suite mặc định (smoke + regression)
├─ testng-smoke.xml       # chỉ smoke
├─ testng-parallel.xml    # chạy song song
└─ pom.xml
```

---

## 3. Chạy test nhanh

```powershell
# Chạy suite mặc định (testng.xml)
mvn clean test

# Chỉ chạy nhóm smoke
mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

# Chạy song song
mvn test "-Dsurefire.suiteXmlFile=testng-parallel.xml"
```

### Truyền tham số khi chạy

```powershell
# Đổi trình duyệt + môi trường + headless
mvn test "-Dbrowser=edge" "-Denv=stg" "-Dheadless=true"

# Chạy qua Selenium Grid
mvn test "-DgridUrl=http://localhost:4444/wd/hub"

# Pin version trình duyệt (WebDriverManager sẽ tải đúng version)
mvn test "-DchromeVersion=120"

# Chrome mobile emulation
mvn test "-DmobileEmulation=true" "-DmobileDevice=Pixel 7"
```

### Nhóm test (TestNG groups)

| Group       | Mục đích                              |
|-------------|---------------------------------------|
| `smoke`     | Bộ test nhanh, chạy mỗi PR            |
| `regression`| Đầy đủ, chạy nightly                  |

---

## 4. Cấu hình (`src/test/resources/config/config.properties`)

| Key | Mặc định | Mô tả |
|-----|----------|-------|
| `browser` | chrome | chrome / edge / firefox |
| `headless` | false | bật chế độ ẩn |
| `baseUrl` | — | URL gốc của ứng dụng |
| `explicitWait` | 15 | giây — chờ phần tử |
| `pageLoadWait` | 30 | giây — chờ load trang |
| `scriptTimeout` | 30 | giây — chờ JS |
| `retryCount` | 2 | số lần retry khi test fail |
| `reportRetentionDays` | 7 | số ngày giữ report cũ |
| `gridUrl` | — | URL Selenium Grid (trống = chạy local) |
| `chromeVersion` / `edgeVersion` / `firefoxVersion` | — | pin version trình duyệt |
| `mobileEmulation` | false | bật mobile emulation (Chrome) |
| `mobileDevice` | Pixel 7 | tên thiết bị giả lập |

> Mọi key đều có thể override bằng `-Dkey=value` khi chạy `mvn`.

File theo môi trường: `dev.properties`, `stg.properties`, `prod.properties` — chọn bằng `-Denv=stg`.

---

## 5. Quản lý mật khẩu (Credentials)

File `src/test/resources/config/credentials.properties` lưu user/pass theo alias. Password có 3 dạng:

| Dạng | Ví dụ | Khi dùng |
|------|-------|----------|
| Plaintext | `secret123` | chỉ DEV |
| Base64 obfuscation | `b64:c2VjcmV0MTIz` | tránh để lộ trực tiếp |
| AES-256/GCM | `enc:...` | production — cần biến môi trường `CRED_KEY` |

Sinh chuỗi mã hoá:
```powershell
mvn -q compile
$env:CRED_KEY="your-secret-key"
java -cp target/classes com.selenium.framework.utils.CryptoUtils encrypt "password"
```

---

## 6. Báo cáo & Log

- **ExtentReport** — `reports/ExtentReport_<timestamp>.html` (mở trực tiếp bằng trình duyệt)
- **Allure** — kết quả ở `target/allure-results/`, xem dạng web:
  ```powershell
  mvn allure:serve
  ```
- **Screenshot** khi test fail được đính kèm trong cả Extent lẫn Allure
- **Log** — `logs/automation.log` (mặc định INFO; thao tác click/type ở level DEBUG)
- Report cũ hơn `reportRetentionDays` (mặc định 7 ngày) sẽ tự xoá đầu mỗi suite

---

## 7. Self-Healing Locator

Khi locator chính fail (vd: FE đổi `id`, đổi class), framework sẽ tự thử các **fallback strategy** dựa trên đặc điểm khác của element (text, role + aria-label, attribute fuzzy match...). Nếu một strategy tìm được **đúng 1 ứng viên**, element được dùng tạm để test PASS, đồng thời log + đính kèm Allure cảnh báo để dev cập nhật code.

```java
HealableElement loginBtn = HealableElement.builder()
    .primary(By.id("login-button"))
    .fallback(ByTextStrategy.of("Login"))
    .fallback(ByRoleStrategy.of("button", "Login"))
    .fallback(ByAttributeContainsStrategy.of("class", "submit"))
    .build();

loginBtn.findWithWait().click();  // dùng cho hầu hết trường hợp
```

### Strategies

| Strategy | Khi dùng |
|----------|----------|
| `ByTextStrategy` | Element có visible text ổn định |
| `ByRoleStrategy` | Button/link/textbox có `aria-label` hoặc accessible name ổn định |
| `ByAttributeContainsStrategy` | Last resort — fuzzy match qua một phần của attribute (vd `class*='submit'`) |

### Hai chế độ tìm element

| Method | Hành vi | Khi dùng |
|--------|---------|----------|
| `find()` | Thử primary + fallback **1 lần ngay** (~40ms), fail là throw | DOM chắc chắn đã render (kiểm tra ngay tại chỗ) |
| `findWithWait(int seconds)` | Poll mỗi 500ms, hết timeout mới throw | DOM đang render / sau khi click navigate |
| `findWithWait()` | Như trên, dùng `explicitWait` từ config (mặc định 15s) | **Default cho 90% trường hợp** |

> **Triết lý:**
> - Healing chỉ kick-in khi fallback tìm thấy **chính xác 1** element → tránh click nhầm.
> - Healing **không thay thế** explicit wait — nó chạy bên trong wait, mỗi tick poll thử cả primary + fallback. Element xuất hiện ở tick nào sẽ return tick đó, không lãng phí thời gian.
> - Mỗi heal event đều được log WARNING + đính kèm Allure để dev không bỏ sót việc fix locator gốc.
>
> Xem demo tại `src/test/java/com/selenium/tests/HealingDemoTests.java`.

---

## 8. Soft Assertion

Dùng khi muốn check nhiều thứ trong một test mà không fail ngay ở assertion đầu:

```java
Assertions.soft().assertEquals(actual, expected, "msg");
Assertions.soft().assertTrue(condition);
Assertions.assertAll(); // gọi cuối test để gom kết quả
```

---

## 9. CI (GitHub Actions)

Workflow: `.github/workflows/ci.yml` — tự chạy headless khi `push` / `pull_request`, upload Allure + Extent + log làm artifact.

> Nếu dùng password dạng `enc:`, thêm secret `CRED_KEY` trong repo settings.

---

## 10. Mẹo nhanh khi viết test mới

1. Tạo Page Object trong `src/main/java/com/selenium/framework/pages/`, kế thừa `BasePage`.
2. Tạo test class trong `src/test/java/com/selenium/tests/`, kế thừa `BaseTest`.
3. Gắn `@Test(groups = {"smoke"})` hoặc `"regression"` để chọn nhóm.
4. Data-driven: đặt file JSON/Excel trong `src/test/resources/testdata/`, đọc bằng `JsonUtils` / `ExcelUtils`.
5. Chạy local: `mvn test -Dheadless=false` để xem trình duyệt mở thật.
