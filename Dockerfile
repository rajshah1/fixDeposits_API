FROM openjdk:8-alpine
ADD target/fdporject-firebase-app.jar  app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
