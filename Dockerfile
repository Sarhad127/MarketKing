# Stage 1: Build the application using Gradle
FROM gradle:jdk17 AS builder

# Set the working directory inside the Docker container
WORKDIR /app

# Copy only necessary Gradle files first to cache dependencies
COPY gradle ./gradle
COPY build.gradle settings.gradle gradlew ./

# Set execute permission for gradlew
RUN chmod +x gradlew

# Download dependencies (helps with caching)
RUN ./gradlew build --no-daemon --stacktrace || return 0

# Copy the rest of the application files
COPY . .

# Build the application to create the jar file
RUN ./gradlew clean build --no-daemon

# Stage 2: Use a lightweight image to run the application
FROM eclipse-temurin:17-jdk-jammy

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app.jar"]
