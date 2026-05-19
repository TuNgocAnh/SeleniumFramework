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
- `.claude/skills/` — Claude Code skills (agent capabilities)
- `.claude/commands/` — Slash commands (invokable via `/<name>`)
- `.agent/` — Reference documentation (rules, workflows, plans, prompt templates)

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

## Documentation Sync Rule

**When editing [README.md](README.md), also update [README.en.md](README.en.md) with the equivalent change, and vice versa.** The two files are mirrors — Vietnamese and English — and must stay in sync. If the change is content-only (text, tables), translate it accordingly; if it's structural (headings, sections), apply the same structural change to both.

## Claude Code Customization

Standard Claude Code layout:

- `.claude/skills/<name>/SKILL.md` — Skills (agent capabilities). Skill `name:` frontmatter must equal folder name and use only `[a-z0-9-]`.
- `.claude/commands/<name>.md` — Slash commands. Invoke with `/<name>` in Claude Code. Use `$ARGUMENTS` for user input.
- `.claude/settings.json` — Project settings (permissions, env, hooks).
- `.claude/settings.local.json` — Local overrides (gitignored).

Available slash commands:

| Command | Purpose |
|---|---|
| `/test-gen <requirement>` | Sinh manual test cases |
| `/flaky-check <test-file>` | Phân tích flaky test + đề xuất fix |
| `/locator-gen <element-desc>` | Sinh locator + healable fallback |
| `/page-gen <page-name>` | Sinh Page Object class theo convention |

## .agent Folder (reference documentation)

`.agent/` chứa documentation tham khảo cho Claude (không auto-discover, được reference từ skills/commands khi cần):

- `.agent/rules/` — Coding rules (Selenium, locator strategy, automation best practices)
- `.agent/workflows/` — Workflow guides (bản gốc các quy trình, dùng làm reference cho slash commands)
- `.agent/plans/` — 6-step automation plan
- `.agent/practices/` — Sample requirements và test cases
- `.agent/prompt_templates/` — Prompt mẫu để paste vào AI tool khác (Antigravity, Gemini)
