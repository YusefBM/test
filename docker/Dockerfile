FROM openjdk:11-jre-slim
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

ADD /target/${PROJECT_BINARY} /
ADD /src/main/resources/application*.properties /conf/

ENTRYPOINT ["java", "-jar", "/${PROJECT_BINARY}", "--spring.config.location=/conf/"]
