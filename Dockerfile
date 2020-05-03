FROM openjdk:14-jdk-alpine
RUN addgroup -S wefox && adduser -S wefox -G wefox
USER wefox:wefox
COPY build/libs/*.jar app.jar
COPY scripts/wait-for-it.sh wait-for-it.sh
CMD ["java","-jar","/app.jar"]