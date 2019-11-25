#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh

JVM_OPTS="-XX:TieredStopAtLevel=1 -Djava.security.egd=file:/dev/./urandom "

# java $JVM_OPTS -jar target/*.jar --spring.config.location=config/ --spring.application.name=test --spring.cloud.config.uri=http://localhost:8888 
java $JVM_OPTS -jar target/*.jar --spring.application.name=test --spring.cloud.config.uri=http://localhost:8888 
