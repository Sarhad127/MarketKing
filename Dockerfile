FROM gradle:jdk17 AS builder

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle/
COPY build.gradle settings.gradle ./

COPY ./ ./

RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jdk-jammy

COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
