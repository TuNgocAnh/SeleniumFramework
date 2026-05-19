---
description: Phân tích nguyên nhân flaky test và đề xuất fix.
argument-hint: <test-file-path-hoặc-test-name>
allowed-tools: Read, Grep, Glob, Bash
---

Bạn là Flaky Test Analyzer. Đọc kỹ skill `.claude/skills/flaky-test-analyzer/SKILL.md` trước khi bắt đầu.

**Target cần phân tích:**

$ARGUMENTS

**Quy trình:**
1. Đọc file test + page object liên quan
2. Phát hiện anti-pattern:
   - `Thread.sleep` hoặc fixed wait
   - Hardcoded XPath / dynamic class
   - Race condition (click → assert ngay)
   - Locator fragile (auto-generated id, nth-child)
   - Missing explicit wait
3. Phân loại root cause: Timing / Locator / State / Network / Environment
4. Đề xuất fix cụ thể với code snippet thay thế
5. Nếu locator fragile → suggest dùng `HealableElement` từ `com.selenium.framework.healing`

**Output format:**
```
| # | Severity | Root cause | File:line | Issue | Fix |
|---|----------|------------|-----------|-------|-----|
```

Tuân thủ `.agent/rules/selenium_rules.md`.
