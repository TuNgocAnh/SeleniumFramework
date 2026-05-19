---
description: Sinh manual test cases nhanh từ requirements (QUICK mode — không qua quy trình 6 bước).
---

# Workflow: Sinh Manual Test Cases Nhanh từ Requirements

Workflow sinh test cases nhanh từ requirements đã sẵn có, không qua quy trình RBT 6 bước.

## ⚠️ Nguyên tắc

- **Mode:** QUICK (1 lượt duy nhất, không chờ user giữa chừng)
- Phù hợp cho module đơn giản, requirements đã rõ ràng
- Nếu requirements quá phức tạp hoặc mơ hồ → đề nghị user clarify trước khi sinh
- Tất cả output bằng **Tiếng Việt**

## Các bước thực hiện

1. **Đọc và hiểu requirements** được user cung cấp
2. **Xác định các luồng chính:** Happy Path, Negative Path, Boundary Cases, Edge Cases
3. **Áp dụng kỹ thuật thiết kế test case:**
   - Equivalence Partitioning (EP)
   - Boundary Value Analysis (BVA)
   - Decision Table (nếu có nhiều rules)
   - State Transition (nếu có workflow)
4. **Validation chuyên biệt từng trường (Field-Level Validation):**
   - Liệt kê tất cả input fields trên form/UI
   - Sinh validation TCs **riêng cho TỪNG trường** theo đặc tính riêng (text, email, phone, date, number, dropdown, file upload, password...)
   - **KHÔNG** gộp validation nhiều trường vào 1 test case
5. **Sinh test cases đầy đủ fields:**
   - TC ID (format: `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`)
   - Module
   - Test Scenario / Test Case Title
   - Pre-conditions
   - Test Steps (đánh số)
   - Expected Results (đánh số tương ứng)
   - Test Data (**phải cụ thể**, không placeholder)
   - Priority (Critical / High / Medium / Low)
6. **Xuất ra bảng Markdown chuẩn**

## Bảng Output

```
| TC ID | Module | Test Scenario | Pre-Condition | Test Steps | Test Data | Expected Result | Priority |
```

## Quy tắc quan trọng

- Test Data phải cụ thể: `test_login_01@domain.com`, không phải "email hợp lệ"
- Phải bao gồm cả Positive, Negative, Boundary, và Edge cases
- Mỗi trường input phải có validation TCs riêng (không gộp nhiều trường vào 1 TC)
- TC ID theo format thống nhất do user quy ước hoặc mặc định `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`
- Nếu quá nhiều TCs → chia thành Part 1, Part 2 và hỏi user
