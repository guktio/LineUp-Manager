FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache libstdc++
WORKDIR /app
COPY target/LineUp-0.2.1.jar LineUp-0.2.1.jar
EXPOSE 8080
CMD ["java", "-jar", "LineUp-0.2.1.jar","--spring.profiles.active=dev"]
