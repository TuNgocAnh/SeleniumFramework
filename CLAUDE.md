# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Overview

Selenium web test automation framework — **Java 21 + Maven + TestNG**, Page Object Model. Supports cross-browser, parallel, data-driven (JSON/Excel), Selenium Grid, mobile emulation, Extent + Allure reporting, and encrypted credentials.

Primary user docs:
- [README.md](README.md) — Vietnamese (primary)
- [README.en.md](README.en.md) — English mirror

## Project Structure

- `src/main/java/com/selenium/framework/` — framework code (config, driver, pages, listeners, reports, utils)
- `src/test/java/com/selenium/tests/` — test classes (extend `BaseTest`)
- `src/test/resources/config/` — `config.properties`, env-specific files, credentials, log4j2
- `src/test/resources/testdata/` — test data (JSON, Excel)
- `testng*.xml` — TestNG suites (default / smoke / parallel)
- `.claude/` — All Claude Code AI assets (skills, commands, rules, workflows, plans, practices, prompt templates)

## Common Commands

```powershell
# Run default suite
mvn clean test

# Run smoke only
mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

# Override at runtime
mvn test "-Dbrowser=edge" "-Denv=stg" "-Dheadless=true"

# View Allure report
mvn allure:serve
```

## Conventions

- New Page Objects extend `BasePage` and live under `src/main/java/com/selenium/framework/pages/`.
- New tests extend `BaseTest` and live under `src/test/java/com/selenium/tests/`.
- Use TestNG groups: `smoke` (per-PR) or `regression` (nightly).
- Read config via `ConfigReader`; do not hardcode values.
- Read credentials via `CredentialsManager` (supports plaintext / `b64:` / `enc:`).
- Data-driven tests: use `JsonUtils` or `ExcelUtils` to read from `src/test/resources/testdata/`.
- Java code style: 1 space quanh `=`, KHÔNG align cột (theo Google Java Style). Áp dụng cho khai báo locator, field, constant.

## Browser Rules (Playwright MCP)

Khi dùng Playwright MCP để inspect DOM / debug UI, **bắt buộc** theo thứ tự:

```
navigate → resize(1920×1080) → wait_for(page_load) → snapshot → interact → screenshot(on_fail)
```

- KHÔNG gọi `browser_navigate` lại nếu đã ở đúng trang (tránh reload mất state).
- LUÔN `browser_resize(1920, 1080)` ngay sau navigate — đảm bảo desktop viewport.
- Headed mode khi debug; headless chỉ dùng sau khi test PASS hoặc trong CI.
- `snapshot` để phân tích DOM (sinh locator); `screenshot` chỉ chụp khi fail hoặc milestone — không chụp tràn lan.
- Locator lấy từ snapshot phải verify trên browser hiện tại trước khi đưa vào code. **KHÔNG đoán locator.**

## Anti-Patterns (FORBIDDEN)

| ❌ | ✅ |
|---|---|
| Đoán locator / copy từ code cũ không verify | Inspect DOM thực tế qua MCP, verify trước khi dùng |
| `Thread.sleep`, fixed delay | `WebDriverWait` + `ExpectedConditions` (xem `selenium_rules.md`) |
| Test data hardcoded (`test@email.com`, `user123`) | Sinh động: prefix + timestamp + random, traceable |
| Locator dynamic class (`css-1a2b3c`), xpath tuyệt đối, auto-generated id | `id` / `data-testid` / `name` / CSS attribute / aria (xem `locator_strategy.md`) |
| Assertion không có message | Mỗi assert kèm message mô tả expected behavior |
| Test phụ thuộc thứ tự chạy / share state | Mỗi test setup/teardown riêng, độc lập |
| Commit test FAIL hoặc còn debug log | Chỉ commit khi test PASS ổn định + cleanup |

## Definition of Done

Trước khi báo cáo task automation hoàn thành, kiểm tra toàn bộ:

- [ ] **Code cleanup:** xoá `System.out.println` / debug log; xoá locator + import không dùng; không còn commented-out code.
- [ ] **Wait strategy:** không còn `Thread.sleep` hardcoded — chỉ smart waits.
- [ ] **Test data:** không hardcode email/username/ID — sinh random + traceable.
- [ ] **POM:** locator khai báo trong Page class, không inline trong test; assertion trong test, không trong page.
- [ ] **Stability:** test PASS ổn định **≥ 2 lần liên tiếp** (headed mode khi local).
- [ ] **Assertion:** mỗi test có ≥ 1 assertion với message rõ ràng.
- [ ] **Naming + structure:** file/class/method đúng convention; file đúng vị trí.
- [ ] **Report:** tóm tắt PASS/FAIL/SKIP + lý do skip + known issues (nếu có).

## Safety Rules (CRITICAL)

- Never execute destructive commands (`DROP TABLE`, `DELETE FROM` without `WHERE`, `rm -rf`, `Remove-Item -Recurse -Force`) without explicit user confirmation.
- Never print secrets (API keys, passwords, tokens, connection strings, `.env` values) into the chat.
- Never commit or push files containing real credentials. Test credentials in this repo are intentionally public demo accounts (saucedemo.com `standard_user` / `secret_sauce`) — no real secrets.
- Always re-verify before running any command that modifies or deletes data.

## Cleanup Temp & Debug Files

When generating temporary files during analysis (DOM dumps, snapshots, scratch scripts, debug output), follow this discipline:

**Patterns to clean up at end of task:**

| Pattern | Description |
|---|---|
| `*_debug.txt`, `debug_output.txt`, `*_output.txt` | Debug/output dumps |
| `*.tmp`, `*.temp` | Temp files |
| `page_snapshot.md`, `snapshot_*.md`, `dom_dump.txt`, `html_dump.html` | DOM/page snapshots |
| `console_log.txt`, `network_requests.txt` | Browser console / network dumps |
| `scratch_*.{py,js,ts,java}` | Throwaway scripts outside `src/`, `tests/`, `scripts/` |

**Never delete:**
- `target/`, `reports/`, `allure-results/`, `logs/` — official build/test outputs (already in `.gitignore`)
- Project config: `pom.xml`, `testng*.xml`, `*.properties`, `.gitignore`, `.editorconfig`
- Anything the user explicitly asked to keep

**Discipline:**
- Prefer placing scratch files in OS temp dir, not project root.
- If unsure whether a file is throwaway, ask the user before deleting.
- Report cleanup actions at end of task.

## Language

- Default to Vietnamese for explanations and conversation.
- Code (identifiers, class/method names) must be in English.
- Code comments may be English for international readability.

## Documentation Sync Rule

**When editing [README.md](README.md), also update [README.en.md](README.en.md) with the equivalent change, and vice versa.** The two files are mirrors — Vietnamese and English — and must stay in sync. If the change is content-only (text, tables), translate it accordingly; if it's structural (headings, sections), apply the same structural change to both.

## `.claude/` Folder

Mọi AI asset gom hết trong `.claude/`:

**Runtime (Claude Code auto-discover):**
- `.claude/skills/<name>/SKILL.md` — Skills (agent capabilities). Frontmatter `name:` phải khớp folder name và chỉ chứa `[a-z0-9-]`.
- `.claude/commands/<name>.md` — Slash commands. Invoke bằng `/<name>`. Dùng `$ARGUMENTS` cho user input.
- `.claude/settings.json` — Project settings (permissions, env, hooks).
- `.claude/settings.local.json` — Local overrides (gitignored).

**Reference docs (Claude chỉ đọc khi skill/command trigger):**
- `.claude/rules/` — Coding rules (Selenium, locator strategy, automation best practices).
- `.claude/plans/` — 6-step automation plan.
- `.claude/practices/` — Sample requirements và test cases.
- `.claude/prompt_templates/` — Prompt mẫu để paste vào AI tool khác (Antigravity, Gemini).

> **Lưu ý:** Toàn bộ workflow chi tiết đã được gộp vào `.claude/commands/` để gọi được bằng `/`. Không còn folder `workflows/`.

Available slash commands:

### Quick commands (sinh nhanh 1 lượt)
| Command | Purpose |
|---|---|
| `/generate_testcases_from_requirements <requirement>` | Sinh manual test cases (quick mode) |
| `/analyze_flaky_tests <test-file>` | Phân tích flaky test + đề xuất fix |
| `/generate_locator <element-desc>` | Sinh locator + healable fallback |
| `/page-gen <page-name>` | Sinh Page Object class theo convention repo |
| `/generate_test_data <feature>` | Sinh test data 4 categories (positive/negative/boundary/edge) |

### Requirements & Test Planning
| Command | Purpose |
|---|---|
| `/generate_requirements_from_website <url>` | Sinh requirements doc từ website module |
| `/analyze_requirement_document <doc-path>` | Phân tích requirement document (Jira/.doc) — không sinh TC |
| `/generate_application_test_plan <url>` | Khám phá app + sinh test plan (PLAN/FULL mode) |
| `/generate_cross_module_test_plan <feature>` | Sinh ma trận kết hợp cross-module (Pairwise) |

### Automation Generation
| Command | Purpose |
|---|---|
| `/generate_automation_framework` | Scaffold automation framework hoàn chỉnh |
| `/generate_automation_from_testcases <tc-file>` | Convert manual test cases → automation scripts |
| `/generate_automation_from_ui_flow <url+steps>` | Thực thi UI flow trên browser → sinh scripts |
| `/generate_api_tests_from_swagger <url>` | Sinh API tests từ Swagger/OpenAPI spec |
| `/generate_combinatorial_test_data <matrix-file>` | Sinh test data cho ma trận kết hợp (pipeline mode) |
