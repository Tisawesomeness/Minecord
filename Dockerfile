FROM gradle:6.5.0-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:15.0.1-jdk-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/minecord.jar

WORKDIR /app

COPY minecord/ /app/minecord/

ENTRYPOINT ["java", "-jar","minecord.jar"]