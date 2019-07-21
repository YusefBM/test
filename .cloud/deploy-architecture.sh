#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.architecture.sh
. ${ENV_DIR}/env.cloud.sh

export TEMP_DIR=.temp_architecture
export ARCHITECTURE_DIR=.architecture

chmod 400 "$ENV_DIR/$PEM_FILE_PATH"
ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    if [ -d ${TEMP_DIR} ]; then
        sudo rm -rf ${TEMP_DIR}
    fi
    mkdir ${TEMP_DIR}
    if [ ! -d ${ARCHITECTURE_DIR} ]; then
        mkdir ${ARCHITECTURE_DIR}
    fi
EOF

envsubst <docker/docker-compose-architecture.yml > docker/docker-compose-architecture-cloud.yml

scp -i "$ENV_DIR/$PEM_FILE_PATH" -r docker/docker-compose-architecture-cloud.yml config $CLOUD_USER@$CLOUD_HOST:${TEMP_DIR}

DEPLOY_DATE=`date +"%Y%m%d_%H%M%S"`
ssh -o "StrictHostKeyChecking no" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
    cd ${ARCHITECTURE_DIR}
    if [ -f current/docker-compose-architecture-cloud.yml ]; then
        docker-compose -f current/docker-compose-architecture-cloud.yml down
    fi
    mv ~/${TEMP_DIR} $DEPLOY_DATE
    echo "$ARCHITECTURE_VERSION">$DEPLOY_DATE/version.txt
    echo "$DEPLOY_DATE">$DEPLOY_DATE/deploy.txt
    rm -rf current
    ln -sf $DEPLOY_DATE current
    docker-compose -f current/docker-compose-architecture-cloud.yml up &
    sleep 5
EOF
