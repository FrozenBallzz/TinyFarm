#!/usr/bin/env bash

set -euo pipefail

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven is missing from the devcontainer image." >&2
  exit 1
fi

if ! command -v sdk >/dev/null 2>&1; then
  echo "sdkman is required to install the Spring Boot CLI." >&2
  exit 1
fi

if ! command -v spring >/dev/null 2>&1; then
  sdk install springboot
fi

mvn -q -DskipTests compile
