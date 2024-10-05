FROM gradle:jdk17 AS builder

WORKDIR /app

COPY gradle/ ./gradle/
COPY build.gradle settings.gradle gradlew ./

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon --info --stacktrace

COPY . .

RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jdk-jammy

COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
