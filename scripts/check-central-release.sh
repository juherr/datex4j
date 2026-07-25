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

# Exit 0: every payload resolves. Exit 1: Central answered, something is absent.
# Exit 2: Central could not be questioned. Callers must not read 2 as "absent" —
# deploying on a transport failure would republish an already published version.
resolves() {
  local url=$1
  local http_code curl_status=0

  http_code=$(curl \
    --connect-timeout 5 \
    --max-time 10 \
    --silent \
    --head \
    --output /dev/null \
    --write-out '%{http_code}' \
    "${url}") || curl_status=$?

  if [[ "${curl_status}" -ne 0 ]]; then
    echo "Maven Central is unreachable (curl exit ${curl_status}): ${url}" >&2
    exit 2
  fi

  case "${http_code}" in
    200) return 0 ;;
    404) return 1 ;;
    *)
      echo "Unexpected HTTP ${http_code} from Maven Central: ${url}" >&2
      exit 2
      ;;
  esac
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
      echo "Not published to Maven Central: ${payload}" >&2
      exit 1
    fi
  done
done < "${inventory}"
