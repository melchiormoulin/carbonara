#!/usr/bin/env bash

PROJECT_ROOT="$(git rev-parse --show-toplevel)"
DOCKER_ROOT="$(dirname "$(readlink -fm "$0")")"
TAG=$(basename ${DOCKER_ROOT})

run (){
  docker run -p 7080:7080 -t ${TAG}
}

run
