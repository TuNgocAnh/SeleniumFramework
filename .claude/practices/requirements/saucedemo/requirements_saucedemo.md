# 📋 Tài Liệu Yêu Cầu — Swag Labs (saucedemo.com)

> **URL:** https://www.saucedemo.com
> **Nguồn dữ liệu:** Inspect DOM trực tiếp qua Playwright MCP (viewport 1920×1080)
> **Phạm vi:** Toàn bộ luồng e-commerce demo — Đăng nhập → Danh sách sản phẩm → Giỏ hàng → Thanh toán → Hoàn tất đơn

---

## 1. Tổng Quan (Overview)

**Swag Labs** là một website e-commerce **demo** dành cho mục đích học và thực hành kiểm thử tự động. Hệ thống cung cấp luồng mua hàng đầy đủ với các trạng thái người dùng đặc biệt để giả lập lỗi (locked, performance glitch, visual bug, v.v.).

### Đối tượng người dùng
- QA Engineer / Automation Tester đang luyện tập
- Developer làm demo flow checkout

### Phạm vi tài liệu
| Module | URL | Mô tả |
|---|---|---|
| Đăng nhập | `/` | Form xác thực người dùng |
| Danh sách sản phẩm | `/inventory.html` | Hiển thị 6 sản phẩm, sort, add-to-cart |
| Chi tiết sản phẩm | `/inventory-item.html?id=<n>` | Trang con — chưa nằm trong scope inspect lần này |
| Giỏ hàng | `/cart.html` | Xem/xoá sản phẩm trong giỏ |
| Checkout — Step 1 | `/checkout-step-one.html` | Nhập thông tin giao hàng |
| Checkout — Step 2 | `/checkout-step-two.html` | Xem tổng tiền + Payment/Shipping |
| Checkout — Complete | `/checkout-complete.html` | Hoàn tất đơn hàng |

---

## 2. Yêu Cầu Chức Năng (Functional Requirements)

### FR-01 — Đăng nhập (Login)

> **User Story:** Là một khách hàng, tôi muốn đăng nhập bằng username + password để truy cập danh sách sản phẩm và mua hàng.

**Acceptance Criteria:**
- AC-01.1: Nhập username + password đúng → điều hướng tới `/inventory.html`.
- AC-01.2: Bỏ trống username → hiển thị thông báo `Epic sadface: Username is required`.
- AC-01.3: Có username, bỏ trống password → hiển thị `Epic sadface: Password is required`.
- AC-01.4: Sai username hoặc password → hiển thị `Epic sadface: Username and password do not match any user in this service`.
- AC-01.5: Đăng nhập bằng `locked_out_user` → hiển thị `Epic sadface: Sorry, this user has been locked out.`.
- AC-01.6: Thông báo lỗi có nút **(X)** đóng để dismiss.

**Tài khoản demo (public):**

| Username | Trạng thái |
|---|---|
| `standard_user` | Normal — login + flow thành công |
| `locked_out_user` | Bị khoá — không thể login |
| `problem_user` | Login được nhưng UI/data có bug |
| `performance_glitch_user` | Login chậm bất thường |
| `error_user` | Login được nhưng có lỗi runtime |
| `visual_user` | Login được nhưng có visual bug |

> **Password chung cho tất cả:** `secret_sauce`

---

### FR-02 — Danh sách sản phẩm (Products / Inventory)

> **User Story:** Là người dùng đã đăng nhập, tôi muốn xem danh sách sản phẩm, sắp xếp theo tên/giá, và thêm sản phẩm vào giỏ.

**Acceptance Criteria:**
- AC-02.1: Hiển thị tiêu đề `Products` ở đầu trang.
- AC-02.2: Hiển thị 6 sản phẩm, mỗi sản phẩm có: ảnh, tên (link), mô tả, giá, nút **Add to cart**.
- AC-02.3: Click **Add to cart** → nút đổi thành **Remove**, badge giỏ hàng tăng 1.
- AC-02.4: Click **Remove** → nút đổi lại thành **Add to cart**, badge giỏ giảm 1.
- AC-02.5: Dropdown sort có 4 lựa chọn: `Name (A to Z)` (mặc định), `Name (Z to A)`, `Price (low to high)`, `Price (high to low)`.
- AC-02.6: Click vào tên hoặc ảnh sản phẩm → vào trang chi tiết.
- AC-02.7: Click icon giỏ hàng (góc trên phải) → điều hướng tới `/cart.html`.

**Catalogue sản phẩm:**

| # | Tên | Giá (USD) |
|---|---|---|
| 1 | Sauce Labs Backpack | 29.99 |
| 2 | Sauce Labs Bike Light | 9.99 |
| 3 | Sauce Labs Bolt T-Shirt | 15.99 |
| 4 | Sauce Labs Fleece Jacket | 49.99 |
| 5 | Sauce Labs Onesie | 7.99 |
| 6 | Test.allTheThings() T-Shirt (Red) | 15.99 |

---

### FR-03 — Menu Sidebar (Hamburger)

> **User Story:** Là người dùng, tôi muốn truy cập các action điều hướng chính từ menu.

**Acceptance Criteria:**
- AC-03.1: Click nút **Open Menu** (góc trên trái) → sidebar trượt ra.
- AC-03.2: Sidebar có 4 mục: **All Items**, **About**, **Logout**, **Reset App State**.
- AC-03.3: **All Items** → quay lại `/inventory.html`.
- AC-03.4: **About** → mở `https://saucelabs.com/` (external).
- AC-03.5: **Logout** → trả về `/` (login page), session clear.
- AC-03.6: **Reset App State** → xoá toàn bộ giỏ hàng + state hiện tại.
- AC-03.7: Click **Close Menu** (X) → đóng sidebar.

---

### FR-04 — Giỏ hàng (Cart)

> **User Story:** Là người dùng, tôi muốn xem các sản phẩm đã chọn, xoá sản phẩm, và tiến hành checkout.

**Acceptance Criteria:**
- AC-04.1: Tiêu đề trang là `Your Cart`.
- AC-04.2: Bảng 2 cột: **QTY**, **Description**.
- AC-04.3: Mỗi sản phẩm trong giỏ hiển thị: số lượng (luôn = 1 — hệ thống không hỗ trợ tăng/giảm số lượng), tên, mô tả, giá, nút **Remove**.
- AC-04.4: Click **Remove** trong giỏ → sản phẩm biến mất, badge giỏ giảm 1.
- AC-04.5: Nút **Continue Shopping** → quay về `/inventory.html`, giữ nguyên giỏ.
- AC-04.6: Nút **Checkout** → điều hướng tới `/checkout-step-one.html`.
- AC-04.7: Khi giỏ rỗng — vẫn cho phép vào trang checkout (không block).

---

### FR-05 — Checkout: Your Information (Step 1)

> **User Story:** Là khách hàng, tôi muốn nhập thông tin giao hàng để tiếp tục thanh toán.

**Acceptance Criteria:**
- AC-05.1: Tiêu đề là `Checkout: Your Information`.
- AC-05.2: Form gồm 3 field bắt buộc: **First Name**, **Last Name**, **Zip/Postal Code**.
- AC-05.3: Click **Continue** khi thiếu First Name → hiển thị `Error: First Name is required`.
- AC-05.4: Có First Name, thiếu Last Name → `Error: Last Name is required`.
- AC-05.5: Có First + Last, thiếu Zip → `Error: Postal Code is required`.
- AC-05.6: Điền đủ 3 field + click **Continue** → điều hướng tới `/checkout-step-two.html`.
- AC-05.7: Nút **Cancel** → quay về `/cart.html`, giữ nguyên giỏ.

---

### FR-06 — Checkout: Overview (Step 2)

> **User Story:** Là khách hàng, tôi muốn xem tổng tiền + payment/shipping info trước khi xác nhận đơn.

**Acceptance Criteria:**
- AC-06.1: Tiêu đề là `Checkout: Overview`.
- AC-06.2: Hiển thị danh sách sản phẩm trong đơn (giống cart).
- AC-06.3: Hiển thị **Payment Information**: `SauceCard #31337` (hardcoded).
- AC-06.4: Hiển thị **Shipping Information**: `Free Pony Express Delivery!` (hardcoded).
- AC-06.5: Khu vực **Price Total** hiển thị:
  - `Item total: $<sum>`
  - `Tax: $<sum × 8%>` (làm tròn 2 chữ số thập phân)
  - `Total: $<item_total + tax>`
- AC-06.6: Click **Finish** → điều hướng tới `/checkout-complete.html`, giỏ được clear.
- AC-06.7: Click **Cancel** → quay về `/inventory.html`.

> **Ví dụ tính toán:** 1 × Backpack ($29.99) → Item total: $29.99, Tax: $2.40, Total: $32.39.

---

### FR-07 — Checkout: Complete

> **User Story:** Là khách hàng, tôi muốn nhận xác nhận đơn hàng đã đặt thành công.

**Acceptance Criteria:**
- AC-07.1: Tiêu đề là `Checkout: Complete!`.
- AC-07.2: Hiển thị ảnh "Pony Express".
- AC-07.3: Heading `Thank you for your order!`.
- AC-07.4: Thông điệp `Your order has been dispatched, and will arrive just as fast as the pony can get there!`.
- AC-07.5: Nút **Back Home** → quay về `/inventory.html`.

---

## 3. Đặc Tả Trường Dữ Liệu (Field Specifications)

### 3.1. Form Đăng Nhập (`/`)

| Field | Loại UI | Bắt buộc | Validation | Ghi chú |
|---|---|---|---|---|
| Username | `input[type=text]` | ✅ | Không trống | `data-test="username"` |
| Password | `input[type=password]` | ✅ | Không trống | `data-test="password"`, ẩn ký tự |

**Button:** `Login` (`data-test="login-button"`) — submit form.

### 3.2. Form Checkout Step 1 (`/checkout-step-one.html`)

| Field | Loại UI | Bắt buộc | Validation | Ghi chú |
|---|---|---|---|---|
| First Name | `input[type=text]` | ✅ | Không trống | `data-test="firstName"` |
| Last Name | `input[type=text]` | ✅ | Không trống | `data-test="lastName"` |
| Zip/Postal Code | `input[type=text]` | ✅ | Không trống — KHÔNG validate format zip cụ thể | `data-test="postalCode"` |

**Buttons:**
- `Cancel` (`data-test="cancel"`) → `/cart.html`
- `Continue` (`data-test="continue"`) → submit + validate

### 3.3. Dropdown Sort (`/inventory.html`)

| Option | Hành vi |
|---|---|
| `Name (A to Z)` (default) | Sort theo tên tăng dần |
| `Name (Z to A)` | Sort theo tên giảm dần |
| `Price (low to high)` | Sort theo giá tăng dần |
| `Price (high to low)` | Sort theo giá giảm dần |

`data-test="product-sort-container"` (select).

---

## 4. Luồng Nghiệp Vụ & Báo Lỗi (Business Rules & Validations)

### 4.1. Quy tắc nghiệp vụ

| # | Quy tắc | Vị trí |
|---|---|---|
| BR-01 | User `locked_out_user` không được phép login dù password đúng | Login |
| BR-02 | Sản phẩm chỉ thêm được tối đa **1 lần** vào giỏ (nút toggle Add↔Remove, không tăng số lượng) | Inventory |
| BR-03 | Thuế bằng **8%** giá trị đơn (làm tròn 2 chữ số thập phân) | Checkout Step 2 |
| BR-04 | Payment + Shipping info là **giá trị cố định** — không có form chỉnh sửa | Checkout Step 2 |
| BR-05 | Sau khi Finish, giỏ hàng được **clear hoàn toàn** | Checkout Complete |
| BR-06 | **Reset App State** trong menu xoá toàn bộ giỏ + state form | Sidebar |

### 4.2. Bảng Validation Messages

| Ngữ cảnh | Input | Thông báo |
|---|---|---|
| Login — trống username | `""` + bất kỳ password | `Epic sadface: Username is required` |
| Login — trống password | username hợp lệ + `""` | `Epic sadface: Password is required` |
| Login — sai credentials | `standard_user` + `wrong_pass` | `Epic sadface: Username and password do not match any user in this service` |
| Login — user bị khoá | `locked_out_user` + `secret_sauce` | `Epic sadface: Sorry, this user has been locked out.` |
| Checkout Step 1 — trống First Name | `""` | `Error: First Name is required` |
| Checkout Step 1 — trống Last Name | có First, `""` | `Error: Last Name is required` |
| Checkout Step 1 — trống Zip | có First+Last, `""` | `Error: Postal Code is required` |

> Tất cả error message hiển thị dạng banner đỏ trên form, có nút **X** để đóng.

---

## 5. Yêu Cầu Phi Chức Năng (Non-Functional Requirements)

| # | Yêu cầu | Quan sát |
|---|---|---|
| NFR-01 | Tương thích browser | Trang chạy mượt trên Chrome/Edge/Firefox (test với Playwright Chromium) |
| NFR-02 | Responsive | Có viewport meta; bố cục chính phù hợp với 1920×1080 (chưa kiểm mobile) |
| NFR-03 | Performance | User `performance_glitch_user` cố tình mô phỏng độ trễ |
| NFR-04 | Security | Trang **demo** — credentials public, không HTTPS-only cookie, không CAPTCHA |
| NFR-05 | Accessibility | Có aria-label cho nút, button có cursor pointer; ảnh có alt text |

---

## 6. Câu Hỏi / Làm Rõ Với PO (Open Questions)

> Các điểm dưới đây quan sát thấy nhưng cần xác nhận business rule cụ thể trước khi viết test case cuối:

1. **Q-01:** Sản phẩm có hỗ trợ tăng số lượng (>1) không? — Quan sát hiện tại: nút toggle, qty cố định = 1. **Giả định:** Không hỗ trợ. **Cần xác nhận** đây là feature hay bug.
2. **Q-02:** Zip code có format validation cụ thể (chỉ digit, độ dài) không? — Quan sát: chấp nhận mọi chuỗi non-empty kể cả `"abcde"`.
3. **Q-03:** Có giới hạn độ dài cho First Name / Last Name không?
4. **Q-04:** Behavior khi vào `/cart.html` trực tiếp lúc chưa login? — Cần verify (giả định: redirect login).
5. **Q-05:** Behavior khi click **Checkout** với giỏ rỗng? — Hiện tại cho qua → khả năng là bug.
6. **Q-06:** Tax 8% áp dụng cho mọi đơn hàng và mọi tài khoản, hay phụ thuộc location của Zip code?
7. **Q-07:** Các user đặc biệt (`problem_user`, `visual_user`, `error_user`) gây bug cụ thể ở module nào? — Cần whitelist để viết visual regression / error handling test.

---

## 7. Tham Chiếu

- **Inspect tool:** Playwright MCP — viewport 1920×1080
- **Pages inspected:** `/`, `/inventory.html`, `/cart.html`, `/checkout-step-one.html`, `/checkout-step-two.html`, `/checkout-complete.html`
- **Test attribute prefix:** Toàn site dùng `data-test="..."` — locator priority **tốt**, ưu tiên dùng `By.cssSelector("[data-test='...']")` trong Selenium.

---

> **Bước tiếp theo gợi ý:**
> - Sinh test cases chi tiết → `/generate_testcases_from_requirements <file này>`
> - Sinh automation scripts → `/generate_automation_from_testcases <test cases>`
> - Sinh locator cho từng element → `/generate_locator`
