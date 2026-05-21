# Learning Notes — Selenium Framework

> Ghi chú tự học, tổng hợp các pattern và util quan trọng trong project.
> Không phải tài liệu chính thức — chỉ là cheat sheet cá nhân.

---

## 1. Page Object Model (POM)

Tách UI thành **class đại diện trang**: locator + action ở Page class, assertion ở Test class.

```
LoginPage.java     →  locator + method tương tác (login, getError...)
LoginTests.java    →  gọi method + Assert kết quả
```

Quy ước project: Page extend `BasePage`, Test extend `BaseTest`.

---

## 2. Fluent Page Object Pattern

Method trả về `this` → cho phép **chain method**.

```java
public LoginPage login(String user, String pass) {
    type(userInput, user);
    type(passInput, pass);
    click(loginBtn);
    return this;   // ← key
}
```

Dùng:
```java
new LoginPage().login(user, pass).getErrorMessage();
```

`this` = chính object đang gọi method. Trả `this` để chain tiếp method khác cùng page.

---

## 3. Chain of Pages

Khi action **chuyển trang** → return page MỚI (không phải `this`).

```java
public DashboardPage loginAsValid(String u, String p) {
    ...
    return new DashboardPage();   // type-safe — sau đó chỉ gọi được method của Dashboard
}
```

---

## 4. Khi nào chain thẳng vs gán biến

```java
// Chain — dùng 1 lần, không cần debug
new LoginPage().login(user, pass);

// Gán biến — cần dùng lại object nhiều lần
LoginPage login = new LoginPage().login(user, pass);
Assert.assertTrue(login.isErrorDisplayed());
String err = login.getErrorMessage();
```

---

## 5. TestNG Groups

```java
@Test(groups = {"smoke", "regression"})
```

| Group | Khi nào chạy |
|---|---|
| `smoke` | Mỗi PR — test cốt lõi, nhanh |
| `regression` | Nightly — full coverage |

Lọc chạy:
```powershell
mvn test "-Dgroups=smoke"
mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"
```

---

## 6. Hard Assert vs Soft Assert

### Hard (`org.testng.Assert`)
Fail → **dừng test ngay**. Các assert sau không chạy.
```java
Assert.assertTrue(condition, "message");
```

### Soft (`Assertions.soft()` — custom của project)
Fail → **ghi nhận**, không dừng. Cuối test phải gọi `assertAll()` để raise.
```java
Assertions.soft().assertTrue(cond1, "msg1");
Assertions.soft().assertTrue(cond2, "msg2");
Assertions.assertAll();   // ← BẮT BUỘC, thiếu là test PASS giả!
```

| Tình huống | Loại |
|---|---|
| Điều kiện tiên quyết (page load, login OK) | Hard |
| Verify nhiều thuộc tính độc lập | Soft |

**Cơ chế:** `Assertions` của project dùng `ThreadLocal<SoftAssert>` → mỗi thread test 1 instance riêng → parallel-safe.

File: [src/main/java/com/selenium/framework/utils/Assertions.java](../src/main/java/com/selenium/framework/utils/Assertions.java)

---

## 7. CredentialsManager

Đọc credential từ config thay vì hardcode.

```java
CredentialsManager.user("standard")   // → "standard_user"
CredentialsManager.pass("standard")   // → "secret_sauce"
```

`"standard"` là **alias** trong file properties. Hỗ trợ 3 format:
- Plaintext: `standard.pass=secret_sauce`
- Base64: `standard.pass=b64:c2VjcmV0X3NhdWNl`
- Encrypted: `standard.pass=enc:...`

---

## 8. Locator — thứ tự ưu tiên (Selenium)

1. `By.id("...")` — nhanh, unique nhất
2. `By.cssSelector("[data-testid='...']")` — test-only attribute
3. `By.name("...")`
4. `By.cssSelector(...)` — flexible
5. `By.xpath(...)` — last resort

**Cấm:**
- `Thread.sleep()` → dùng `WebDriverWait` + `ExpectedConditions`
- XPath tuyệt đối (`//div[3]/div[2]/...`)
- Class hash động (`css-1n2xyz`)

Chi tiết: [.claude/rules/selenium_rules.md](../.claude/rules/selenium_rules.md)

---

## 9. Wait Strategy

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("x")));
wait.until(ExpectedConditions.elementToBeClickable(By.id("x")));
wait.until(ExpectedConditions.urlContains("/dashboard"));
```

---

## 10. Test Data — Quy tắc unique + traceable

```
Format: [prefix]_[testName]_[timestamp]_[random]
Ví dụ:  auto_createCustomer_20260522_A3F2@test.com
```

Dùng UUID, Timestamp, Faker. Không hardcode email/username unique.

---

## 11. `@Step` của Allure

```java
@Step("Login với user: {0}")
public LoginPage login(String user, String pass) { ... }
```

→ Hiện thành step trong **Allure report**, `{0}` thay bằng giá trị tham số đầu tiên (user).

---

## 11b. Xem báo cáo (Reports)

### Quy trình chuẩn

```powershell
mvn clean test          # Chạy test
mvn allure:serve        # Generate Allure HTML + tự mở browser
```

### Vị trí từng loại report

| Loại | Đường dẫn | Khi nào tạo |
|---|---|---|
| **Allure raw data** | `target/allure-results/*.json` | Sau `mvn test` |
| **Allure HTML** | (server tạm `http://localhost:xxxxx`) | Khi chạy `mvn allure:serve` |
| **Extent** | `reports/ExtentReport_<timestamp>.html` | Tự tạo sau khi test xong |
| **Surefire** | `target/surefire-reports/index.html` | Sau `mvn test` |
| **Logs** | `logs/automation.log` | Trong khi test chạy |

### Lưu ý quan trọng

- **Không mở** `target/site/allure-maven-plugin/index.html` bằng `file://` → bị **CORS block**, hiện `Loading...` mãi và 404. Phải dùng `mvn allure:serve` để có web server.
- **Extent** có timestamp trong tên file → giữ nhiều lần chạy. Folder `reports/` ở root, không bị `mvn clean` xóa.
- **Allure + Surefire** lưu trong `target/` → **mất** khi `mvn clean`.
- **Surefire** ghi đè mỗi lần chạy, không lưu history.

### Cách mở file Extent HTML

- **Reveal in File Explorer** (Shift+Alt+R) → double-click file
- Hoặc terminal: `start reports\ExtentReport_xxx.html` (mở bằng browser default)
- Extension VS Code "open in browser" thường bug với Chrome per-user → dùng `start` hoặc set Edge làm default cho gọn.

Chạy report:
```powershell
mvn allure:serve
```
