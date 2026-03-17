FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/LineUp-0.2.1.jar LineUp-0.2.1.jar

EXPOSE 8080
CMD ["java", "-jar", "LineUp-0.2.1.jar","--spring.profiles.active=dev"]


