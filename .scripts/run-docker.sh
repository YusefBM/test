#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

. ./.scripts/env.docker.sh

mvn clean package

docker build -t dperezcabrera/test -f docker/Dockerfile .

/usr/local/bin/docker-compose -f docker/docker-compose.yml up

# /usr/local/bin/docker-compose -f docker/docker-compose.yml down
# docker exec -it postgres psql --username postgres --dbname test