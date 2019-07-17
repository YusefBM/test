#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.docker.sh

docker-compose -f docker/docker-compose.yml down

docker rmi dperezcabrera/test:latest
