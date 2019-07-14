FROM openjdk:11-jre-slim
MAINTAINER David Perez Cabrera, dperezcabrera@gmail.com

ADD /target/test-1.0.0-SNAPSHOT.jar /
ADD /logback.xml /conf/
ENTRYPOINT ["java", "-jar", "/test-1.0.0-SNAPSHOT.jar", "--logging.config=/conf/logback.xml"]
