#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh

envsubst <docker/docker-compose-architecture.yml | docker-compose -f - up
envsubst <docker/docker-compose-architecture.yml | docker-compose -f - down
