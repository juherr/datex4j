# Contributing to datex4j

Thank you for improving datex4j. Contributions are licensed under Apache-2.0 and must preserve the
boundary between generated standards models and handwritten library code.

## Prerequisites

The project requires Java 25. [mise](https://mise.jdx.dev) provisions the JDK, and the committed
Maven wrapper pins Maven.

```bash
mise install
./mvnw verify
```

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
`examples` and `datex4j-integration-tests` modules verify public behavior and are not published.

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

datex4j follows Semantic Versioning and treats public types and methods as compatibility
commitments. Document additive changes in [CHANGELOG.md](CHANGELOG.md). Highlight breaking changes
before release and include migration guidance.

## Release workflow

Artifacts publish to Maven Central through the Sonatype Central Portal with the opt-in `release`
profile. The profile adds source, Javadoc, signing, and central-publishing plugins. `examples` and
`datex4j-integration-tests` are not published.

Release prerequisites remain outside the repository:

- a published GPG signing key;
- Central Portal credentials in `~/.m2/settings.xml` under server id `central`.

Release steps:

1. Run `./mvnw verify` and confirm the working tree is clean.
2. Update `CHANGELOG.md`, moving relevant `Unreleased` entries into
   `## [X.Y.Z] - YYYY-MM-DD`. Keep an empty `Unreleased` section and add comparison links for the
   release and the next development cycle.
3. Remove `-SNAPSHOT`, set the release version, and create the release tag.
4. Run `./mvnw -Prelease deploy`.
5. Review and release the deployment in the Central Portal because `autoPublish=false`.
6. Open the next `-SNAPSHOT` version and restore an `Unreleased` changelog section.
