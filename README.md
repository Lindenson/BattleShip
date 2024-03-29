# BattleShip

<img src="./fight.png" width=200/>


![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
</br>
![Coverage](.github/badges/jacoco.svg)


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

## Environment basic (docker-compose.yaml and Dockerfile):
```
version: '3.7'
services:
  mysql:
    image: mysql:latest
    container_name: some-mysql
    environment:
      MYSQL_ROOT_PASSWORD: user_pass
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
      RABBITMQ_DEFAULT_USER: user
      RABBITMQ_DEFAULT_PASS: user_pass
    command: ["rabbitmq-plugins", "enable", "rabbitmq_web_stomp", "rabbitmq_stomp"]

  java-app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: my-java-app
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/yes?createDatabaseIfNotExist=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: user_pass
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_RABBITMQ_USERNAME: user
      SPRING_RABBITMQ_PASSWORD: user_pass
    depends_on:
      - mysql
      - rabbitmq
    ports:
      - "8080:8080"

volumes:
  mysql-data:
```

```
FROM openjdk:17-alpine
WORKDIR /app
COPY BattleShip-1.jar /app/BattleShip-1.jar
CMD ["java", "-jar", "bf.jar"]
```

