FROM openjdk:11-jre-slim
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

ADD /target/${PROJECT_BINARY} /app
ADD /src/main/resources/bootstrap.yml /config/

ENTRYPOINT ["java", "-jar", "/app/${PROJECT_BINARY}", "--spring.config.location=/config/"]
