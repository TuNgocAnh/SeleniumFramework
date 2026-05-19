# Docker — Containerized Test Execution

Hai use case khác nhau, dùng tách biệt hoặc kết hợp:

## 1. Selenium Grid (recommend) — `docker-compose.yml`

Chạy Grid trong container, gọi từ máy host. Phù hợp khi muốn **cross-browser parallel** mà không cần cài Chrome/Firefox trên máy.

```powershell
# Khởi động Grid
docker compose -f docker/docker-compose.yml up -d

# Mở Grid console
start http://localhost:4444

# Chạy test trỏ vào Grid
mvn test "-DgridUrl=http://localhost:4444/wd/hub"

# Scale chrome node lên 3 (= 12 parallel session)
docker compose -f docker/docker-compose.yml up -d --scale chrome=3

# Tắt khi xong
docker compose -f docker/docker-compose.yml down
```

**Mặc định:** 1 chrome node (4 session) + 1 firefox node (2 session) = 6 test song song.

## 2. Test runner image — `Dockerfile`

Đóng gói **cả runtime + test code** vào 1 image. Phù hợp cho **CI** hoặc khi reviewer muốn chạy thử mà không cần cài Java/Maven.

```powershell
# Build image
docker build -t selenium-framework -f docker/Dockerfile .

# Chạy smoke suite (headless mặc định)
docker run --rm -v "$(pwd)/target:/app/target" selenium-framework

# Override suite
docker run --rm -v "$(pwd)/target:/app/target" selenium-framework \
    mvn test "-Dsurefire.suiteXmlFile=testng-parallel.xml"

# Override browser / env
docker run --rm -v "$(pwd)/target:/app/target" selenium-framework \
    mvn test "-Dbrowser=chrome" "-Denv=stg"
```

Allure/Extent reports được mount ra `target/` của host.

## 3. Kết hợp (Grid + Runner)

Chạy test runner image, trỏ vào Grid bên ngoài:

```powershell
docker compose -f docker/docker-compose.yml up -d
docker run --rm --network host -v "$(pwd)/target:/app/target" selenium-framework \
    mvn test "-DgridUrl=http://localhost:4444/wd/hub"
```

## Yêu cầu

- Docker Desktop (Windows/Mac) hoặc Docker Engine (Linux)
- Khoảng 3GB ổ đĩa cho images (`selenium/hub`, `selenium/node-chrome`, `selenium/node-firefox`, `maven`)
