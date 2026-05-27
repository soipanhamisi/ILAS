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
COPY wait-for-db.sh /usr/local/bin/wait-for-db.sh

RUN chmod +x /usr/local/bin/wait-for-db.sh && chown appuser:appuser /usr/local/bin/wait-for-db.sh

EXPOSE 8081

USER appuser

ENTRYPOINT ["/usr/local/bin/wait-for-db.sh"]
