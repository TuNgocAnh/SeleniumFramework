# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- REST Assured API testing utilities (`framework.api.ApiClient`, `BaseApi`) and `apiBaseUrl` / `apiTimeoutMs` config keys.
- Spotless Maven plugin (Google Java Format) for code style enforcement.
- JaCoCo coverage plugin wired into Surefire — run `mvn verify` to produce `target/site/jacoco/`.
- Soft-assertion convenience methods on `framework.utils.Assertions` (`assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `fail`).
- Slack notifier step in CI workflow (triggers on failure when `SLACK_WEBHOOK_URL` secret is set).
- `CHANGELOG.md` (this file).

## [1.0.0] - 2026-05-22

### Added
- Initial Selenium framework (Java 21 + Maven + TestNG) with Page Object Model.
- Cross-browser support (Chrome / Edge / Firefox), parallel execution, Selenium Grid, mobile emulation.
- Data-driven testing via JSON and Excel; encrypted credentials via `CredentialsManager`.
- Extent + Allure reporting, log4j2 logging.
- Retry mechanism (`RetryAnalyzer` + `RetryListener` registered via `META-INF/services`).
- Healing locator strategy (`framework.healing`).
- Docker support (Selenium Grid + test runner image), Makefile with doctor/grid commands.
- Login locator refinement via MCP + learning notes (`docs/learning-notes.md`).
