#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.db.sh

java -jar target/test-*.jar --spring.config.location=config/
