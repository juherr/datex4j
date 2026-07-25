#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <output.zip> <version>" >&2
  exit 2
fi

output=$1
version=$2
project_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
inventory="${project_root}/config/release/public-artifacts.txt"

if [[ ! "${version}" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Invalid release version: ${version}" >&2
  exit 2
fi

mkdir -p "$(dirname "${output}")"
output_directory=$(cd "$(dirname "${output}")" && pwd)
output="${output_directory}/$(basename "${output}")"

staging=$(mktemp -d)
trap 'rm -rf "${staging}"' EXIT
repository_root="${staging}/dev/juherr/datex4j"

copy_payload() {
  local source=$1
  local destination=$2

  if [[ ! -f "${source}" ]]; then
    echo "Missing built release payload: ${source}" >&2
    exit 1
  fi
  if [[ ! -f "${source}.asc" ]]; then
    echo "Missing built release signature: ${source}.asc" >&2
    exit 1
  fi

  cp "${source}" "${destination}"
  cp "${source}.asc" "${destination}.asc"
}

while read -r artifact_id packaging; do
  [[ -z "${artifact_id}" || "${artifact_id}" == \#* ]] && continue

  module_root="${project_root}/${artifact_id}"
  if [[ "${artifact_id}" == "datex4j" ]]; then
    module_root="${project_root}"
  fi
  artifact_root="${repository_root}/${artifact_id}/${version}"
  base="${artifact_id}-${version}"
  mkdir -p "${artifact_root}"

  copy_payload "${module_root}/target/${base}.pom" "${artifact_root}/${base}.pom"
  if [[ "${packaging}" == "jar" ]]; then
    copy_payload "${module_root}/target/${base}.jar" "${artifact_root}/${base}.jar"
    copy_payload \
      "${module_root}/target/${base}-sources.jar" \
      "${artifact_root}/${base}-sources.jar"
    copy_payload \
      "${module_root}/target/${base}-javadoc.jar" \
      "${artifact_root}/${base}-javadoc.jar"
  elif [[ "${packaging}" != "pom" ]]; then
    echo "Unsupported packaging '${packaging}' for ${artifact_id}" >&2
    exit 1
  fi
done < "${inventory}"

while IFS= read -r payload; do
  for algorithm in md5 sha1 sha256 sha512; do
    openssl dgst "-${algorithm}" "${payload}" | awk '{print $NF}' >"${payload}.${algorithm}"
  done
done < <(find "${repository_root}" -type f ! -name '*.md5' ! -name '*.sha1' ! -name '*.sha256' ! -name '*.sha512')

(
  cd "${staging}"
  rm -f "${output}"
  zip -q -r "${output}" dev
)

echo "Built Maven Central bundle: ${output}"
