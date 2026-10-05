# syntax=docker/dockerfile:1.7

FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy the Maven descriptor first to maximize layer caching.
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copy the application sources and build the executable jar.
COPY src ./src
RUN mvn -B -DskipTests package \
    && cp "$(ls target/*.jar | grep -v 'original' | head -n 1)" /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

ENV FILE_STORAGE_UPLOAD_DIR=/data/ilas/uploads \
    SPRING_PROFILES_ACTIVE=prod

RUN mkdir -p /data/ilas/uploads \
    && useradd --create-home --uid 10001 appuser \
    && chown -R appuser:appuser /app /data/ilas/uploads

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8081

USER appuser

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -jar /app/app.jar --ILAS_SERVER_PORT=${PORT:-8081}"]
