#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
report_directory="${project_root}/target/verification"
log_file="${report_directory}/verify.log"

mkdir -p "${report_directory}"
: >"${log_file}"

run_logged() {
  local description="$1"
  shift
  echo "==> ${description}" | tee -a "${log_file}"
  if "$@" 2>&1 | tee -a "${log_file}"; then
    return
  fi
  echo "Verification failed during: ${description}" >&2
  echo "Last 80 log lines:" >&2
  tail -n 80 "${log_file}" >&2
  exit 1
}

print_coverage() {
  local module="$1"
  local report="${project_root}/${module}/target/site/jacoco/jacoco.csv"
  if [[ ! -f "${report}" ]]; then
    echo "${module}: no JaCoCo report"
    return
  fi
  awk -F, -v module="${module}" \
    'NR > 1 { missed += $8; covered += $9 }
     END {
       total = missed + covered;
       if (total == 0) {
         printf "%s line coverage: 0/0 (0.0%%)\n", module;
         exit;
       }
       printf "%s line coverage: %d/%d (%.1f%%)\n", module, covered, total, 100 * covered / total
     }' \
    "${report}"
}

cd "${project_root}"

run_logged "GitHub Actions syntax" mise exec -- actionlint -color
run_logged \
  "GitHub Actions security" \
  mise exec -- zizmor --min-severity medium .github/workflows
run_logged "Revapi allowlist policy" ./scripts/verify-revapi-allowlist.sh
run_logged \
  "critical XML and validation modules" \
  ./mvnw --batch-mode --no-transfer-progress \
  -pl datex4j-xml,datex4j-validation -am verify
run_logged "isolated consumer dependency graph" ./scripts/verify-consumer-isolation.sh
run_logged \
  "full Maven reactor" \
  ./mvnw --batch-mode --no-transfer-progress verify

print_coverage datex4j-xml | tee -a "${log_file}"
print_coverage datex4j-validation | tee -a "${log_file}"
echo "Verification succeeded. Full log: ${log_file}"
