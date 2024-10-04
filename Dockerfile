# Använd en Java 17-bild från Eclipse Temurin som bas
FROM eclipse-temurin:17-jdk-jammy

# Sätt arbetskatalogen till /app
WORKDIR /app

# Kopiera Gradle wrapper filer först för att utnyttja Docker-caching
COPY gradlew .
COPY gradle/ gradle/

# Ge körningsrättigheter till Gradle wrappern
RUN chmod +x gradlew

# Kopiera resten av applikationsfilerna
COPY . .

# Bygg applikationen med Gradle wrapper utan att använda daemon
RUN ./gradlew build --no-daemon

# Flytta den byggda JAR-filen till /app.jar
RUN mv ./build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Exponera port 8080 för att köra applikationen
EXPOSE 8080

# Kör applikationen med Java
CMD ["java", "-jar", "/app.jar"]
