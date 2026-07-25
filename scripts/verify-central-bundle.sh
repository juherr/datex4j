#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <central-bundle.zip> <version>" >&2
  exit 2
fi

bundle=$1
version=$2
project_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
inventory="${project_root}/config/release/public-artifacts.txt"

if [[ ! -f "${bundle}" ]]; then
  echo "Central bundle not found: ${bundle}" >&2
  exit 1
fi

staging=$(mktemp -d)
trap 'rm -rf "${staging}"' EXIT
unzip -q "${bundle}" -d "${staging}"

repository_root="${staging}/dev/juherr/datex4j"
if [[ ! -d "${repository_root}" ]]; then
  echo "Bundle does not contain dev/juherr/datex4j" >&2
  exit 1
fi

require_payload() {
  local path=$1
  if [[ ! -f "${path}" ]]; then
    echo "Missing release payload: ${path#${staging}/}" >&2
    exit 1
  fi

  if [[ ! -f "${path}.asc" ]]; then
    echo "Missing signature: ${path#${staging}/}.asc" >&2
    exit 1
  fi
  if ! gpg --batch --verify "${path}.asc" "${path}" >/dev/null 2>&1; then
    echo "Invalid signature: ${path#${staging}/}.asc" >&2
    exit 1
  fi

  local actual_checksum checksum expected_checksum
  for checksum in md5 sha1 sha256 sha512; do
    if [[ ! -f "${path}.${checksum}" ]]; then
      echo "Missing ${checksum} checksum: ${path#${staging}/}.${checksum}" >&2
      exit 1
    fi
    expected_checksum=$(openssl dgst "-${checksum}" "${path}" | awk '{print $NF}')
    actual_checksum=$(tr -d '[:space:]' <"${path}.${checksum}")
    if [[ "${actual_checksum}" != "${expected_checksum}" ]]; then
      echo "Invalid ${checksum} checksum: ${path#${staging}/}.${checksum}" >&2
      exit 1
    fi
  done
}

expected_artifacts=()
while read -r artifact_id packaging; do
  [[ -z "${artifact_id}" || "${artifact_id}" == \#* ]] && continue
  expected_artifacts+=("${artifact_id}")

  artifact_root="${repository_root}/${artifact_id}/${version}"
  base="${artifact_root}/${artifact_id}-${version}"
  require_payload "${base}.pom"

  if [[ "${packaging}" == "jar" ]]; then
    require_payload "${base}.jar"
    require_payload "${base}-sources.jar"
    require_payload "${base}-javadoc.jar"
  elif [[ "${packaging}" != "pom" ]]; then
    echo "Unsupported packaging '${packaging}' for ${artifact_id}" >&2
    exit 1
  fi
done < "${inventory}"

for forbidden in datex4j-examples datex4j-consumer-tests datex4j-integration-tests; do
  if [[ -d "${repository_root}/${forbidden}" ]]; then
    echo "Non-publishable artifact found in bundle: ${forbidden}" >&2
    exit 1
  fi
done

actual_artifacts=$(find "${repository_root}" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort)
sorted_expected=$(printf '%s\n' "${expected_artifacts[@]}" | sort)
if [[ "${actual_artifacts}" != "${sorted_expected}" ]]; then
  echo "Bundle artifact inventory differs from config/release/public-artifacts.txt" >&2
  diff -u <(printf '%s\n' "${sorted_expected}") <(printf '%s\n' "${actual_artifacts}") || true
  exit 1
fi

echo "Verified ${#expected_artifacts[@]} Maven Central artifacts for ${version}."
