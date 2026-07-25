# Working in this repository

[CONTRIBUTING.md](CONTRIBUTING.md) is the source of truth for conventions, testing, and the release
workflow. This file only covers what an agent needs up front and the traps that are easy to hit.

## Verification

`./scripts/verify.sh` is the entry point. It checks workflow syntax and security, verifies the XML
and validation modules, tests the consumer dependency graph in an isolated Maven repository, runs
the full reactor, and prints coverage ratios. Its log goes to `target/verification/verify.log`.

Narrow first, then widen:

```bash
./mvnw -pl datex4j-xml -am test     # one module and its prerequisites
./mvnw spotless:apply               # before committing
./scripts/verify.sh                 # before opening a PR
```

Never pipe a build through `head` — the upstream process gets SIGPIPE and dies mid-run, and the
truncated output looks like a completed build. Redirect to a file, then grep it.

## Where facts live

Search these before experimenting to explain build behaviour; they are usually more accurate than
inline code comments.

- [Architecture guide](docs/architecture.md) — module boundaries and dependency direction.
- [API compatibility policy](docs/api-compatibility.md) — supported API, unsupported internals,
  Revapi enforcement rules.
- [Version upgrade runbook](docs/version-upgrade.md) — adding a DATEX II version.
- [Contributing guide](CONTRIBUTING.md) — release workflow, coding conventions, testing policy.

## Traps

**Generated sources.** Model classes under `target` are generated from vendored schemas and must
never be edited. Provider and mapping classes are handwritten and subject to every quality gate.

**The Revapi gate needs a SNAPSHOT ahead of the release.** The `RELEASE` baseline resolves to the
newest final version *strictly older* than the current one. On a reactor sitting at the released
version, it resolves to nothing and silently compares every module against an empty archive — a
comparison that always passes. A green build does not by itself prove the gate ran; check for
`Failed to resolve old artifacts` in the log.

**Maven plugin properties are indirected.** The release profile binds
`central-publishing-maven-plugin` through an explicit `<configuration>` reading `central.autoPublish`
and `central.waitUntil`. An explicit configuration takes precedence over a mojo's own user
properties, so the plugin's `-DautoPublish` and `-DwaitUntil` are silently ignored. When a `-D` flag
appears to have no effect, check the effective POM:

```bash
./mvnw -q -N -Prelease help:effective-pom -Doutput=/tmp/effective-pom.xml
```
