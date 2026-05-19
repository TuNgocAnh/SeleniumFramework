# Selenium Test Framework

> 🌐 **Vietnamese version:** [README.md](README.md) — _When editing this file, please update `README.md` to keep both in sync._

A web test automation framework built with **Java 21 + Maven + TestNG**, following the **Page Object Model (POM)** pattern. Supports cross-browser execution, parallel runs, data-driven testing (JSON/Excel), Selenium Grid, mobile emulation, Extent + Allure reporting, file logging, and encrypted credentials.

---

## 1. Requirements

| Component   | Version |
|-------------|---------|
| JDK         | 21 |
| Maven       | 3.9+ |
| Browsers    | Chrome / Edge / Firefox (pre-installed) |
| Allure CLI  | (optional — to view Allure web report) |

Quick check:
```powershell
java -version
mvn -version
```

---

## 2. Project Structure

```
SeleniumFramework/
├─ src/
│  ├─ main/java/com/selenium/framework/
│  │  ├─ config/        # ConfigReader, CredentialsManager, FrameworkConstants
│  │  ├─ driver/        # DriverFactory (Chrome/Edge/Firefox, Grid, mobile)
│  │  ├─ pages/         # BasePage + Page Objects (LoginPage, ProductsPage…)
│  │  ├─ healing/       # HealableElement + strategies (self-healing locator)
│  │  ├─ listeners/     # TestListener, RetryAnalyzer, RetryListener
│  │  ├─ reports/       # ExtentManager, ExtentTestManager, ReportRetention
│  │  ├─ utils/         # Wait/Screenshot/Excel/Json/Crypto/Assertions
│  │  └─ exceptions/
│  └─ test/
│     ├─ java/com/selenium/tests/   # Test classes (BaseTest, LoginTests, HealingDemoTests…)
│     └─ resources/
│        ├─ config/      # config.properties, dev/stg/prod, credentials, log4j2.xml
│        └─ testdata/    # login_data.json/.xlsx — can be split by env: testdata/<env>/
├─ docker/                 # Dockerfile + docker-compose.yml (Selenium Grid)
├─ .claude/                # Claude Code AI assets (skills, commands, rules, workflows…)
├─ .github/workflows/      # CI pipeline
├─ testng.xml              # default suite
├─ testng-smoke.xml        # smoke only
├─ testng-parallel.xml     # parallel execution
├─ Makefile                # short aliases (make smoke, make grid-up, …)
├─ .mcp.json               # Playwright MCP config (Claude Code DOM inspect)
├─ CLAUDE.md               # Claude Code agent guidance
└─ pom.xml
```

---

## 3. Quick Start

### Option 1 — Use `make` (recommended)

```bash
make help            # list all commands
make doctor          # check environment (Java/Maven/Docker/Chrome)
make smoke           # run smoke suite
make test-one T=LoginTests#loginSuccess
make grid-up && make grid-test && make grid-down
```

> **Windows note:** Use **Git Bash** (bundled with Git for Windows) — `make` does not work in cmd/PowerShell.

### Option 2 — Maven directly

```powershell
# Run default suite (testng.xml)
mvn clean test

# Run smoke group only
mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

# Run in parallel
mvn test "-Dsurefire.suiteXmlFile=testng-parallel.xml"
```

### Runtime Parameters

```powershell
# Change browser + environment + headless mode
mvn test "-Dbrowser=edge" "-Denv=stg" "-Dheadless=true"

# Run against Selenium Grid
mvn test "-DgridUrl=http://localhost:4444/wd/hub"

# Pin browser version (WebDriverManager downloads the exact version)
mvn test "-DchromeVersion=120"

# Chrome mobile emulation
mvn test "-DmobileEmulation=true" "-DmobileDevice=Pixel 7"
```

### Test Groups (TestNG)

| Group        | Purpose                              |
|--------------|--------------------------------------|
| `smoke`      | Fast suite, runs on every PR         |
| `regression` | Full suite, runs nightly             |

---

## 4. Configuration (`src/test/resources/config/config.properties`)

| Key | Default | Description |
|-----|---------|-------------|
| `browser` | chrome | chrome / edge / firefox |
| `headless` | false | enable headless mode |
| `baseUrl` | — | application base URL |
| `explicitWait` | 15 | seconds — element wait |
| `pageLoadWait` | 30 | seconds — page load wait |
| `scriptTimeout` | 30 | seconds — JS wait |
| `retryCount` | 2 | retry count on test failure |
| `reportRetentionDays` | 7 | days to keep old reports |
| `gridUrl` | — | Selenium Grid URL (empty = local) |
| `chromeVersion` / `edgeVersion` / `firefoxVersion` | — | pin browser version |
| `mobileEmulation` | false | enable mobile emulation (Chrome) |
| `mobileDevice` | Pixel 7 | emulated device name |

> Any key can be overridden via `-Dkey=value` when running `mvn`.

Environment-specific files: `dev.properties`, `stg.properties`, `prod.properties` — select with `-Denv=stg`.

---

## 5. Credentials Management

`src/test/resources/config/credentials.properties` stores user/password by alias. Three password formats are supported:

| Format | Example | When to use |
|--------|---------|-------------|
| Plaintext | `secret123` | DEV only |
| Base64 obfuscation | `b64:c2VjcmV0MTIz` | avoid direct exposure |
| AES-256/GCM | `enc:...` | production — requires `CRED_KEY` env var |

Generate an encrypted string:
```powershell
mvn -q compile
$env:CRED_KEY="your-secret-key"
java -cp target/classes com.selenium.framework.utils.CryptoUtils encrypt "password"
```

---

## 6. Reports & Logs

- **ExtentReport** — `reports/ExtentReport_<timestamp>.html` (open directly in browser)
- **Allure** — results in `target/allure-results/`, view as web:
  ```powershell
  mvn allure:serve
  ```
- **Screenshots** on test failure are attached to both Extent and Allure
- **Logs** — `logs/automation.log` (INFO by default; click/type at DEBUG level)
- Reports older than `reportRetentionDays` (default 7 days) are auto-cleaned at suite start

---

## 7. Self-Healing Locator

When the primary locator fails (e.g., the FE team changes an `id` or class), the framework automatically tries **fallback strategies** based on other characteristics of the element (visible text, role + aria-label, fuzzy attribute match...). If a strategy finds **exactly one** candidate, that element is used so the test still PASSes, while the heal event is logged as WARNING and attached to the Allure report so developers can fix the original locator.

```java
HealableElement loginBtn = HealableElement.builder()
    .primary(By.id("login-button"))
    .fallback(ByTextStrategy.of("Login"))
    .fallback(ByRoleStrategy.of("button", "Login"))
    .fallback(ByAttributeContainsStrategy.of("class", "submit"))
    .build();

loginBtn.findWithWait().click();  // recommended for most cases
```

### Strategies

| Strategy | When to use |
|----------|-------------|
| `ByTextStrategy` | Element has stable visible text |
| `ByRoleStrategy` | Button/link/textbox with stable `aria-label` or accessible name |
| `ByAttributeContainsStrategy` | Last resort — fuzzy match on part of an attribute (e.g., `class*='submit'`) |

### Two lookup modes

| Method | Behavior | When to use |
|--------|----------|-------------|
| `find()` | Try primary + fallbacks **once** (~40ms), throws on failure | DOM is guaranteed to be ready (immediate check) |
| `findWithWait(int seconds)` | Polls every 500ms, throws on timeout | DOM is still rendering / after navigation |
| `findWithWait()` | Same as above, uses `explicitWait` from config (default 15s) | **Default for 90% of cases** |

> **Philosophy:**
> - Healing only kicks in when a fallback finds **exactly one** element → no accidental clicks.
> - Healing does **not** replace explicit wait — it runs inside the wait. Each poll tick tries both primary and fallbacks, so whichever appears first wins. No wasted time.
> - Every heal event is logged at WARNING level and attached to Allure, so developers never silently miss a broken locator.
>
> See `src/test/java/com/selenium/tests/HealingDemoTests.java` for a runnable demo.

---

## 8. Soft Assertion

Use when checking multiple things in one test without failing on the first assertion:

```java
Assertions.soft().assertEquals(actual, expected, "msg");
Assertions.soft().assertTrue(condition);
Assertions.assertAll(); // call at end of test to aggregate results
```

---

## 9. MCP — Inspect real DOM from Claude Code

The repo ships with a **Playwright MCP server** preconfigured in `.mcp.json`. Slash commands like `/locator-gen` can drive a real browser, capture the DOM accessibility tree, click/type — no more guessing locators.

**One-time setup:**

```powershell
npx playwright install chromium
```

Then reload Claude Code (`Ctrl+Shift+P` → "Developer: Reload Window"). The `mcp__playwright__browser_*` tools become available to the agent.

**Quick test:**

```
/locator-gen "Add to cart" button on https://www.saucedemo.com
```

The agent will navigate, inspect the real DOM, and return a locator + healable fallbacks based on actual attributes.

---

## 10. Docker — Selenium Grid & Test Runner

The repo ships two Docker workflows (see `docker/README.md` for details):

**Grid** — cross-browser parallel execution without installing Chrome/Firefox locally:

```powershell
docker compose -f docker/docker-compose.yml up -d
mvn test "-DgridUrl=http://localhost:4444/wd/hub"
docker compose -f docker/docker-compose.yml down
```

→ Grid console at http://localhost:4444. Default: 1 chrome node (4 sessions) + 1 firefox node (2 sessions). Scale with `--scale chrome=3`.

**Test runner image** — packages Java + Maven + Chrome + code into one image:

```powershell
docker build -t selenium-framework -f docker/Dockerfile .
docker run --rm -v "$(pwd)/target:/app/target" selenium-framework
```

→ Convenient for CI or reviewers who want to try the framework without local setup.

---

## 11. CI (GitHub Actions)

Workflow: `.github/workflows/ci.yml` — runs headless on `push` / `pull_request`, uploads Allure + Extent + logs as artifacts.

> If using `enc:` passwords, add `CRED_KEY` to repo secrets.

---

## 12. Tips for Writing New Tests

1. Create a Page Object in `src/main/java/com/selenium/framework/pages/`, extending `BasePage`.
2. Create a test class in `src/test/java/com/selenium/tests/`, extending `BaseTest`.
3. Annotate with `@Test(groups = {"smoke"})` or `"regression"` to assign a group.
4. Data-driven: place JSON/Excel files in `src/test/resources/testdata/`, read via `JsonUtils` / `ExcelUtils`.
5. Run locally: `mvn test -Dheadless=false` to watch the browser open.
