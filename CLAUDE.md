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
- `.agent/` — Claude Code skills, workflows, plans, prompt templates

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

## .agent Folder

This repo includes Claude Code skills under `.agent/skills/` (kebab-case names — see folder names). Skill `name:` frontmatter must match folder name and contain only lowercase letters, numbers, and hyphens. Workflows referenced by skills live in `.agent/workflows/`.
