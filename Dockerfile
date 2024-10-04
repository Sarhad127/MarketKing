# Stage 1: Build the application using Gradle
FROM gradle:jdk17 AS builder

# Set the working directory inside the Docker container
WORKDIR /app

# Copy only the necessary files for Gradle to cache dependencies first
COPY gradle ./gradle
COPY build.gradle settings.gradle gradlew ./

# Download dependencies (this step helps in caching dependencies and speeds up future builds)
RUN ./gradlew build --no-daemon --stacktrace || return 0

# Copy the rest of the application files
COPY . .

# Build the application
RUN ./gradlew clean build --no-daemon

# Stage 2: Run the application using a lightweight image
FROM eclipse-temurin:17-jdk-jammy

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app.jar"]
