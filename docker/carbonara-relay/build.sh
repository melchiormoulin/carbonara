#!/usr/bin/env bash

PROJECT_ROOT="$(git rev-parse --show-toplevel)"
DOCKER_ROOT="$(dirname "$(readlink -fm "$0")")"
TAG=$(basename ${DOCKER_ROOT})

build (){
  docker build -t ${TAG} -f ${DOCKER_ROOT}/Dockerfile ${PROJECT_ROOT}
}

build
