# BattleShip

<img src="./fight.png" width=200/>

![coverage](https://img.shields.io/badge/coverage-75%25-green)


## A case study: 
*A PART OF MY JAVA COURSE*
### 
*"WEB MULTIPLAYER GAME IN JAVA SPRING"*


## Technologies: 

- Java
- Spring 
- Spring Security
- Rest
- Websocket/STOMP
- Webflow
- Thymeleaf
- JS
- JQuery
- Bootstrap
- MySql
- RabbitMQ (two releases with/without it)
- Logback
- Docker
- Unit Test
- Mockito
- Component test
- Coverage check (jacoco, PiiTest)



## Resourses:

[My article on Habr](https://habr.com/ru/post/346296)

## Environment (docker-compose.yaml):
```
version: '3.7'
services:
  mysql:
    image: mysql:latest
    container_name: some-mysql
    environment:
      MYSQL_ROOT_PASSWORD: bolt
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  rabbitmq:
    image: rabbitmq:3-management
    container_name: some-rabbit
    hostname: my-rabbit
    ports:
      - "5672:5672"
      - "15672:15672"
      - "15674:15674"
      - "61613:61613"
    environment:
      RABBITMQ_DEFAULT_USER: sb
      RABBITMQ_DEFAULT_PASS: sb
    command: ["rabbitmq-plugins", "enable", "rabbitmq_web_stomp", "rabbitmq_stomp"]

volumes:
  mysql-data:
```

