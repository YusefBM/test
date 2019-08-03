FROM maven:alpine AS builder
LABEL maintainer="dperezcabrera@gmail.com"

COPY . /app/
RUN mvn dependency:go-offline -f /app/pom.xml
RUN mvn package -f /app/pom.xml
RUN mv /app/target/*.jar /app/target/spring-boot-app.jar

FROM openjdk:8-jre-alpine
COPY --from=builder /app/target/spring-boot-app.jar /app/

ENTRYPOINT ["java", "-jar", "/app/spring-boot-app.jar"]
