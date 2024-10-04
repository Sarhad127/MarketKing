

FROM gradle:jdk17

COPY ./ ./

RUN gradle build

RUN mv ./build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar


FROM eclipse-temurin:17-jdk-jammy

COPY --from=builder /app.jar /app.jar


EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]