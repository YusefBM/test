FROM maven:slim AS builder
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

COPY . /app/
WORKDIR /app/
RUN mvn package
RUN mv target/*.jar target/spring-boot-app.jar

FROM openjdk:11-jre-slim
EXPOSE 8080
COPY --from=builder /app/target/spring-boot-app.jar /app/
WORKDIR /

ENTRYPOINT ["java", "-jar", "/app/spring-boot-app.jar"]
