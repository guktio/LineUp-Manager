FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /build/target/LineUp-0.2.1.jar LineUp-0.2.1.jar
EXPOSE 8080

ENV STEAM_API_KEY=""

ENTRYPOINT ["java", "-jar", "LineUp-0.2.1.jar", "--spring.profiles.active=prod"]