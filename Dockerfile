# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /build

# Copy only pom.xml first so Maven dependencies get cached
# (Docker layer cache: dependencies are only re-downloaded when pom.xml changes)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy the rest of the source code and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:25

# Install curl for the healthcheck below
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Non-root user for better security (the Java process shouldn't run as root)
RUN useradd --create-home --shell /bin/bash the-rettington-gardener

WORKDIR /workspace

COPY --from=build /build/target/the-rettington-gardener.jar /workspace/the-rettington-gardener.jar

# Fix ownership so the non-root user can execute the file
RUN chown -R the-rettington-gardener:the-rettington-gardener /workspace

USER the-rettington-gardener

HEALTHCHECK --interval=15s --timeout=3s --start-period=5s --retries=10 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "the-rettington-gardener.jar"]
