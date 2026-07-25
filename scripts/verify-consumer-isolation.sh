#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
temporary_root="$(mktemp -d "${TMPDIR:-/tmp}/datex4j-consumer-isolation.XXXXXX")"
local_repository="${temporary_root}/repository"
dependency_tree="${temporary_root}/dependency-tree.txt"

cleanup() {
  rm -rf "${temporary_root}"
}
trap cleanup EXIT

cd "${project_root}"

./mvnw --batch-mode --no-transfer-progress \
  -Dmaven.repo.local="${local_repository}" \
  -DskipTests \
  -Djacoco.skip=true \
  -pl datex4j-xml,datex4j-json,datex4j-model-v3_7 \
  -am \
  install

./mvnw --batch-mode --no-transfer-progress \
  -Dmaven.repo.local="${local_repository}" \
  -f datex4j-consumer-tests/pom.xml \
  -Dscope=test \
  -DoutputType=text \
  -DoutputFile="${dependency_tree}" \
  dependency:tree

if ! grep -Eq 'dev\.juherr\.datex4j:datex4j-model-v3_7:' "${dependency_tree}"; then
  echo "Expected datex4j-model-v3_7 is missing from the consumer dependency tree." >&2
  exit 1
fi

if grep -Eq \
  'dev\.juherr\.datex4j:(datex4j-model:|datex4j-model-v(2_[0-3]|3_[0-6]):)' \
  "${dependency_tree}"; then
  echo "Unexpected generated model dependency found:" >&2
  grep -E 'dev\.juherr\.datex4j:(datex4j-model:|datex4j-model-v[23]_)' \
    "${dependency_tree}" >&2
  exit 1
fi

echo "Consumer isolation verified: only datex4j-model-v3_7 is installed."
