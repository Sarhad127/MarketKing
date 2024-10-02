# Steg 1: Bygg steg
FROM gradle:jdk17 AS build

# Sätt arbetskatalog
WORKDIR /home/gradle/project

# Kopiera gradle wrapper och build.gradle
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Kopiera källkoden
COPY src src

# Bygg applikationen
RUN ./gradlew build --no-daemon

# Steg 2: Kör steg
FROM eclipse-temurin:17-jdk-jammy

# Kopiera den byggda JAR-filen från byggsteget
COPY --from=build /home/gradle/project/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Exponera porten som applikationen kommer att lyssna på
EXPOSE 8080

# Starta applikationen med rätt sökväg
CMD ["java", "-jar", "/app.jar"]
