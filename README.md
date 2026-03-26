# CS2 Lineup REST API

### Built with: Spring Boot, Lombok, Hibernate, JWT, Spring Security
### Testing: Spring Boot Test, JUnit Jupiter, K6

## Overview

The CS2 Lineup REST API allows users to create, share, and explore Counter-Strike 2 lineups. It provides an easy way for players to save and showcase their strategies, videos, and related content.
## Features
  User Authentication
  Login with your Steam account
  Register and login without Steam
  Lineups Management
  Upload video and add text information for your lineup
  Automatic thumbnail generation using FFmpeg
  Save your favorite lineups with stars
  Privacy & Approval
  Users can keep their lineups private
  Admins can approve lineups for public view
  Connected to CS2 server to collect info directly from game

  ## Main workflow for application:
  1. User register/User log in/User log in via steam
  2. Go on cs2 server throw their lineup
  3. On special route user gets all lineups from the server
  4. Edit lineup, add video and publish it
  5. Admin approve lineup and now it visible for all
  6. Anothe user can star it and than use it in their game

## Requirments
Java 17+
Python 3.12+
Maven 3.8+
Docker 
FFmpeg 
Любые дополнительные Python-библиотеки для build_and_deploy.py

## Running the Project Locally

1. **Clone the repository:**

```bash
git clone https://github.com/YOUR_USERNAME/cs2-lineup-api.git
cd cs2-lineup-api
```
2. **Build the project:**
 ```bash
  mvn clean package
```
3. **Run the project:**
```bash
  mvn spring-boot:run -Dspring-boot.run.arguments="--STEAM_API_KEY=YOUR_STEAM_KEY"
```
  **Access the API documentation:**
  http://localhost:8080/swagger-ui/index.html
  
## Build a docker image
1. **Clone the repository:**
 >**Notice:** Before using the script, make sure to set up all variables according to your environment.

```bash
git clone https://github.com/YOUR_USERNAME/cs2-lineup-api.git
cd cs2-lineup-api
```
2. **Build the project:**
```bash
python build_and_deploy.py 
```
3. **Load docker image on your server**
```bash
docker load -i lineup.tar
```
4. **Run docker image**
```bash
docker run -d -p 8080:8080 -v /data/lineup/media:/app/uploads -e STEAM_API_KEY=YOUR_STEAM_API_KEY --name lineup lineup:latest
```
>**Notice:** To find Steam API key visit https://steamcommunity.com/dev/apikey 
