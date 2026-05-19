---
description: Sinh Page Object class theo convention của repo.
argument-hint: <tên-page> [URL để inspect]
allowed-tools: Read, Write, Grep, Glob
---

Bạn là QA Automation Engineer. Đọc kỹ:
- Skill `.claude/skills/qa-automation-engineer/SKILL.md`
- Rule `.claude/rules/automation_rules.md` + `.claude/rules/selenium_rules.md`
- Reference Page hiện tại: `src/main/java/com/selenium/framework/pages/LoginPage.java`

**Page cần sinh:**

$ARGUMENTS

**Quy trình:**
1. Phân tích flow/screen
2. Inspect DOM (nếu user cung cấp URL) — liệt kê element + locator strategy
3. Sinh Page Object **kế thừa BasePage** với:
   - Locator dạng `private final By` (không dùng `@FindBy` để giữ consistency với BasePage)
   - Method action có `@Step("...")` của Allure
   - Method return `this` để chain
   - **Locator quan trọng**: sinh kèm `HealableElement` version cho element dễ break
4. **KHÔNG** dùng `Thread.sleep` — dùng `WaitUtils` của framework
5. Lưu vào `src/main/java/com/selenium/framework/pages/<Name>Page.java`

**Template chuẩn:**
```java
package com.selenium.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class <Name>Page extends BasePage {

    private final By someInput = By.id("...");
    private final By submitBtn = By.cssSelector("...");

    @Step("Action description: {0}")
    public <Name>Page someAction(String value) {
        type(someInput, value);
        click(submitBtn);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(someInput);
    }
}
```

Sau khi sinh xong, gợi ý test class skeleton tương ứng trong `src/test/java/com/selenium/tests/`.
