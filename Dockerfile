# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

RUN --mount=type=bind,source=app,target=. \
    --mount=type=cache,target=/app/.gradle \
    --mount=type=cache,target=/app/bin \
    --mount=type=cache,target=/app/build \
    --mount=type=cache,target=/root/.gradle <<BUILD
    set -e
    mkdir -p /build
    chmod +x ./gradlew
    ./gradlew clean installShadowDist --stacktrace --no-daemon
    cp build/libs/app-1.0-SNAPSHOT-all.jar /build/
BUILD

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app
COPY --from=builder /build/app-1.0-SNAPSHOT-all.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
