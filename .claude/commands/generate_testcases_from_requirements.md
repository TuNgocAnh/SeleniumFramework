---
description: Sinh manual test cases từ requirement (quick mode).
argument-hint: <requirement-text-hoặc-file-path>
allowed-tools: Read, Write, Grep, Glob
---

Bạn là QA Automation Engineer. Sinh manual test cases QUICK mode (1 lượt, không qua quy trình RBT 6 bước).

**Requirement đầu vào:**

$ARGUMENTS

**Yêu cầu:**
- Áp dụng kỹ thuật: Equivalence Partitioning, Boundary Value Analysis, Decision Table
- Field-level validation cho TỪNG input field (không gộp)
- Bao gồm: Happy path, Negative, Boundary, Edge cases
- TC ID format: `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`
- Test Data **cụ thể**, không placeholder
- Output format: bảng Markdown chuẩn với cột: TC ID, Module, Test Scenario, Pre-Condition, Test Steps, Test Data, Expected Result, Priority
- Lưu kết quả vào `testcases/<module>_<timestamp>.md`

Khi xong, báo cáo: số TC đã sinh + phân loại theo Priority.
