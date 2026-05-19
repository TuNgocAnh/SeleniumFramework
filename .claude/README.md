# .claude — Selenium-focused subset of antigravity-testing-kit

## Cấu trúc

- `rules/` — quy tắc bắt buộc khi AI viết code Selenium
  - `selenium_rules.md` — locator priority, cấm Thread.sleep, WebDriverWait, TestNG patterns
  - `automation_rules.md` — quy tắc automation chung
  - `locator_strategy.md` — chiến lược chọn locator
- `skills/` — vai trò AI agent
  - `framework-architect/`, `qa-automation-engineer/`
  - `smart-locator-agent/`, `locator-healer-agent/`
  - `test-data-generator/`, `flaky-test-analyzer/`
  - `ui-debug-agent/`, `requirements-analyzer/`
- `workflows/` — quy trình từng đầu việc (sinh framework, sinh script từ testcase/UI flow, sinh locator, test data, phân tích flaky…)
- `prompt_templates/` — prompt mẫu (Selenium-only)
  - `prompt_01..02` requirements & test cases
  - `prompt_03_create_framework_selenium.txt`
  - `prompt_04_generate_script_selenium.txt`
  - `prompt_05..09` convert manual→auto, test data, flaky, API tests
- `plans/automation/` — quy trình 6 bước:
  1. context_and_roleplay → 2. analysis_and_ui_recon → 3. pom_design
  → 4. test_data_strategy → 5. script_generation → 6. review_and_refactor
- `practices/` — sample **requirements** (CRM, ecommerce, Jira KAN-5) + **test cases mẫu** (.md + .xlsx) để AI tham khảo format
> Rule chung cho AI (safety, language, cleanup) đã được merge vào [`/CLAUDE.md`](../CLAUDE.md).

## Cách dùng

Khi nhờ AI sinh/sửa code Selenium trong repo này, yêu cầu AI đọc trước:
1. `.claude/rules/selenium_rules.md`
2. `.claude/rules/locator_strategy.md`
3. Skill tương ứng trong `.claude/skills/`
4. Workflow tương ứng trong `.claude/workflows/`
