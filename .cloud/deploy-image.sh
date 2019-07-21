#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh
. ${ENV_DIR}/env.cloud.sh

export TEMP_DIR=.temp_deploy

chmod 400 "$ENV_DIR/$PEM_FILE_PATH"

ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    docker run -i --rm \
        --name test \
        --network=current_test-net \
        --env JDBC_DATABASE_URL=${JDBC_DATABASE_URL} \
        --env JDBC_DATABASE_USERNAME=${JDBC_DATABASE_USERNAME} \
        --env JDBC_DATABASE_PASSWORD=${JDBC_DATABASE_PASSWORD} \
        --env SPRING_PROFILES_ACTIVE=production,develop \
        --publish 8080:8080 \
        dperezcabrera/${PROJECT_ARTIFACT}
EOF
