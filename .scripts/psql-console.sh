#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.project.sh
. ${ENV_DIR}/env.docker.sh

docker exec -it test_test-postgres_1 psql --username ${DATABASE_USERNAME} --dbname ${PROJECT_ARTIFACT}
