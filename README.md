# kalah-rest
Kalah game REST service. Backbase coding assignment 

## Assignment details

* This web service lets 2 humans play the game, each using his own computer. There is no AI.
* Game details at https://en.wikipedia.org/wiki/Kalah. 6-stone version
* Creation of the game is performed with POST command
* Game move is performed with PUT command

## Libs used

* Java 11
* Gradle 5
* Spring Boot 2
* Lombok
* Swagger 2
* WebFlux
* MongoDB

## Build and run

Clone git repo:

```
git clone https://github.com/palazares/kalah-rest.git
```

Build application:

```
gradle build 
```

Run


```
gradle bootRun
```

## Tests

Run unit and integration tests:

```
gradle test
```

## API details

URI | HTTP Method | Content | Description
--- | --- | --- | ---
`<host>/games` | POST | -  | Create a game
`<host>/games/{gameId}/pits/{pitId}` | PUT | -  | Make a game move

