# Stage 1: Build the application using Gradle
FROM gradle:jdk17 AS builder

# Set the working directory
WORKDIR /app

# Copy the build files
COPY ./ ./

# Build the application
RUN gradle clean build --no-daemon

# Stage 2: Run the application using a lightweight image
FROM eclipse-temurin:17-jdk-jammy

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app.jar"]
