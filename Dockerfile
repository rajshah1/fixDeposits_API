FROM openjdk:8-jdk-alpine as builder
MAINTAINER Raj Shah
COPY . /tmp/
WORKDIR /tmp
RUN chmod +x ./mvnw clean package 

FROM openjdk:8-jdk-alpine as final_build
COPY --from=builder ./tmp/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
