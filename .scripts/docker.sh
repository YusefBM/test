#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

. ./.scripts/env.docker.sh

mvn clean package docker:build

/usr/local/bin/docker-compose -f ./.scripts/docker-compose.yml up

# /usr/local/bin/docker-compose -f ./.scripts/docker-compose.yml down
# docker exec -it postgres psql --username postgres --dbname test