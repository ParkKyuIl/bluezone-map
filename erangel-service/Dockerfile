FROM gradle:8.3-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test


FROM openjdk:17-jdk-slim
COPY --from=builder /app/build/libs/erangel-service-0.0.1-SNAPSHOT.jar /app/erangel-service.jar
ENTRYPOINT ["java", "-jar", "/app/erangel-service.jar"]
