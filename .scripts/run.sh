#! /bin/bash

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

. ./.scripts/env.heroku.sh

java -jar target/test-*.jar
