# Contributing to datex4j

Thank you for improving datex4j. Contributions are licensed under Apache-2.0 and must preserve the
boundary between generated standards models and handwritten library code.

## Prerequisites

The project requires Java 21 or newer. [mise](https://mise.jdx.dev) provisions the baseline JDK,
CI also verifies Java 25, and the committed
Maven wrapper pins Maven.

```bash
mise install
./scripts/verify.sh
```

The verification script checks GitHub Actions syntax and security, verifies the critical XML and
validation modules, tests the consumer dependency graph with an isolated Maven repository, runs
the complete Maven reactor, and prints the final coverage ratios. Its full log is written to
`target/verification/verify.log`.

Read the [architecture guide](docs/architecture.md) before changing module boundaries or adding a
dependency.

## Repository structure

datex4j generates one artifact for each bundled DATEX II version:

- `datex4j-model-v2_0` through `datex4j-model-v2_3`;
- `datex4j-model-v3_0` through `datex4j-model-v3_7`;
- `datex4j-model-spi`, which defines the version-neutral `DatexModelProvider` contract;
- `datex4j-model`, which aggregates every version artifact but generates no model classes itself.

Each version module owns its schemas, XJC bindings, generated classes, and a small handwritten
`DatexModelProviderVXY`. The provider registers through
`META-INF/services/dev.juherr.datex4j.model.spi.DatexModelProvider`, allowing the XML and JSON
facades to discover only the versions present on the classpath.

Handwritten support code lives in the facade, helper, domain, and integration modules. The
`examples`, `datex4j-consumer-tests`, and `datex4j-integration-tests` modules verify public behavior
and are not published. Consumer tests deliberately install one model version and must remain free
of the all-version aggregate.

## Generated sources

Never edit generated Java classes. Maven writes them under `target/generated-sources`, and the next
generation removes manual changes.

Each `datex4j-model-vX_Y` module contains:

- official XSDs under `src/main/resources/META-INF/datex4j/schema/vX.Y/`;
- an XJC bindings file under `src/main/xjb/`;
- the handwritten provider and `ServiceLoader` registration;
- generated JAXB classes under `target/generated-sources/jaxb/`.

Generate one model while iterating:

```bash
./mvnw -pl datex4j-model-v3_7 clean generate-sources
```

Verify the module and its prerequisites before committing:

```bash
./mvnw -pl datex4j-model-v3_7 -am verify
```

Generated sources are excluded from Spotless, Checkstyle, and JaCoCo. Provider classes are
handwritten code and remain subject to every quality gate.

### OCPI generated sources

`datex4j-ocpi` generates its OCPI 2.3.0 model from the vendored OpenAPI document under
`src/main/resources/META-INF/datex4j/ocpi/`. The generated classes also live under `target` and must
not be edited.

The mapping code under `src/main/java/dev/juherr/datex4j/ocpi/mapping/` is handwritten. Format,
review, and test it like any other public library code.

### Adding a DATEX II version

Follow [docs/version-upgrade.md](docs/version-upgrade.md). A version addition includes a new model
module, provider registration, reactor and BOM entries, aggregate dependency, tests, examples, and
documentation updates.

## Coding conventions

- Apply Spotless before committing:

  ```bash
  ./mvnw spotless:apply
  ```

- Checkstyle enforces import hygiene, naming, and structural rules on handwritten sources.
- Public APIs require Javadoc.
- Prefer immutable, thread-safe facades and keep business logic independent of frameworks,
  persistence, transport, and queues.
- Use JUnit Jupiter and AssertJ. Add or update tests before changing behavior.
- Follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) for commit
  messages. Use the `type(scope): description` structure and lowercase types such as `feat`, `fix`,
  `docs`, `test`, `refactor`, `build`, `ci`, and `chore`.
- Mark breaking changes with `!` after the type or scope and add a `BREAKING CHANGE:` footer with
  migration guidance.

## Testing

Run the narrowest relevant test first, then the full build.

```bash
./mvnw -pl datex4j-xml -am test
./mvnw -pl examples -am test
./mvnw verify
```

Real feeds live in `datex4j-integration-tests`. The committed suite runs offline; large or
credential-gated feeds remain opt-in. See
[datex4j-integration-tests/README.md](datex4j-integration-tests/README.md) for the supported
properties and fixture policy.

## Documentation

Technical documentation is written in English. Keep each fact in its designated source:

- `README.md` is the consumer landing page.
- `docs/README.md` is the documentation index.
- `docs/architecture.md` defines module boundaries and dependency direction.
- `docs/afir/nap/*.md` records current NAP access and country context.
- fixture README files record snapshot provenance and expected test behavior.
- `docs/datex-test-data-sources.md` remains a concise cross-domain catalogue.
- `CHANGELOG.md` follows [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/) and records
  notable user-visible changes rather than raw commit history.

When adding a module or DATEX II version, update the compatibility matrix, architecture guide,
version runbook, BOM, examples, and relevant data documentation in the same change.

## Public API stability

datex4j follows the [API compatibility policy](docs/api-compatibility.md). Revapi checks every
module against the latest final release during `verify`; implementation packages listed in the
policy are excluded. Validate configuration changes explicitly:

```bash
./mvnw revapi:validate-configuration
```

The allowlist in `config/revapi/accepted-differences.json` is intentionally empty, and the
`revapi.differences` transform is disabled while it remains empty. Every exception must enable the
transform, identify the exact difference, use a justification beginning with `Migration:`, and
repeat that migration in `CHANGELOG.md`. Run `scripts/verify-revapi-allowlist.sh` after any change
to the allowlist.

## Release workflow

Artifacts publish to Maven Central through the manually dispatched `Release to Maven Central`
workflow. It only accepts `main`, uses the protected `maven-central` environment, validates a local
Central bundle and an isolated Maven consumer, then publishes automatically. A separate job creates
the signed tag and GitHub Release only after Central reports `published`.

The environment must provide `CENTRAL_USERNAME`, `CENTRAL_TOKEN`, `GPG_PRIVATE_KEY`, and
`GPG_PASSPHRASE`. The corresponding public key must be available from a public keyserver.

Release steps:

1. Open and merge a release PR containing the final version, dated changelog section, and versioned
   release notes.
2. Dispatch `Release to Maven Central` from `main` with that version.
3. Verify Central resolution, the signed tag, and the GitHub Release.
4. Open a separate PR for the next `X.Y.Z-SNAPSHOT` version and an empty `Unreleased` section.

The workflow safely resumes when its signed tag or GitHub Release already exists at the same
commit. It refuses conflicting tags or releases.

Resume a failed release with `gh run rerun <run-id> --failed` rather than a new dispatch. A re-run
keeps the original commit, so the signed tag still points at the code that produced the published
artifacts. The publishing job skips the deploy when every artifact already resolves from Central,
so the re-run only replays the remaining verification, tag, and release steps.

The publishing job drives `central-publishing-maven-plugin` through the `central.autoPublish` and
`central.waitUntil` properties. The root POM binds them into an explicit plugin `<configuration>`,
which takes precedence over the plugin's own `autoPublish` and `waitUntil` user properties — those
`-D` flags are silently ignored. `Validate release inputs` asserts the effective POM resolves to
`true` and `published` before anything is uploaded.
