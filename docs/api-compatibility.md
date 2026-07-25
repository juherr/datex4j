# API compatibility

datex4j uses Semantic Versioning to communicate source and binary compatibility. Before `1.0.0`,
minor releases may contain breaking changes, but every such change is announced in the changelog
with explicit migration guidance. Patch releases preserve the supported API.

## Supported API

Compatibility checks cover:

- the XML, JSON, and validation facades;
- builders, location helpers, and domain convenience APIs;
- the version-neutral model SPI;
- generated DATEX II model classes for every published model artifact;
- generated OCPI model classes and the public OCPI mapping API.

Applications may implement `DatexModelProvider` to integrate an additional DATEX II version. This
is the supported extension point. Generated models remain versioned by artifact and Java package,
so applications can migrate one model version at a time.

## Unsupported implementation details

The following types remain accessible where Java or inter-module integration requires it, but are
not compatibility commitments:

- every package named `internal` and its subpackages;
- `dev.juherr.datex4j.ocpi.support`;
- each `dev.juherr.datex4j.model.vX_Y.spi.DatexModelProviderVXY` implementation;
- `dev.juherr.datex4j.xml.SecureXmlSource`.

Do not import these types from application code. Use the public facades, mapping APIs, and
`DatexModelProvider` contract instead.

## Compatibility enforcement

Maven runs Revapi during `verify` and compares each module with its most recent final release.
Dependencies are checked in their owning modules rather than repeatedly in consumers. Before the
first release, an unresolved baseline is allowed; once `0.1.0` is available, it automatically
becomes the baseline for `0.2.0-SNAPSHOT`.

The versioned allowlist in `config/revapi/accepted-differences.json` is empty by default, so the
`revapi.differences` transform is disabled. When an exception is required, enable the transform,
add its entries to the analysis configuration, and keep the allowlist synchronized. A
compatibility exception must:

1. identify the exact Revapi difference;
2. provide a justification beginning with `Migration:`;
3. describe the same migration in `CHANGELOG.md`.

Prefer deprecation and an additive replacement before removal. When a breaking change is necessary
during `0.x`, retain the old API for at least one minor release whenever practical.
