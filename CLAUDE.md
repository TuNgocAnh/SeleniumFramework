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
- `.claude/workflows/` — Workflow guides chi tiết (bản gốc các quy trình).
- `.claude/plans/` — 6-step automation plan.
- `.claude/practices/` — Sample requirements và test cases.
- `.claude/prompt_templates/` — Prompt mẫu để paste vào AI tool khác (Antigravity, Gemini).

Available slash commands:

| Command | Purpose |
|---|---|
| `/test-gen <requirement>` | Sinh manual test cases |
| `/flaky-check <test-file>` | Phân tích flaky test + đề xuất fix |
| `/locator-gen <element-desc>` | Sinh locator + healable fallback |
| `/page-gen <page-name>` | Sinh Page Object class theo convention |
