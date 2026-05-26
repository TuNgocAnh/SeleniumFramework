# 🎯 Locator Catalogue — saucedemo.com (Selenium Java)

> **Verified bằng:** Playwright MCP + `browser_evaluate` query `[data-test], [id], [name]` trên DOM thật, viewport 1920×1080.
> **Locator priority dùng cho site này:** `data-test` (toàn site có) > `id` (login/checkout có) > CSS attribute > text/role fallback.
> **Khuyến nghị primary:** `By.cssSelector("[data-test='...']")` — duy nhất, semantic, ổn định qua reload và DOM changes.

---

## 📌 Quy ước

- **Primary:** `By` dùng trong `private final By xxx = ...` của Page class.
- **Healable:** dùng cho element **dễ break** (button thao tác, link nav, field input) — cấu hình `HealableElement.builder()` để self-heal khi DOM thay đổi.
- **Lý do chọn data-test ưu tiên hơn id:** mặc dù element login có cả `id="user-name"` lẫn `data-test="username"`, site dùng `data-test` xuyên suốt (cả product card, footer, error) → đồng nhất + intent là cho automation.

---

## 1. LoginPage (`/`)

| # | Element | Primary | Healable fallback |
|---|---|---|---|
| 1 | Username input | `By.cssSelector("[data-test='username']")` | text(Username placeholder), attribute(`id`, `user-name`) |
| 2 | Password input | `By.cssSelector("[data-test='password']")` | attribute(`id`, `password`) |
| 3 | Login button | `By.cssSelector("[data-test='login-button']")` | role(button, Login), text(Login) |
| 4 | Error message | `By.cssSelector("[data-test='error']")` | text("Epic sadface") — partial |
| 5 | Credentials info box | `By.cssSelector("[data-test='login-credentials']")` | — read-only static |

```java
// LoginPage.java — locators
private final By usernameInput  = By.cssSelector("[data-test='username']");
private final By passwordInput  = By.cssSelector("[data-test='password']");
private final By loginButton    = By.cssSelector("[data-test='login-button']");
private final By errorMessage   = By.cssSelector("[data-test='error']");

// Healable versions
private final HealableElement loginButtonHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='login-button']"))
    .fallback(ByRoleStrategy.of("button", "Login"))
    .fallback(ByAttributeContainsStrategy.of("id", "login-button"))
    .build();

private final HealableElement usernameInputHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='username']"))
    .fallback(ByAttributeContainsStrategy.of("name", "user-name"))
    .fallback(ByAttributeContainsStrategy.of("placeholder", "Username"))
    .build();

private final HealableElement passwordInputHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='password']"))
    .fallback(ByAttributeContainsStrategy.of("name", "password"))
    .fallback(ByAttributeContainsStrategy.of("placeholder", "Password"))
    .build();
```

---

## 2. Header & Sidebar Menu (xuất hiện trên tất cả trang sau login)

| # | Element | Primary | Fallback |
|---|---|---|---|
| 1 | Hamburger (Open Menu) | `By.id("react-burger-menu-btn")` | text("Open Menu"), css(`button[aria-label='Open Menu']`) |
| 2 | Close menu (X) | `By.id("react-burger-cross-btn")` | text("Close Menu") |
| 3 | All Items link | `By.cssSelector("[data-test='inventory-sidebar-link']")` | text("All Items") |
| 4 | About link | `By.cssSelector("[data-test='about-sidebar-link']")` | text("About") |
| 5 | Logout link | `By.cssSelector("[data-test='logout-sidebar-link']")` | text("Logout") |
| 6 | Reset App State | `By.cssSelector("[data-test='reset-sidebar-link']")` | text("Reset App State") |
| 7 | Cart icon | `By.cssSelector("[data-test='shopping-cart-link']")` | css(`a.shopping_cart_link`) |
| 8 | Cart badge (count) | `By.cssSelector("[data-test='shopping-cart-badge']")` | css(`span.shopping_cart_badge`) — chỉ tồn tại khi cart > 0 |
| 9 | Page title (secondary header) | `By.cssSelector("[data-test='title']")` | css(`.title`) |

```java
// HeaderComponent.java (hoặc khai báo trong BasePage)
private final By menuButton      = By.id("react-burger-menu-btn");
private final By menuClose       = By.id("react-burger-cross-btn");
private final By allItemsLink    = By.cssSelector("[data-test='inventory-sidebar-link']");
private final By aboutLink       = By.cssSelector("[data-test='about-sidebar-link']");
private final By logoutLink      = By.cssSelector("[data-test='logout-sidebar-link']");
private final By resetAppLink    = By.cssSelector("[data-test='reset-sidebar-link']");
private final By cartIcon        = By.cssSelector("[data-test='shopping-cart-link']");
private final By cartBadge       = By.cssSelector("[data-test='shopping-cart-badge']");
private final By pageTitle       = By.cssSelector("[data-test='title']");

private final HealableElement logoutLinkHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='logout-sidebar-link']"))
    .fallback(ByTextStrategy.of("Logout"))
    .fallback(ByAttributeContainsStrategy.of("id", "logout_sidebar_link"))
    .build();

private final HealableElement cartIconHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='shopping-cart-link']"))
    .fallback(ByAttributeContainsStrategy.of("class", "shopping_cart_link"))
    .build();
```

---

## 3. InventoryPage (`/inventory.html`)

### 3.1. Page-level

| # | Element | Primary | Fallback |
|---|---|---|---|
| 1 | Sort dropdown | `By.cssSelector("[data-test='product-sort-container']")` | css(`select.product_sort_container`) |
| 2 | Sort active option label | `By.cssSelector("[data-test='active-option']")` | — |
| 3 | Inventory container | `By.id("inventory_container")` | css(`[data-test='inventory-container']`) |
| 4 | Inventory list (wrapper) | `By.cssSelector("[data-test='inventory-list']")` | — |
| 5 | Inventory item (1 trong N) | `By.cssSelector("[data-test='inventory-item']")` | css(`div.inventory_item`) |

### 3.2. Item-scoped (template — thay `{slug}` với product slug)

> **Slug pattern:** lowercase, dấu cách → `-`. Ví dụ: `Sauce Labs Backpack` → `sauce-labs-backpack`.
> Đặc biệt: `Test.allTheThings() T-Shirt (Red)` → `test.allthethings()-t-shirt-(red)` — dấu chấm + ngoặc giữ nguyên.

| # | Element | Primary |
|---|---|---|
| 1 | Item name (text) | `By.cssSelector("[data-test='inventory-item-name']")` |
| 2 | Item description | `By.cssSelector("[data-test='inventory-item-desc']")` |
| 3 | Item price | `By.cssSelector("[data-test='inventory-item-price']")` |
| 4 | Item image link | `By.cssSelector("[data-test='item-{n}-img-link']")` (n = 0..5) |
| 5 | Item title link | `By.cssSelector("[data-test='item-{n}-title-link']")` |
| 6 | Add to cart button | `By.cssSelector("[data-test='add-to-cart-{slug}']")` |
| 7 | Remove button | `By.cssSelector("[data-test='remove-{slug}']")` |

```java
// InventoryPage.java
private final By sortDropdown    = By.cssSelector("[data-test='product-sort-container']");
private final By activeSortLabel = By.cssSelector("[data-test='active-option']");
private final By inventoryList   = By.cssSelector("[data-test='inventory-list']");
private final By allItems        = By.cssSelector("[data-test='inventory-item']");
private final By allItemNames    = By.cssSelector("[data-test='inventory-item-name']");
private final By allItemPrices   = By.cssSelector("[data-test='inventory-item-price']");

// Item buttons — dynamic theo slug
public By addToCartBtn(String slug)  { return By.cssSelector("[data-test='add-to-cart-" + slug + "']"); }
public By removeBtn(String slug)     { return By.cssSelector("[data-test='remove-" + slug + "']"); }

// Healable cho Add to cart Backpack (ví dụ — element user dùng thường xuyên)
private final HealableElement addBackpackHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']"))
    .fallback(ByAttributeContainsStrategy.of("id", "add-to-cart-sauce-labs-backpack"))
    // Fallback cuối: tìm button "Add to cart" gần text "Sauce Labs Backpack" — phức tạp,
    // không nên dùng làm fallback tự động. Nên catch + báo user thay vì auto-heal.
    .build();
```

### 3.3. Bảng slug → tên đầy đủ

| Product | Slug |
|---|---|
| Sauce Labs Backpack | `sauce-labs-backpack` |
| Sauce Labs Bike Light | `sauce-labs-bike-light` |
| Sauce Labs Bolt T-Shirt | `sauce-labs-bolt-t-shirt` |
| Sauce Labs Fleece Jacket | `sauce-labs-fleece-jacket` |
| Sauce Labs Onesie | `sauce-labs-onesie` |
| Test.allTheThings() T-Shirt (Red) | `test.allthethings()-t-shirt-(red)` |

⚠️ **Cảnh báo:** slug "Test.allTheThings()..." chứa dấu `.` `(` `)` — escape khi dùng trong selector phức tạp, nhưng `[data-test='...']` thì chấp nhận as-is.

---

## 4. CartPage (`/cart.html`)

| # | Element | Primary | Fallback |
|---|---|---|---|
| 1 | Cart contents container | `By.id("cart_contents_container")` | css(`[data-test='cart-contents-container']`) |
| 2 | Cart list | `By.cssSelector("[data-test='cart-list']")` | — |
| 3 | QTY label | `By.cssSelector("[data-test='cart-quantity-label']")` | text("QTY") |
| 4 | Description label | `By.cssSelector("[data-test='cart-desc-label']")` | text("Description") |
| 5 | Item quantity (per row) | `By.cssSelector("[data-test='item-quantity']")` | — |
| 6 | Continue Shopping | `By.id("continue-shopping")` | css(`[data-test='continue-shopping']`), text |
| 7 | Checkout | `By.id("checkout")` | css(`[data-test='checkout']`), role(button, Checkout) |

```java
// CartPage.java
private final By cartList            = By.cssSelector("[data-test='cart-list']");
private final By itemQuantity        = By.cssSelector("[data-test='item-quantity']");
private final By continueShoppingBtn = By.cssSelector("[data-test='continue-shopping']");
private final By checkoutBtn         = By.cssSelector("[data-test='checkout']");

private final HealableElement checkoutBtnHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='checkout']"))
    .fallback(ByRoleStrategy.of("button", "Checkout"))
    .fallback(ByTextStrategy.of("Checkout"))
    .build();
```

---

## 5. CheckoutStepOnePage (`/checkout-step-one.html`)

| # | Element | Primary | Fallback |
|---|---|---|---|
| 1 | First Name | `By.cssSelector("[data-test='firstName']")` | id(`first-name`), name(`firstName`) |
| 2 | Last Name | `By.cssSelector("[data-test='lastName']")` | id(`last-name`), name(`lastName`) |
| 3 | Zip/Postal Code | `By.cssSelector("[data-test='postalCode']")` | id(`postal-code`) |
| 4 | Continue (input[type=submit]) | `By.cssSelector("[data-test='continue']")` | id(`continue`) |
| 5 | Cancel | `By.cssSelector("[data-test='cancel']")` | id(`cancel`), text("Cancel") |
| 6 | Error banner | `By.cssSelector("[data-test='error']")` | text("Error:") |

```java
// CheckoutStepOnePage.java
private final By firstNameInput  = By.cssSelector("[data-test='firstName']");
private final By lastNameInput   = By.cssSelector("[data-test='lastName']");
private final By postalCodeInput = By.cssSelector("[data-test='postalCode']");
private final By continueBtn     = By.cssSelector("[data-test='continue']");
private final By cancelBtn       = By.cssSelector("[data-test='cancel']");
private final By errorBanner     = By.cssSelector("[data-test='error']");

private final HealableElement continueBtnHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='continue']"))
    .fallback(ByAttributeContainsStrategy.of("id", "continue"))
    .fallback(ByAttributeContainsStrategy.of("value", "Continue"))
    .build();
```

> ⚠️ Note: `Continue` ở step 1 là `<input type="submit" value="Continue">` (không phải `<button>`) → `getByRole("button", "Continue")` của Playwright vẫn match nhờ implicit role, **nhưng** Selenium `ByRoleStrategy` của framework chỉ scan attribute `role`/`aria-label` → **không** dùng được role fallback cho element này. Dùng `ByAttributeContainsStrategy.of("value", "Continue")` thay thế.

---

## 6. CheckoutStepTwoPage (`/checkout-step-two.html`)

| # | Element | Primary |
|---|---|---|
| 1 | Summary container | `By.id("checkout_summary_container")` |
| 2 | Item quantity | `By.cssSelector("[data-test='item-quantity']")` |
| 3 | Item name (in summary) | `By.cssSelector("[data-test='inventory-item-name']")` |
| 4 | Item price (in summary) | `By.cssSelector("[data-test='inventory-item-price']")` |
| 5 | Payment info label | `By.cssSelector("[data-test='payment-info-label']")` |
| 6 | Payment info value | `By.cssSelector("[data-test='payment-info-value']")` |
| 7 | Shipping info label | `By.cssSelector("[data-test='shipping-info-label']")` |
| 8 | Shipping info value | `By.cssSelector("[data-test='shipping-info-value']")` |
| 9 | Total info label | `By.cssSelector("[data-test='total-info-label']")` |
| 10 | Subtotal (Item total: $X) | `By.cssSelector("[data-test='subtotal-label']")` |
| 11 | Tax (Tax: $Y) | `By.cssSelector("[data-test='tax-label']")` |
| 12 | Total (Total: $Z) | `By.cssSelector("[data-test='total-label']")` |
| 13 | Finish button | `By.cssSelector("[data-test='finish']")` |
| 14 | Cancel button | `By.cssSelector("[data-test='cancel']")` |

```java
// CheckoutStepTwoPage.java
private final By subtotalLabel       = By.cssSelector("[data-test='subtotal-label']");
private final By taxLabel            = By.cssSelector("[data-test='tax-label']");
private final By totalLabel          = By.cssSelector("[data-test='total-label']");
private final By paymentInfoValue    = By.cssSelector("[data-test='payment-info-value']");
private final By shippingInfoValue   = By.cssSelector("[data-test='shipping-info-value']");
private final By finishBtn           = By.cssSelector("[data-test='finish']");
private final By cancelBtn           = By.cssSelector("[data-test='cancel']");

private final HealableElement finishBtnHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='finish']"))
    .fallback(ByRoleStrategy.of("button", "Finish"))
    .fallback(ByTextStrategy.of("Finish"))
    .build();
```

---

## 7. CheckoutCompletePage (`/checkout-complete.html`)

| # | Element | Primary | Fallback |
|---|---|---|---|
| 1 | Complete container | `By.id("checkout_complete_container")` | css(`[data-test='checkout-complete-container']`) |
| 2 | Pony Express image | `By.cssSelector("[data-test='pony-express']")` | css(`img.pony_express`) |
| 3 | Complete header | `By.cssSelector("[data-test='complete-header']")` | text("Thank you for your order!") |
| 4 | Complete text | `By.cssSelector("[data-test='complete-text']")` | — |
| 5 | Back Home button | `By.cssSelector("[data-test='back-to-products']")` | id(`back-to-products`), text("Back Home") |

```java
// CheckoutCompletePage.java
private final By completeHeader = By.cssSelector("[data-test='complete-header']");
private final By completeText   = By.cssSelector("[data-test='complete-text']");
private final By backHomeBtn    = By.cssSelector("[data-test='back-to-products']");

private final HealableElement backHomeBtnHeal = HealableElement.builder()
    .primary(By.cssSelector("[data-test='back-to-products']"))
    .fallback(ByTextStrategy.of("Back Home"))
    .fallback(ByAttributeContainsStrategy.of("id", "back-to-products"))
    .build();
```

---

## 8. Footer (chung cho tất cả trang)

| # | Element | Primary |
|---|---|---|
| 1 | Footer container | `By.cssSelector("[data-test='footer']")` |
| 2 | Twitter link | `By.cssSelector("[data-test='social-twitter']")` |
| 3 | Facebook link | `By.cssSelector("[data-test='social-facebook']")` |
| 4 | LinkedIn link | `By.cssSelector("[data-test='social-linkedin']")` |
| 5 | Copyright text | `By.cssSelector("[data-test='footer-copy']")` |

---

## 9. Tổng kết quy ước

### 9.1. Khi nào dùng `HealableElement`?

| Loại element | Cần healable? | Lý do |
|---|---|---|
| Button thao tác chính (Login, Checkout, Finish, Add to cart) | ✅ Có | Frequent target, dễ break |
| Input field bắt buộc (username, firstName, ...) | ✅ Có | Test fail ngay nếu locator hỏng |
| Title / heading (text-only assertion) | ❌ Không | Chỉ assertion 1 lần, fail rõ ràng |
| Static label (QTY, Description) | ❌ Không | Không tương tác |
| Read-only info (price, total, error message) | ⚠️ Tuỳ | Healable nếu test fail khi locator đổi; thường primary đủ |
| Footer link (social) | ❌ Không | Hiếm khi assert |

### 9.2. Anti-patterns cần tránh trên saucedemo

| ❌ | ✅ |
|---|---|
| `By.xpath("//button[3]")` | `By.cssSelector("[data-test='add-to-cart-...']")` |
| `By.className("btn_primary")` | `By.cssSelector("[data-test='login-button']")` |
| Hardcode index `inventory-item:nth-child(1)` | Lọc theo text/slug |
| Build locator cho `complete-header` rồi assert sai chính tả "Thank you" | Assert qua text dùng `getText().contains("Thank you")` |

### 9.3. Verify uniqueness — TẤT CẢ locator trong file này đã verify match đúng 1 element trên DOM

> Xác minh trên browser thật bằng `document.querySelectorAll("[data-test='...']").length === 1` cho mỗi locator unique. Các locator dạng list (`inventory-item`, `inventory-item-name`...) trả về 6 (số sản phẩm) — đúng theo design.

---

## 10. Bước tiếp theo

```
/page-gen LoginPage          # sinh full LoginPage.java
/page-gen InventoryPage      # sinh InventoryPage.java
/page-gen CartPage           # sinh CartPage.java
/page-gen CheckoutStepOnePage
/page-gen CheckoutStepTwoPage
/page-gen CheckoutCompletePage
```

→ Page Object class sẽ kế thừa `BasePage`, dùng locator catalogue này, method có `@Step("...")` Allure annotation.

Hoặc generate luôn full automation suite:
```
/generate_automation_from_testcases <test_cases_file>
```
