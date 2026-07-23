# Contributing to datex4j

Thanks for your interest in improving datex4j! This guide covers the few things that are specific to
this project. By contributing you agree that your contributions are licensed under the Apache-2.0
license.

## Prerequisites

- **Java 25**, provisioned by [mise](https://mise.jdx.dev). Maven comes from the committed wrapper
  (`./mvnw`), so it is not pinned in `mise.toml`.

  ```bash
  mise install
  ```

- Build everything with the wrapper:

  ```bash
  ./mvnw verify
  ```

## Generated sources — read this first

`datex4j-model` contains **only generated code**. It is produced by XJC (Jakarta XML Binding) from
the official DATEX II XML Schemas that are vendored under
`datex4j-model/src/main/resources/META-INF/datex4j/schema/v<version>/`.

- **Never edit generated classes.** They live under `target/generated-sources/` and are not
  committed. Any manual change would be lost on the next build.
- Generated sources are excluded from Spotless and Checkstyle on purpose.
- Put hand-written logic in `datex4j-core`, `datex4j-xml` or a future dedicated module — never in
  `datex4j-model`.
- `datex4j-ocpi` is generated too: its OCPI model is produced by `openapi-generator-maven-plugin`
  from the vendored OpenAPI spec (`src/main/resources/META-INF/datex4j/ocpi/vX.Y.Z/openapi.yaml`).
  Same rule — never edit the generated OCPI classes.

### Regenerating the model

```bash
./mvnw -pl datex4j-model generate-sources
```

datex4j bundles **multiple DATEX II versions at once** (currently 3.6 and 3.7). Each version has its
own vendored schemas (`.../schema/v3.6`, `.../schema/v3.7`), its own bindings file
(`datex4j-model/src/main/xjb/bindings-v3.6.xjb`, `bindings-v3.7.xjb`) and a dedicated
`jaxb-maven-plugin` execution that generates into a version-scoped package
(`dev.juherr.datex4j.model.v3_6.*`, `...v3_7.*`). The bindings follow the
[official DATEX II JAXB guidance](https://docs.datex2.eu/v3.4/developers/):

- `underscoreBinding="asCharInWord"` to keep DATEX II's leading underscores;
- a rename of the `_namedAreaExtension` element in `LocationReferencing` to resolve a name
  collision with the abstract `NamedArea` type in `Common`;
- one clean target package per DATEX II module namespace.

### Upgrading to a new DATEX II version

See [docs/version-upgrade.md](docs/version-upgrade.md). In short: add the new schemas under a new
`v<version>` directory, point the build at it, adjust the bindings if the collision workarounds
changed, and regenerate. No hand-written code should need to change.

## Coding conventions

- Formatting is enforced by **Spotless** (Palantir Java Format + Apache-2.0 license header). Apply it
  before committing:

  ```bash
  ./mvnw spotless:apply
  ```

- **Checkstyle** enforces import hygiene, naming and a few structural rules on hand-written sources.
- Public API must carry **Javadoc**. Prefer immutability where it fits, and keep business logic free
  of framework, transport and persistence concerns.
- Write tests. New behaviour is not complete without tests; we use **JUnit** (Jupiter) as the test
  runner and **AssertJ** (`assertThat(...)`) for assertions.

## Public API stability

datex4j follows **Semantic Versioning**. Treat every public type and method as a compatibility
commitment: additive changes go in minor releases, breaking changes require a major release and a
clear note in the changelog.

## Release workflow

Artifacts are published to **Maven Central** through the Sonatype Central Portal, driven by the
opt-in `release` Maven profile (`central-publishing-maven-plugin`, plus source, Javadoc and GPG
plugins). The `examples` module is not published.

Prerequisites (kept out of the repository):

- a published **GPG key** for signing;
- **Central Portal credentials** configured as a `<server>` with id `central` in `~/.m2/settings.xml`.

Steps:

1. Ensure `./mvnw verify` is green and the working tree is clean.
2. Update the changelog, highlighting any breaking changes.
3. Set the release version (remove `-SNAPSHOT`) and tag. Reproducible-build settings (the fixed
   `project.build.outputTimestamp`) are already configured.
4. Publish:

   ```bash
   ./mvnw -Prelease deploy
   ```

   With `autoPublish=false`, review and release the deployment from the Central Portal UI.
5. Open the next `-SNAPSHOT` development version.
