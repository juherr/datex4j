#!/usr/bin/env bash
set -euo pipefail

: "${GPG_PASSPHRASE_FILE:?GPG_PASSPHRASE_FILE must point to a protected passphrase file}"

exec gpg \
  --batch \
  --pinentry-mode loopback \
  --passphrase-file "${GPG_PASSPHRASE_FILE}" \
  "$@"
