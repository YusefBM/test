#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.cloud.sh

echo ssh -o \"StrictHostKeyChecking no\" -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST