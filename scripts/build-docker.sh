#!/usr/bin/env bash

set -euo pipefail

docker run -v `pwd`:`pwd` -w `pwd` --network host -i -t openjdk:14-jdk ./gradlew clean build check test -Dorg.gradle.parallel=false

docker build -t "wefox-centralized-payments" .