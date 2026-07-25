#!/usr/bin/env bash
set -euo pipefail

project_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
allowlist="${project_root}/config/revapi/accepted-differences.json"
changelog="${project_root}/CHANGELOG.md"
pom="${project_root}/pom.xml"

if ! jq --exit-status 'type == "array"' "${allowlist}" >/dev/null; then
  echo "Revapi allowlist must be a JSON array." >&2
  exit 1
fi

entry_count=$(jq 'length' "${allowlist}")
if [[ "${entry_count}" -eq 0 ]]; then
  if ! grep -Fq '<item>revapi.differences</item>' "${pom}"; then
    echo "The empty Revapi allowlist requires the revapi.differences transform to be disabled." >&2
    exit 1
  fi
  echo "Verified empty Revapi allowlist."
  exit 0
fi

if grep -Fq '<item>revapi.differences</item>' "${pom}"; then
  echo "Enable the revapi.differences transform before accepting differences." >&2
  exit 1
fi

if ! jq --exit-status '
  length == 1
  and .[0].extension == "revapi.differences"
  and (. [0].configuration.differences | type == "array" and length > 0)
  and all(
    .[0].configuration.differences[];
    .ignore == true
    and (.code | type == "string" and length > 0)
    and (
      (.old | type == "string" and length > 0)
      or (.new | type == "string" and length > 0)
    )
    and (
      .justification
      | type == "string"
      and startswith("Migration:")
      and length > 10
    )
  )
' "${allowlist}" >/dev/null; then
  echo "Each Revapi exception must be exact, ignored explicitly, and justified with 'Migration:'." >&2
  exit 1
fi

while IFS= read -r justification; do
  if ! grep -Fq "${justification}" "${changelog}"; then
    echo "Revapi migration is missing from CHANGELOG.md: ${justification}" >&2
    exit 1
  fi
done < <(jq --raw-output '.[0].configuration.differences[].justification' "${allowlist}")

echo "Verified Revapi compatibility exceptions."
