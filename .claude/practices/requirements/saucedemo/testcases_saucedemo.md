# 🧪 Manual Test Cases — Swag Labs (saucedemo.com)

> **Nguồn:** [requirements_saucedemo.md](requirements_saucedemo.md)
> **Locator reference:** [locators_saucedemo.md](locators_saucedemo.md)
> **Mode:** QUICK (1 lượt — không qua RBT 6 bước)
> **Sinh ngày:** 2026-05-26
> **Kỹ thuật áp dụng:** Equivalence Partitioning (EP), Boundary Value Analysis (BVA), Decision Table, Field-Level Validation

## Quy ước

- **TC ID format:** `SAUCE_<MODULE>_TC_<số>` — `SAUCE` là project code, MODULE viết tắt module (LOGIN, INV, MENU, CART, CHK1, CHK2, CHK3, E2E)
- **Priority:** P1 Critical (chặn release) / P2 High / P3 Medium / P4 Low
- **Pre-condition mặc định:** Trình duyệt mở `https://www.saucedemo.com/`, viewport 1920×1080, app ở trạng thái fresh (Reset App State trước nếu cần)

---

## 1. Module LOGIN — Đăng nhập (`/`)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_LOGIN_TC_01 | LOGIN | Đăng nhập thành công với standard_user | Đang ở trang `/` | 1. Nhập username<br>2. Nhập password<br>3. Click **Login** | `standard_user` / `secret_sauce` | URL chuyển sang `/inventory.html`, hiển thị danh sách 6 sản phẩm, tiêu đề `Products` | P1 |
| SAUCE_LOGIN_TC_02 | LOGIN | Đăng nhập thất bại — user bị khoá | Đang ở trang `/` | 1. Nhập username<br>2. Nhập password<br>3. Click **Login** | `locked_out_user` / `secret_sauce` | URL vẫn `/`, hiển thị error: `Epic sadface: Sorry, this user has been locked out.` | P1 |
| SAUCE_LOGIN_TC_03 | LOGIN | Đăng nhập — username trống | Đang ở trang `/` | 1. Bỏ trống username<br>2. Nhập password<br>3. Click **Login** | username = `""` / password = `secret_sauce` | Error: `Epic sadface: Username is required`. URL không đổi | P1 |
| SAUCE_LOGIN_TC_04 | LOGIN | Đăng nhập — password trống | Đang ở trang `/` | 1. Nhập username<br>2. Bỏ trống password<br>3. Click **Login** | username = `standard_user` / password = `""` | Error: `Epic sadface: Password is required`. URL không đổi | P1 |
| SAUCE_LOGIN_TC_05 | LOGIN | Đăng nhập — cả 2 field trống | Đang ở trang `/` | 1. Bỏ trống cả 2 field<br>2. Click **Login** | `""` / `""` | Error: `Epic sadface: Username is required` (priority: Username check trước Password) | P1 |
| SAUCE_LOGIN_TC_06 | LOGIN | Đăng nhập — username đúng, password sai | Đang ở trang `/` | 1. Nhập username<br>2. Nhập password sai<br>3. Click **Login** | `standard_user` / `wrong_password` | Error: `Epic sadface: Username and password do not match any user in this service` | P1 |
| SAUCE_LOGIN_TC_07 | LOGIN | Đăng nhập — username không tồn tại | Đang ở trang `/` | 1. Nhập username không có trong hệ thống<br>2. Nhập password<br>3. Click **Login** | `ghost_user_xyz` / `secret_sauce` | Error: `Epic sadface: Username and password do not match any user in this service` | P2 |
| SAUCE_LOGIN_TC_08 | LOGIN | Đăng nhập case-sensitive — username viết hoa | Đang ở trang `/` | 1. Nhập username uppercase<br>2. Nhập password<br>3. Click **Login** | `STANDARD_USER` / `secret_sauce` | Error: `Epic sadface: Username and password do not match any user in this service` (username phân biệt hoa thường) | P3 |
| SAUCE_LOGIN_TC_09 | LOGIN | Đăng nhập — username có khoảng trắng đầu/cuối | Đang ở trang `/` | 1. Nhập username có spaces<br>2. Nhập password<br>3. Click **Login** | `"  standard_user  "` / `secret_sauce` | Error: `Epic sadface: Username and password do not match any user in this service` (hệ thống KHÔNG auto-trim) | P3 |
| SAUCE_LOGIN_TC_10 | LOGIN | Đăng nhập — SQL injection attempt | Đang ở trang `/` | 1. Nhập username payload<br>2. Nhập password payload<br>3. Click **Login** | `' OR '1'='1` / `' OR '1'='1` | Error mismatch (không bypass auth). KHÔNG có lỗi 500 | P2 |
| SAUCE_LOGIN_TC_11 | LOGIN | Đăng nhập — XSS payload trong username | Đang ở trang `/` | 1. Nhập payload XSS<br>2. Nhập password<br>3. Click **Login** | `<script>alert(1)</script>` / `secret_sauce` | Error mismatch. KHÔNG hiển thị alert popup (XSS bị escape) | P2 |
| SAUCE_LOGIN_TC_12 | LOGIN | Đăng nhập — username rất dài (256 ký tự) | Đang ở trang `/` | 1. Nhập username 256 ký tự `a`<br>2. Nhập password<br>3. Click **Login** | `"aaa...aaa"` (256 chars) / `secret_sauce` | Error mismatch hoặc lỗi validation độ dài. KHÔNG crash | P4 |
| SAUCE_LOGIN_TC_13 | LOGIN | Đóng error banner bằng nút X | Đang ở trang `/` đang hiển thị error | 1. Trigger error (login fail)<br>2. Click nút **X** trên banner | Sau TC_03 fail | Error banner biến mất, form vẫn giữ data đã nhập | P3 |
| SAUCE_LOGIN_TC_14 | LOGIN | Field password ẩn ký tự (masked) | Đang ở trang `/` | 1. Nhập password vào field | password = `secret_sauce` | Hiển thị `•••••••••••` (input type="password"), không lộ plaintext | P2 |
| SAUCE_LOGIN_TC_15 | LOGIN | Đăng nhập với performance_glitch_user | Đang ở trang `/` | 1. Nhập username<br>2. Nhập password<br>3. Click **Login**<br>4. Đo thời gian load | `performance_glitch_user` / `secret_sauce` | Login thành công nhưng chậm bất thường (~5s+). Confirm bug đã biết của user này | P4 |

**Tổng module LOGIN: 15 TCs** (P1: 6, P2: 4, P3: 3, P4: 2)

---

## 2. Module INV — Danh sách sản phẩm (`/inventory.html`)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_INV_TC_01 | INV | Hiển thị đủ 6 sản phẩm sau khi login | Đã login `standard_user` | 1. Đếm số inventory item trên trang | — | Có đúng 6 sản phẩm, mỗi sản phẩm có: ảnh, tên, mô tả, giá, nút Add to cart | P1 |
| SAUCE_INV_TC_02 | INV | Add to cart — Backpack | Đã login, giỏ rỗng | 1. Click **Add to cart** trên Backpack | Backpack ($29.99) | Nút đổi thành **Remove**, badge giỏ hàng hiển thị `1` | P1 |
| SAUCE_INV_TC_03 | INV | Add to cart nhiều sản phẩm | Đã login, giỏ rỗng | 1. Add Backpack<br>2. Add Bike Light<br>3. Add Bolt T-Shirt | 3 sản phẩm | Cart badge = `3`, cả 3 nút đổi thành Remove | P1 |
| SAUCE_INV_TC_04 | INV | Remove khỏi giỏ ngay tại inventory | Đã login, đã add Backpack | 1. Click **Remove** trên Backpack | — | Nút đổi lại thành Add to cart, badge giỏ giảm 1 (hoặc biến mất nếu = 0) | P1 |
| SAUCE_INV_TC_05 | INV | Sort — Name (A to Z) (default) | Đã login | 1. Kiểm tra thứ tự tên sản phẩm | — | Sản phẩm sort theo alphabet tăng dần: Sauce Labs Backpack → ... → Test.allTheThings() T-Shirt | P2 |
| SAUCE_INV_TC_06 | INV | Sort — Name (Z to A) | Đã login | 1. Chọn option `Name (Z to A)` trong dropdown | sort = `za` | Sản phẩm sort theo alphabet giảm dần | P2 |
| SAUCE_INV_TC_07 | INV | Sort — Price (low to high) | Đã login | 1. Chọn option `Price (low to high)` | sort = `lohi` | Đầu danh sách là $7.99 (Onesie), cuối là $49.99 (Fleece Jacket) | P2 |
| SAUCE_INV_TC_08 | INV | Sort — Price (high to low) | Đã login | 1. Chọn option `Price (high to low)` | sort = `hilo` | Đầu danh sách là $49.99, cuối là $7.99 | P2 |
| SAUCE_INV_TC_09 | INV | Click tên sản phẩm → trang chi tiết | Đã login | 1. Click vào tên `Sauce Labs Backpack` | — | URL chuyển sang `/inventory-item.html?id=4`, hiển thị chi tiết Backpack | P2 |
| SAUCE_INV_TC_10 | INV | Click ảnh sản phẩm → trang chi tiết | Đã login | 1. Click vào ảnh Backpack | — | URL chuyển sang `/inventory-item.html?id=4` (giống click tên) | P3 |
| SAUCE_INV_TC_11 | INV | Click icon cart → trang giỏ hàng | Đã login | 1. Click icon giỏ hàng góc phải | — | URL chuyển `/cart.html` | P1 |
| SAUCE_INV_TC_12 | INV | Toggle Add ↔ Remove cùng 1 sản phẩm | Đã login | 1. Add Backpack → 2. Remove → 3. Add lại → 4. Remove | — | State toggle đúng mỗi lần. Badge: 1 → 0 → 1 → 0 | P3 |
| SAUCE_INV_TC_13 | INV | Persistence giỏ hàng qua reload | Đã login, đã add Backpack | 1. F5 reload trang | — | Cart badge vẫn `1`, nút Backpack vẫn Remove | P2 |

**Tổng module INV: 13 TCs** (P1: 5, P2: 6, P3: 2)

---

## 3. Module MENU — Sidebar Hamburger

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_MENU_TC_01 | MENU | Mở sidebar | Đã login, đang ở inventory | 1. Click **Open Menu** | — | Sidebar trượt từ trái sang, hiển thị 4 link: All Items, About, Logout, Reset App State | P2 |
| SAUCE_MENU_TC_02 | MENU | Đóng sidebar bằng nút X | Đã login, sidebar đang mở | 1. Click nút **Close Menu** (X) | — | Sidebar đóng lại | P3 |
| SAUCE_MENU_TC_03 | MENU | All Items → inventory | Đã login, đang ở `/cart.html`, sidebar mở | 1. Click **All Items** | — | URL chuyển về `/inventory.html` | P2 |
| SAUCE_MENU_TC_04 | MENU | About → saucelabs.com | Đã login, sidebar mở | 1. Click **About** | — | URL chuyển sang `https://saucelabs.com/` (external) | P3 |
| SAUCE_MENU_TC_05 | MENU | Logout | Đã login, sidebar mở | 1. Click **Logout** | — | URL về `/`, session clear, hiển thị form login | P1 |
| SAUCE_MENU_TC_06 | MENU | Reset App State xoá giỏ | Đã login, đã add 3 sản phẩm | 1. Mở sidebar<br>2. Click **Reset App State** | — | Badge cart biến mất. Tuy nhiên các nút trên inventory **không** auto refresh sang Add to cart — đây là known issue | P2 |

**Tổng module MENU: 6 TCs** (P1: 1, P2: 3, P3: 2)

---

## 4. Module CART — Giỏ hàng (`/cart.html`)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_CART_TC_01 | CART | Xem giỏ hàng có 1 sản phẩm | Đã login, đã add Backpack | 1. Vào `/cart.html` | — | Hiển thị: QTY=1, Sauce Labs Backpack, mô tả, $29.99, nút Remove | P1 |
| SAUCE_CART_TC_02 | CART | Xem giỏ hàng rỗng | Đã login, giỏ rỗng | 1. Vào `/cart.html` | — | Bảng QTY/Description hiển thị nhưng không có item nào. 2 nút Continue Shopping + Checkout vẫn hiển thị | P2 |
| SAUCE_CART_TC_03 | CART | Remove sản phẩm khỏi giỏ | Đã login, đã add Backpack | 1. Vào `/cart.html`<br>2. Click **Remove** trên Backpack | — | Item biến mất, badge giỏ về 0 | P1 |
| SAUCE_CART_TC_04 | CART | Continue Shopping quay về inventory | Đã login, đang ở cart | 1. Click **Continue Shopping** | — | URL về `/inventory.html`, giỏ hàng giữ nguyên | P2 |
| SAUCE_CART_TC_05 | CART | Checkout từ giỏ có sản phẩm | Đã login, đã add Backpack | 1. Click **Checkout** | — | URL chuyển `/checkout-step-one.html` | P1 |
| SAUCE_CART_TC_06 | CART | Checkout với giỏ rỗng (known issue) | Đã login, giỏ rỗng | 1. Click **Checkout** | — | URL chuyển `/checkout-step-one.html` (hệ thống KHÔNG block — quan sát thực tế) | P3 |
| SAUCE_CART_TC_07 | CART | Click tên sản phẩm trong giỏ | Đã login, đã add Backpack | 1. Vào `/cart.html`<br>2. Click tên `Sauce Labs Backpack` | — | URL chuyển sang trang chi tiết sản phẩm | P3 |
| SAUCE_CART_TC_08 | CART | Số lượng cố định = 1 (không có UI tăng giảm) | Đã login, đã add Backpack | 1. Vào `/cart.html`<br>2. Tìm nút +/- quantity | — | Quantity hiển thị `1` dạng text, KHÔNG có spinner/input. Add lại cùng SP không tăng số lượng | P3 |

**Tổng module CART: 8 TCs** (P1: 3, P2: 2, P3: 3)

---

## 5. Module CHK1 — Checkout Step 1 (Your Information)

> Field-level validation — mỗi field có TC riêng (KHÔNG gộp)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_CHK1_TC_01 | CHK1 | Happy path — điền đủ 3 field | Đã ở `/checkout-step-one.html` | 1. Nhập First Name<br>2. Nhập Last Name<br>3. Nhập Zip<br>4. Click **Continue** | `John` / `Doe` / `10000` | URL chuyển `/checkout-step-two.html` | P1 |
| SAUCE_CHK1_TC_02 | CHK1 | Trống First Name | Đã ở `/checkout-step-one.html` | 1. Bỏ trống First Name<br>2. Nhập Last Name + Zip<br>3. Click **Continue** | `""` / `Doe` / `10000` | Error: `Error: First Name is required` | P1 |
| SAUCE_CHK1_TC_03 | CHK1 | Trống Last Name | Đã ở `/checkout-step-one.html` | 1. Nhập First<br>2. Bỏ trống Last Name<br>3. Nhập Zip<br>4. Click **Continue** | `John` / `""` / `10000` | Error: `Error: Last Name is required` | P1 |
| SAUCE_CHK1_TC_04 | CHK1 | Trống Zip | Đã ở `/checkout-step-one.html` | 1. Nhập First + Last<br>2. Bỏ trống Zip<br>3. Click **Continue** | `John` / `Doe` / `""` | Error: `Error: Postal Code is required` | P1 |
| SAUCE_CHK1_TC_05 | CHK1 | Trống cả 3 field | Đã ở `/checkout-step-one.html` | 1. Bỏ trống cả 3<br>2. Click **Continue** | `""` / `""` / `""` | Error hiển thị First Name first (theo priority validate) | P2 |
| SAUCE_CHK1_TC_06 | CHK1 | First Name có khoảng trắng (whitespace only) | Đã ở `/checkout-step-one.html` | 1. Nhập 3 spaces vào First Name<br>2. Last + Zip hợp lệ<br>3. Click **Continue** | `"   "` / `Doe` / `10000` | **Cần xác nhận:** quan sát thực tế — pass (hệ thống không trim/validate whitespace). Open question Q-03 | P3 |
| SAUCE_CHK1_TC_07 | CHK1 | First Name chứa ký tự đặc biệt | Đã ở `/checkout-step-one.html` | 1. Nhập tên có dấu<br>2. Last + Zip hợp lệ<br>3. Click **Continue** | `Nguyễn` / `Văn` / `100000` | Pass — hệ thống chấp nhận Unicode | P3 |
| SAUCE_CHK1_TC_08 | CHK1 | First Name chứa XSS payload | Đã ở `/checkout-step-one.html` | 1. Nhập payload<br>2. Last + Zip hợp lệ<br>3. Click **Continue** | `<script>alert(1)</script>` / `Doe` / `10000` | Pass tới step 2, KHÔNG hiển thị alert (XSS bị escape khi render) | P2 |
| SAUCE_CHK1_TC_09 | CHK1 | First Name 1 ký tự (min boundary) | Đã ở `/checkout-step-one.html` | 1. Nhập 1 ký tự<br>2. Last + Zip hợp lệ<br>3. Click **Continue** | `A` / `B` / `1` | Pass — không có min length | P3 |
| SAUCE_CHK1_TC_10 | CHK1 | First Name 100+ ký tự (max boundary) | Đã ở `/checkout-step-one.html` | 1. Nhập 256 ký tự<br>2. Last + Zip hợp lệ<br>3. Click **Continue** | 256 char `a` / `Doe` / `10000` | Pass — không có max length giới hạn (Open question Q-03) | P4 |
| SAUCE_CHK1_TC_11 | CHK1 | Zip chứa chữ cái (sai format) | Đã ở `/checkout-step-one.html` | 1. Nhập First + Last<br>2. Zip = chữ cái<br>3. Click **Continue** | `John` / `Doe` / `abcde` | Pass — hệ thống KHÔNG validate format zip (Open question Q-02) | P3 |
| SAUCE_CHK1_TC_12 | CHK1 | Zip chứa ký tự đặc biệt | Đã ở `/checkout-step-one.html` | 1. Nhập First + Last<br>2. Zip = ký tự đặc biệt<br>3. Click **Continue** | `John` / `Doe` / `!@#$%` | Pass — không có validate | P4 |
| SAUCE_CHK1_TC_13 | CHK1 | Cancel quay về cart | Đã ở `/checkout-step-one.html`, đã nhập data | 1. Click **Cancel** | đã nhập `John`/`Doe`/`10000` | URL về `/cart.html`, data form đã nhập không cần persist | P2 |
| SAUCE_CHK1_TC_14 | CHK1 | Đóng error bằng nút X | Đã ở step 1 đang hiển thị error | 1. Trigger error TC_02<br>2. Click **X** | — | Error banner biến mất, form giữ data | P3 |
| SAUCE_CHK1_TC_15 | CHK1 | Truy cập step 1 trực tiếp khi chưa login | Chưa login | 1. Truy cập trực tiếp `/checkout-step-one.html` | — | **Cần xác nhận:** redirect về `/` hay hiển thị form? Open question Q-04 | P2 |

**Tổng module CHK1: 15 TCs** (P1: 4, P2: 4, P3: 5, P4: 2)

---

## 6. Module CHK2 — Checkout Step 2 (Overview)

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_CHK2_TC_01 | CHK2 | Hiển thị thông tin đơn — 1 sản phẩm | Đã hoàn thành step 1 với 1 Backpack | 1. Kiểm tra summary | — | Hiển thị: Backpack, QTY=1, $29.99, Payment Info `SauceCard #31337`, Shipping `Free Pony Express Delivery!` | P1 |
| SAUCE_CHK2_TC_02 | CHK2 | Tính toán Tax đúng 8% (Decision Table) | Đã ở step 2 với 1 Backpack ($29.99) | 1. Đọc Tax label<br>2. Verify công thức | — | `Tax: $2.40` (= $29.99 × 0.08 = 2.3992 → làm tròn $2.40) | P1 |
| SAUCE_CHK2_TC_03 | CHK2 | Tính toán Total đúng | Đã ở step 2 với 1 Backpack | 1. Đọc Total | — | `Total: $32.39` (= 29.99 + 2.40) | P1 |
| SAUCE_CHK2_TC_04 | CHK2 | Tính Tax với nhiều sản phẩm | Đã ở step 2 với Backpack + Bike Light | — | Item total = $39.98, Tax ≈ $3.20, Total ≈ $43.18 | Subtotal/Tax/Total match công thức | P2 |
| SAUCE_CHK2_TC_05 | CHK2 | Finish → checkout complete | Đã ở step 2 | 1. Click **Finish** | — | URL `/checkout-complete.html`, giỏ hàng được clear (badge biến mất) | P1 |
| SAUCE_CHK2_TC_06 | CHK2 | Cancel từ step 2 → inventory | Đã ở step 2 | 1. Click **Cancel** | — | URL về `/inventory.html`, giỏ KHÔNG bị clear | P2 |
| SAUCE_CHK2_TC_07 | CHK2 | Payment + Shipping info là hardcoded | Đã ở step 2 | 1. Tìm form chỉnh sửa Payment/Shipping | — | KHÔNG có form — chỉ là text hardcoded | P3 |
| SAUCE_CHK2_TC_08 | CHK2 | Step 2 với giỏ rỗng | Đã pass step 1 với giỏ rỗng | 1. Vào step 2 | — | Hiển thị summary trống, Item total: $0.00, Tax: $0.00, Total: $0.00 (Open question Q-05) | P3 |

**Tổng module CHK2: 8 TCs** (P1: 4, P2: 2, P3: 2)

---

## 7. Module CHK3 — Checkout Complete

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_CHK3_TC_01 | CHK3 | Hiển thị xác nhận đơn hàng | Đã click Finish ở step 2 | 1. Kiểm tra trang | — | Hiển thị ảnh Pony Express, heading `Thank you for your order!`, message `Your order has been dispatched...`, nút Back Home | P1 |
| SAUCE_CHK3_TC_02 | CHK3 | Back Home → inventory | Đang ở `/checkout-complete.html` | 1. Click **Back Home** | — | URL về `/inventory.html` | P2 |
| SAUCE_CHK3_TC_03 | CHK3 | Giỏ hàng clear sau khi Finish | Đã hoàn tất đơn | 1. Sau Finish, kiểm tra badge giỏ hàng | — | Badge KHÔNG hiển thị (giỏ = 0) | P1 |

**Tổng module CHK3: 3 TCs** (P1: 2, P2: 1)

---

## 8. Module E2E — End-to-End Flow

| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
|---|---|---|---|---|---|---|---|
| SAUCE_E2E_TC_01 | E2E | Happy path đầy đủ — Login → Mua 1 SP → Checkout → Hoàn tất | Đang ở `/` | 1. Login `standard_user`/`secret_sauce`<br>2. Add Backpack<br>3. Vào cart<br>4. Checkout<br>5. Nhập John/Doe/10000<br>6. Click Continue<br>7. Click Finish<br>8. Click Back Home | Như mô tả | Đến `/checkout-complete.html`, thấy `Thank you for your order!`. Back Home → `/inventory.html` với badge giỏ = 0 | P1 |
| SAUCE_E2E_TC_02 | E2E | Happy path với 3 sản phẩm — verify tax tính đúng tổng | Đang ở `/` | 1. Login<br>2. Add Backpack + Bike Light + Onesie<br>3. Checkout → fill form → Finish | Backpack $29.99 + Bike Light $9.99 + Onesie $7.99 = $47.97 | Tax = $3.84 (47.97 × 0.08 = 3.8376), Total = $51.81 | P1 |
| SAUCE_E2E_TC_03 | E2E | Logout giữa flow → quay lại login | Đã login, đã add SP | 1. Mở sidebar<br>2. Click Logout<br>3. Login lại | `standard_user`/`secret_sauce` | Login thành công. **Cần xác nhận** giỏ hàng còn không (Open question — quan sát: giỏ persist) | P2 |
| SAUCE_E2E_TC_04 | E2E | Reset App State giữa flow | Đã login, đã add SP, đã vào step 1 | 1. Quay lại inventory<br>2. Mở sidebar<br>3. Click Reset App State<br>4. Vào cart | — | Cart rỗng, badge biến mất | P2 |

**Tổng module E2E: 4 TCs** (P1: 2, P2: 2)

---

## 📊 Tổng Kết

| Module | TC Count | P1 | P2 | P3 | P4 |
|---|---|---|---|---|---|
| LOGIN | 15 | 6 | 4 | 3 | 2 |
| INV | 13 | 5 | 6 | 2 | 0 |
| MENU | 6 | 1 | 3 | 2 | 0 |
| CART | 8 | 3 | 2 | 3 | 0 |
| CHK1 | 15 | 4 | 4 | 5 | 2 |
| CHK2 | 8 | 4 | 2 | 2 | 0 |
| CHK3 | 3 | 2 | 1 | 0 | 0 |
| E2E | 4 | 2 | 2 | 0 | 0 |
| **TOTAL** | **72** | **27** | **24** | **17** | **4** |

### Phân bố theo Priority

- **P1 Critical:** 27 TCs (38%) — phải PASS trước mỗi release
- **P2 High:** 24 TCs (33%) — chạy regression
- **P3 Medium:** 17 TCs (24%) — chạy weekly
- **P4 Low:** 4 TCs (5%) — chạy on-demand / nightly

### Kỹ thuật áp dụng

| Kỹ thuật | TC áp dụng |
|---|---|
| **Equivalence Partitioning** | TC_LOGIN_01/02/06/07 (valid/invalid/locked users), TC_CHK1_02..04 (required fields), TC_INV_06..08 (sort options) |
| **Boundary Value Analysis** | TC_LOGIN_12, TC_CHK1_09/10 (min/max length), TC_CHK2_02..04 (tax calculation boundaries) |
| **Decision Table** | TC_CHK2_02..04 (Item total × 0.08 = Tax → Total) |
| **Field-Level Validation** | TC_CHK1_02..12 (mỗi field 1 TC riêng) |
| **Edge cases** | TC_LOGIN_10/11 (SQL/XSS), TC_CHK1_07/08 (Unicode/XSS), TC_LOGIN_09 (whitespace) |

---

## 🔗 Open Questions ảnh hưởng tới TC (cần PO clarify)

| Q | Câu hỏi | TC bị ảnh hưởng |
|---|---|---|
| Q-01 | Hỗ trợ quantity > 1? | TC_CART_TC_08 |
| Q-02 | Zip format validation? | TC_CHK1_TC_11, TC_CHK1_TC_12 |
| Q-03 | Min/max length cho name fields? | TC_CHK1_TC_06, TC_CHK1_TC_09, TC_CHK1_TC_10 |
| Q-04 | Behavior khi vào URL nội bộ chưa login? | TC_CHK1_TC_15 |
| Q-05 | Checkout giỏ rỗng — bug hay feature? | TC_CART_TC_06, TC_CHK2_TC_08 |
| Q-07 | Bug đặc thù các user (problem/visual/error)? | Cần TC riêng — chưa cover |

> **Khuyến nghị:** Sau khi PO trả lời, cập nhật TCs hoặc thêm TC cho các scenario chưa cover (especially problem_user / visual_user — visual regression cần screenshot baseline).

---

## ▶️ Bước tiếp theo

1. **Review test cases** với PO/BA để xác nhận business logic.
2. **Sinh automation scripts:**
   ```
   /generate_automation_from_testcases .claude/practices/requirements/saucedemo/testcases_saucedemo.md
   ```
3. **Sinh test data structured:**
   ```
   /generate_test_data Login + Checkout flow saucedemo
   ```
