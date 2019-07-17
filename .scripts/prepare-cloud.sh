#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

export ENV_DIR=${ENV_DIR:-.env}

. ${ENV_DIR}/env.cloud.sh

export DOCKER_COMPOSE_VERSION=1.24.1

chmod 400 "$ENV_DIR/$PEM_FILE_PATH"
ssh -i "$ENV_DIR/$PEM_FILE_PATH" $CLOUD_USER@$CLOUD_HOST << EOF
	sudo yum update -y
	sudo yum install docker -y
	sudo service docker start
	sudo usermod -aG docker $CLOUD_USER
	sudo chmod 666 /var/run/docker.sock
	sudo curl -L https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-`uname -s`-`uname -m` | sudo tee /usr/local/bin/docker-compose > /dev/null
	sudo chmod +x /usr/local/bin/docker-compose
	sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
	docker -v
	docker-compose -v
EOF
