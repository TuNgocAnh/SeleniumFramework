# 🎯 Automation Demo — Saucedemo P1 Critical Subset

> **Input:** [testcases_saucedemo.md](testcases_saucedemo.md)
> **Scope:** 8 TC P1 Critical đại diện 7 module
> **Stack:** Java 21 + Maven + TestNG + Selenium (đã có sẵn trong project)
> **Run mode:** Headless Chrome 1920×1080
> **Date:** 2026-05-27

## Tiến độ

- [x] Bước 1: Phân tích test cases (chọn 8 TC từ 72)
- [x] Bước 2: Khảo sát UI (reuse từ `locators_saucedemo.md` — đã verified trước đó)
- [x] Bước 3: Thiết kế POM — extend `ProductsPage`, tạo 4 page mới
- [x] Bước 4: Test data — reuse `CredentialsManager`, hardcoded John/Doe cho checkout demo
- [x] Bước 5: Sinh automation scripts
- [x] Bước 6: Chạy test — PASS lần đầu, không cần auto-heal
- [x] Bước 7: Verify stability — PASS 2/2 lần liên tiếp

## Files Created / Modified

### Page Objects (5 file mới + 1 sửa)

| File | Status |
|---|---|
| [ProductsPage.java](../../../../src/main/java/com/selenium/framework/pages/ProductsPage.java) | 📝 Extended — thêm `addToCart(slug)`, `openCart()` |
| [CartPage.java](../../../../src/main/java/com/selenium/framework/pages/CartPage.java) | ✨ New |
| [CheckoutStepOnePage.java](../../../../src/main/java/com/selenium/framework/pages/CheckoutStepOnePage.java) | ✨ New |
| [CheckoutStepTwoPage.java](../../../../src/main/java/com/selenium/framework/pages/CheckoutStepTwoPage.java) | ✨ New |
| [CheckoutCompletePage.java](../../../../src/main/java/com/selenium/framework/pages/CheckoutCompletePage.java) | ✨ New |
| [LoginPage.java](../../../../src/main/java/com/selenium/framework/pages/LoginPage.java) | ♻️ Reused (đã có sẵn) |

### Test Classes (3 file mới + 1 sửa)

| File | Status |
|---|---|
| [LoginTests.java](../../../../src/test/java/com/selenium/tests/LoginTests.java) | 📝 Added `loginEmptyUsername` (TC_03) |
| [ProductsTests.java](../../../../src/test/java/com/selenium/tests/ProductsTests.java) | ♻️ Reused — đã cover INV_02 |
| [CartTests.java](../../../../src/test/java/com/selenium/tests/CartTests.java) | ✨ New — `checkoutFromCartWithItem` |
| [CheckoutTests.java](../../../../src/test/java/com/selenium/tests/CheckoutTests.java) | ✨ New — `firstNameRequiredAtCheckoutStepOne` |
| [E2ETests.java](../../../../src/test/java/com/selenium/tests/E2ETests.java) | ✨ New — `fullPurchaseHappyPath` |

## Kết Quả

| TC ID | Test Method | Run 1 | Run 2 | Stability |
|---|---|---|---|---|
| SAUCE_LOGIN_TC_01 | `LoginTests.loginSuccess` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_LOGIN_TC_02 | `LoginTests.loginLockedUser` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_LOGIN_TC_03 | `LoginTests.loginEmptyUsername` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_LOGIN_TC_06 | `LoginTests.loginWrongPassword` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_INV_TC_02 | `ProductsTests.addItemToCart` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_CART_TC_05 | `CartTests.checkoutFromCartWithItem` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_CHK1_TC_02 | `CheckoutTests.firstNameRequiredAtCheckoutStepOne` | ✅ PASS | ✅ PASS | 2/2 stable |
| SAUCE_E2E_TC_01 | `E2ETests.fullPurchaseHappyPath` | ✅ PASS | ✅ PASS | 2/2 stable |

**Tổng:** 8 PASS / 0 FAIL / 0 SKIP — **24-25s mỗi lần chạy**

## Locator Collection (đã dùng — verified từ DOM thật)

| Page | Element | Primary Locator |
|---|---|---|
| LoginPage | username input | `By.id("user-name")` |
| LoginPage | password input | `By.id("password")` |
| LoginPage | Login button | `By.id("login-button")` |
| LoginPage | Error banner | `By.cssSelector("h3[data-test='error']")` |
| ProductsPage | Page title | `By.cssSelector(".title")` |
| ProductsPage | Cart badge | `By.cssSelector(".shopping_cart_badge")` |
| ProductsPage | Cart icon | `By.cssSelector("[data-test='shopping-cart-link']")` |
| ProductsPage | Add Backpack | `By.id("add-to-cart-sauce-labs-backpack")` |
| CartPage | Title | `By.cssSelector("[data-test='title']")` |
| CartPage | Item name | `By.cssSelector("[data-test='inventory-item-name']")` |
| CartPage | Checkout button | `By.cssSelector("[data-test='checkout']")` |
| CheckoutStepOne | First Name | `By.cssSelector("[data-test='firstName']")` |
| CheckoutStepOne | Last Name | `By.cssSelector("[data-test='lastName']")` |
| CheckoutStepOne | Zip | `By.cssSelector("[data-test='postalCode']")` |
| CheckoutStepOne | Continue | `By.cssSelector("[data-test='continue']")` |
| CheckoutStepOne | Error | `By.cssSelector("h3[data-test='error']")` |
| CheckoutStepTwo | Subtotal | `By.cssSelector("[data-test='subtotal-label']")` |
| CheckoutStepTwo | Tax | `By.cssSelector("[data-test='tax-label']")` |
| CheckoutStepTwo | Total | `By.cssSelector("[data-test='total-label']")` |
| CheckoutStepTwo | Finish | `By.cssSelector("[data-test='finish']")` |
| CheckoutComplete | Header | `By.cssSelector("[data-test='complete-header']")` |

## Quy ước & Quality Checklist

- [x] **POM tuân thủ** — locator trong Page class, assertion trong Test class
- [x] **Method chaining** — Page method return `this` hoặc next Page (vd `cart.checkout()` → `CheckoutStepOnePage`)
- [x] **Smart waits** — không có `Thread.sleep` nào; dùng `WaitUtils.waitForVisible/Clickable/UrlContains`
- [x] **Test independent** — mỗi `@Test` có `@BeforeMethod` init driver mới + `@AfterMethod` quit
- [x] **Allure `@Step`** — mọi action method có annotation `@Step` để report đẹp
- [x] **Assertion có message** — message rõ ràng (vd: `"Tax phải đúng 8% giá trị"`)
- [x] **No hardcoded credentials trong test** — dùng `CredentialsManager.user("standard")`
- [x] **Cleanup** — không còn `System.out.println`, commented code, unused import

## Cách chạy

```powershell
# Chạy 8 demo TCs (headless)
mvn test "-Dtest=LoginTests,ProductsTests,CartTests,CheckoutTests,E2ETests" "-Dheadless=true"

# Chạy headed mode (xem browser)
mvn test "-Dtest=LoginTests,ProductsTests,CartTests,CheckoutTests,E2ETests"

# Chỉ chạy 1 TC cụ thể
mvn test "-Dtest=E2ETests#fullPurchaseHappyPath"

# Chạy smoke group (đã đánh tag `smoke` cho tất cả demo TCs)
mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

# Xem Allure report
mvn allure:serve
```

## Known issues / Limitations

| # | Vấn đề | Tác động |
|---|---|---|
| 1 | Selenium CDP warning v148 không match | Cosmetic — không ảnh hưởng test, chỉ là log warning |
| 2 | TC_03 (empty username) — `type()` gọi `clear()` + `sendKeys("")` | Hoạt động đúng do field rỗng sẵn sau navigate |
| 3 | E2E test data hardcoded `John/Doe/10000` | Demo OK — production nên dùng `DataGenerator` để traceable |

## Bước tiếp theo gợi ý

- Mở rộng sang full P1 (27 TC): các TC còn lại — `loginBothEmpty`, `inventoryShows6Products`, `sortPriceLowToHigh`, `logout`, `taxCalculation3Products`, `checkoutCompleteCartCleared`...
- Coverage P2 + P3 → ~64 TC tổng
- Convert E2E test sang data-driven dùng `JsonUtils` với nhiều bộ data
- Bật visual regression test cho user `visual_user` (TC P4 — cần baseline screenshot)
