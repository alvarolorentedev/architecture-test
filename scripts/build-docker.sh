#!/usr/bin/env bash

set -euo pipefail

./gradlew build

docker build -t "wefox-centralized-payments" .