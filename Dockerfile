FROM openjdk:14-jdk-alpine

ENV APPLICATION_USER java
RUN adduser -D -g '' $APPLICATION_USER

RUN apk update && apk add bash coreutils
RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY build/libs/*.jar /app/app.jar

WORKDIR /app

CMD ["java","-Dconfig.env=prod","-jar","app.jar"]