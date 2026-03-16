FROM eclipse-temurin:21

WORKDIR /app
COPY target/LineUp-0.2.0.jar LineUp-0.2.0.jar
EXPOSE 8080
CMD ["java", "-jar", "LineUp-0.2.0.jar","--spring.profiles.active=dev"]
