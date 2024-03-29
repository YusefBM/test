#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.db.sh

JVM_OPTS="-XX:TieredStopAtLevel=1 -Djava.security.egd=file:/dev/./urandom "

java $JVM_OPTS -jar target/test-*.jar --spring.config.location=config/ 
