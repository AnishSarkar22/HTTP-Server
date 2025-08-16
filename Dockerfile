FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline -B

COPY src/ src/
COPY WebRoot/ WebRoot/

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN apt-get update && apt-get install -y curl

RUN groupadd -g 1001 httpserver && \
    useradd -u 1001 -g httpserver -s /bin/bash -m httpserver

COPY --from=builder /app/target/classes/ ./classes/
COPY --from=builder /app/target/dependency/ ./lib/
COPY --from=builder /app/WebRoot/ ./WebRoot/

RUN chown -R httpserver:httpserver /app

USER httpserver

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

CMD ["java", "-cp", "classes:lib/*", "com.httpserver.HttpServer"]