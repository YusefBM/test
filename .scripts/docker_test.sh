#! /bin/bash

export PORT=5432
export DB_NAME=opoc2
export JDBC_DATABASE_URL=jdbc:postgresql://localhost:$PORT/$DB_NAME
export JDBC_DATABASE_USERNAME=postgres
export JDBC_DATABASE_PASSWORD=password
export SPRING_PROFILES_ACTIVE=production

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

docker stop test-postgres
docker run --rm --name test-postgres -p $PORT:5432 -e POSTGRES_DB=${DB_NAME} -e POSTGRES_USER=${JDBC_DATABASE_USERNAME} -e POSTGRES_PASSWORD=${JDBC_DATABASE_PASSWORD} postgres:11.3-alpine 2>&1 1>.scripts/postgres.log &

java -jar target/test-1.0.0-SNAPSHOT.jar

docker stop test-postgres

# debug
# docker exec -it test-postgres psql --username postgres --dbname opoc2
