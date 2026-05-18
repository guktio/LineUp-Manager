# CS2 Lineup REST API
The CS2 Lineup REST API allows users to create, share, and explore Counter-Strike 2 lineups. It provides an easy way for players to save and showcase their strategies, videos, and related content.

  ## Main workflow for application:
  1. User register/User log in/User log in via steam
  2. Go on cs2 server throw their lineup
  3. On special route user gets all lineups from the server
  4. Edit lineup, add video and publish it
  5. Admin approve lineup and now it visible for all
  6. Anothe user can star it and than use it in their game

## Tech Stack  

**Server:** Spring Boot, Lombok, JPA/Hibernate, JWT, Spring Security

**Testing:** Spring Boot Test, JUnit Jupiter, K6, Mockito
## Environment Variables  
To run this project, you will need to add the following environment variables to your .env file  
`STEAM_API_KEY` 

>**Notice:** To find your Steam API key visit https://steamcommunity.com/dev/apikey 
## Run Locally  
Clone the project  
~~~bash  
  git clone https://github.com/guktio/LineUp-Manager.git
~~~

Go to the project directory  
~~~bash  
  cd LineUp-Manager
~~~

Build
~~~bash  
  mvn clean package
~~~

Start  
~~~bash  
  mvn spring-boot:run -Pprod -Dspring-boot.run.arguments="--STEAM_API_KEY=YOUR_STEAM_KEY"
~~~ 

  
## Features  
- User Authentication
- Login with your Steam account
- Register and login without Steam
- Lineups Management
- Upload video and add text information for your lineup
- Automatic thumbnail generation using FFmpeg
- Save your favorite lineups with stars
- Privacy & Approval
- Users can keep their lineups private
- Admins can approve lineups for public view
- Connected to CS2 server to collect info directly from game
## API Reference

#### Get all lineups  

```http
  GET /api/grenades
```  

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `p` | `int` | Page number |
| `s` | `int` | Ammount of elements on page |
| `map` | `Grenade.MapType` | Map name |
| `grenade` | `Grenade.GrenadeType` | Grenade name(in game files name) |
| `sortdirection` | `string` | DESC or ASC |
| `userUuid` | `UUID` | Author uuid |
| `name` | `string` | line up name (example: "fast window","default stair" etc.) |
| `likedByUserId` | `UUID` | Liked by user(uuid) |

#### Save lineup video

~~~http
  POST /api/grenades/video
~~~

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `file`  | `bin` | **Required**. Video |

~~~http
  POST /api/grenades
~~~

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `name`  | `String` | Name of lineup |
| `command`  | `String` | **Required**. in game command (setpos;setang) |
| `map`  | `Grenade.MapType` | **Required**. map name |
| `grenadeType`  | `Grenade.GrenadeType` | **Required**. grenade name(in game files name) |
| `side`  | `String` | T,CT,Both |
| `speed`  | `String` | Speed when throwing |
| `buttons`  | `List<String>` | Buttons that were pressed |
| `media`  | `String` | **Required**. uuid of video |
| `description`  | `String` | more info about lineup |

  **To see more:**
  http://localhost:8080/swagger-ui/index.html

  [<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/44209970-39ffc588-727a-4717-9273-10b8cbaf49d4?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D44209970-39ffc588-727a-4717-9273-10b8cbaf49d4%26entityType%3Dcollection%26workspaceId%3D849b5e54-06f0-4afc-a55f-8808fe913a53)