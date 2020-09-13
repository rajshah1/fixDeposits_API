FROM openjdk:8-jdk-alpine as builder
MAINTAINER Raj Shah
RUN mkdir -p /app/source
COPY . /app/source
WORKDIR /app/source
RUN mvn clean package 

FROM openjdk:8-jdk-alpine as final_build
COPY --from=builder /app/source/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
