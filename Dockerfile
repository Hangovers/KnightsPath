# ---- Build Stage ----
FROM gradle:jdk-21-and-24-alpine AS builder
WORKDIR /home/gradle/project

COPY build.gradle settings.gradle ./
RUN gradle --no-daemon dependencies

COPY src ./src
RUN gradle --no-daemon shadowJar

# ---- Runtime Stage ----
FROM bellsoft/liberica-openjre-alpine:21 AS runtime
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/KnightsPath-1.0-SNAPSHOT-all.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
