FROM maven:alpine AS builder
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

COPY . /app/
WORKDIR /app/
RUN mvn package
RUN mv target/*.jar target/spring-boot-app.jar

FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY --from=builder /app/target/spring-boot-app.jar /app/
WORKDIR /

ENTRYPOINT ["java", "-jar", "/app/spring-boot-app.jar"]
