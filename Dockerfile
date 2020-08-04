FROM openjdk:8
ADD target/firebase-fdproject.jar  app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
