#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <version>" >&2
  exit 2
fi

version=$1
project_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
inventory="${project_root}/config/release/public-artifacts.txt"
base_url=${CENTRAL_BASE_URL:-https://repo1.maven.org/maven2/dev/juherr/datex4j}

resolves() {
  local url=$1
  curl \
    --connect-timeout 5 \
    --max-time 10 \
    --fail \
    --silent \
    --show-error \
    --head \
    "${url}" >/dev/null 2>&1
}

# Mirrors the payload set asserted locally by verify-central-bundle.sh, so a
# release is only considered complete once every published file resolves.
while read -r artifact_id packaging; do
  [[ -z "${artifact_id}" || "${artifact_id}" == \#* ]] && continue

  base="${base_url}/${artifact_id}/${version}/${artifact_id}-${version}"
  payloads=("${base}.pom")
  case "${packaging}" in
    jar)
      payloads+=("${base}.jar" "${base}-sources.jar" "${base}-javadoc.jar")
      ;;
    pom) ;;
    *)
      echo "Unsupported packaging '${packaging}' for ${artifact_id}" >&2
      exit 2
      ;;
  esac

  for payload in "${payloads[@]}"; do
    if ! resolves "${payload}"; then
      echo "Not resolvable from Maven Central: ${payload}" >&2
      exit 1
    fi
  done
done < "${inventory}"
