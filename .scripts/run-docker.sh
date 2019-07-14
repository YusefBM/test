#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

. ./.scripts/env.docker.sh

mvn clean package

docker build -t dperezcabrera/test -f docker/Dockerfile .

docker-compose -f docker/docker-compose.yml up

# docker-compose -f docker/docker-compose.yml down
# docker exec -it postgres psql --username postgres --dbname test