FROM openjdk:11 as builder
MAINTAINER Raj Shah
RUN mkdir -p /app/source
COPY . /app/source
WORKDIR /app/source
RUN mvn clean install 

FROM openjdk:11 as final_build
COPY --from=builder /app/source/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
