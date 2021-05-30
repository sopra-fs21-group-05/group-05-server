# SoPra FS21 - Group 05 Server - Pictures Game

## Project's aim
The goal of this project was to create an online version of the board game Pictures. 
The online version follows all main [Pictures game rules](https://www.riograndegames.com/wp-content/uploads/2020/04/Pictures_Rules_EN_web_1.2.pdf).
The project uses an external API [Pixabay](https://pixabay.com/api/docs/) to retrieve the images for the picture grid.
An additional feature implemented in the online version, in order to make it more challenging, is that winners of a game are restricted for the next game in terms of the material sets they get to recreate a picture.

The implementation was done as a part of the 'SoPra' course at the University of Zurich.

## Technologies used
- **Java** as the main programming language of the server
- **Gradle** for building and wrapping
- **Springboot** for running the server
- **SonarCube** for the code analysis (code smells, test coverage etc.)
- **Heroku** as the deployment platform

### Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

## Main components
The main game logic is implemented in the [**controller**](https://github.com/sopra-fs21-group-05/group-05-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs21/controller) and [**service**](https://github.com/sopra-fs21-group-05/group-05-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs21/service) classes.
The GameroomController and -Service cover all logic regarding creating, joining a gameroom and starting a game.
The GameController and -Service contain the implementation of all the rules and logic of the Pictures game.

Other important high level components are:
- [entity](https://github.com/sopra-fs21-group-05/group-05-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs21/entity): define the fields each entity type should contain
- [repository](https://github.com/sopra-fs21-group-05/group-05-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs21/repository): store entities
- [rest](https://github.com/sopra-fs21-group-05/group-05-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs21/rest): contains all DTOs (data transfer objects) and the DTO mapper that are used by the controller classes

## Launch and Deployment
### Setup this project with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

1. File -> Open... -> group-05-server
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

#### VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs21` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Deployment and Release

This project is set up to work hand-in-hand with Heroku using GitHub actions. Once a new commit is done to the repository, a deploy to Heroku will be launched. A release can simply be done by setting a tag on the GitHub repository.

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.


## Roadmap

Here is a list of features that could be implemented next:
- Shoe laces can be manipulated
- Individual material set items can be rotated
- Players can "react" to other players recreations (e.g. through likes or emojis)
- After each round, the correct solutions (recreations and corresponding grid coordinates) are displayed

## Authors and Acknowledgement

**Main contributors**:  

Michelle Reiter [@Elinriel](https://github.com/Elinriel)  
Roman Stadler [@Galva101](https://github.com/Galva101)  
Kirthan Gengatharan [@kirthan98](https://github.com/kirthan98)  
Norina Braun [@Strawberry17](https://github.com/Strawberry17)  
Ashly Kolenchery [@akolen](https://github.com/akolen)  

Special thanks to our supervisor Raphael Imfeld!

## License
Apache License

Copyright [2021] [SoPra Group 05 - Pictures]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
