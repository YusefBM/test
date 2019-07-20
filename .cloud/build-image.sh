#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh
. ${ENV_DIR}/env.cloud.sh

export TEMP_DIR=.temp_image

chmod 400 "$ENV_DIR/$PEM_FILE_PATH"
ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    if [ -d ${TEMP_DIR} ]; then
        sudo rm -rf ${TEMP_DIR}
    fi
    mkdir ${TEMP_DIR}
EOF

scp -i "$ENV_DIR/$PEM_FILE_PATH" -r src pom.xml Dockerfile $CLOUD_USER@$CLOUD_HOST:${TEMP_DIR}

ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    cd ${TEMP_DIR}
    docker build -t dperezcabrera/${PROJECT_ARTIFACT} .
    rm -rf ${TEMP_DIR}
EOF
