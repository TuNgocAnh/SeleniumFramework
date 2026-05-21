# Test runner image — Maven + JDK 21 + Chrome (headless).
# Build: docker build -t selenium-framework -f docker/Dockerfile .
# Run:   docker run --rm -v "$(pwd)/target:/app/target" selenium-framework \
#               mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

FROM maven:3.9.15-eclipse-temurin-26

# Install Chrome + headless dependencies
RUN apt-get update && apt-get install -y \
        wget gnupg ca-certificates fonts-liberation \
        libnss3 libatk-bridge2.0-0 libxkbcommon0 libgbm1 libasound2 \
        libxcomposite1 libxdamage1 libxrandr2 libxshmfence1 \
    && wget -q -O /tmp/chrome.deb \
        https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y /tmp/chrome.deb \
    && rm /tmp/chrome.deb \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Resolve dependencies in a separate layer for build cache friendliness
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copy source last so dependency layer stays cached across code changes
COPY . .

ENV MAVEN_OPTS="-Xmx2g"
ENV CHROME_BIN=/usr/bin/google-chrome

# Default command — override via `docker run ... <other mvn args>`
CMD ["mvn", "test", "-Dsurefire.suiteXmlFile=testng-smoke.xml", "-Dheadless=true"]
