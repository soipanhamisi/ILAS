FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src src

RUN mvn -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

RUN useradd --create-home --shell /bin/bash appuser \
	&& mkdir -p /data/ilas/uploads \
	&& chown -R appuser:appuser /app /data/ilas/uploads

COPY --from=build /app/target/ILAS-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8081} -Dfile.storage.upload-dir=${FILE_STORAGE_UPLOAD_DIR:-/data/ilas/uploads} -jar /app/app.jar"]
