#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh
. ${ENV_DIR}/env.cloud.sh

export TEMP_DIR=.temp_image

# Prepare private key permissions
chmod 400 "$ENV_DIR/$PEM_FILE_PATH"

# Check temporal directory
ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    if [ -d ${TEMP_DIR} ]; then
        sudo rm -rf ${TEMP_DIR}
    fi
    mkdir ${TEMP_DIR}
EOF

# Upload sources
scp -i "$ENV_DIR/$PEM_FILE_PATH" -r src pom.xml Dockerfile $CLOUD_USER@$CLOUD_HOST:${TEMP_DIR}

# Build docker image
ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    cd ${TEMP_DIR}
    docker build -t dperezcabrera/${PROJECT_ARTIFACT} .
    cd ..
    rm -rf ${TEMP_DIR}
EOF
