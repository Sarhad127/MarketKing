# Stage 1: Build the application using Gradle with JDK 21
FROM gradle:jdk17 AS builder

# Set the working directory inside the Docker container
WORKDIR /app

# Copy the necessary files for Gradle to cache dependencies first
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle gradlew ./

# Make sure gradlew has execute permissions
RUN chmod +x gradlew

# Download dependencies (this helps cache dependencies to speed up future builds)
# '|| return 0' allows the build to continue even if the build fails (helps with dependency resolution failures)
RUN ./gradlew clean build --no-daemon --info --stacktrace


# Copy the rest of the application files
COPY . .

# Build the application and generate the jar file
RUN ./gradlew clean build --no-daemon

# Stage 2: Run the application using Amazon Corretto 21
FROM eclipse-temurin:17-jdk-jammy

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app.jar"]
