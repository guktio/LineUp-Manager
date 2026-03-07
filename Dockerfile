FROM eclipse-temurin:21

WORKDIR /app
COPY target/main-0.0.1-SNAPSHOT.jar main-0.0.1-SNAPSHOT.jar
EXPOSE 8081
CMD ["java", "-jar", "main-0.0.1-SNAPSHOT.jar"]
