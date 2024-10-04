# Byggfasen (multi-stage build)
FROM gradle:jdk20 as builder
# Sätt arbetskatalog i Docker
WORKDIR /app

# Kopiera projektfiler till containern
COPY . .

# Bygg applikationen med Gradle utan att använda daemon
RUN gradle clean build --no-daemon

# Körfasen
FROM amazoncorretto:17-alpine

# Skapa en mapp för applikationen
WORKDIR /app

# Kopiera den genererade JAR-filen från byggfasen
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Exponera porten som Spring Boot använder (8080)
EXPOSE 8080

# Ange det kommando som ska köras när containern startar
CMD ["java", "-jar", "/app/app.jar"]
