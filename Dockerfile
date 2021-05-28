FROM openjdk:11.0.8-jre-slim-buster as builder
MAINTAINER Raj Shah
RUN mkdir -p /app/source
COPY . /app/source
WORKDIR /app/source
RUN ./mvnw clean install 

FROM openjdk:11.0.8-jre-slim-buster as final_build
COPY --from=builder /app/source/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
