FROM gradle:jdk17

COPY --from=build /home/gradle/project/build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

RUN mv ./build/libs/MarketKing-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app-jar"]