FROM openjdk:11-jre-slim
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

ADD /target/${PROJECT_BINARY} /app/

ENTRYPOINT ["java", "-jar", "/app/${PROJECT_BINARY}"]
