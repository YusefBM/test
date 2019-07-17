#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.project.sh
. ${ENV_DIR}/env.docker.sh

mvn clean package

envsubst <docker/Dockerfile | docker build -t dperezcabrera/test . -f -

docker-compose -f docker/docker-compose.yml up
