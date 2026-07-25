# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog], and this project adheres to [Semantic Versioning].

## [Unreleased]

### Added

- Generated DATEX II model artifacts for versions 2.0–2.3 and 3.0–3.7, discovered through a
  version-neutral provider SPI.
- XML, conformant JSON, and structured XSD-validation facades.
- Shared builders, location helpers, and convenience modules for traffic, SRTI, parking, EV
  charging, and UVAR.
- OCPI 2.3.0 generation and bidirectional charging-infrastructure mapping.
- Offline integration coverage for real and synthetic DATEX II 2.x/3.x XML and JSON feeds.
- Runnable, tested examples and separate user/contributor documentation paths.
- Documentation checks based on `mdbook-lint` and Lychee.
- Consumer-classpath verification for applications that install a single DATEX II model.
- Adversarial XML tests covering DTDs, external entities, and recursive entity expansion.

### Changed

- Documentation now reflects the per-version model architecture, complete version support, and
  ownership boundaries between NAP pages, source indexes, and fixture metadata.
- Java 21 is now the consumer baseline, with Java 21 and 25 both verified in CI.
- XML and JSON facades no longer pull every generated model transitively; applications select the
  version artifacts they need.
- JAXB contexts and compiled XML Schemas are reused per model version and classloader.

### Fixed

- Corrected stale v3-only statements, obsolete generated-model paths, broken links to integration
  tests, and duplicated fixture headings.
- Hardened XML reading and validation so documents containing DTDs or entity declarations are
  rejected before JAXB binding or schema validation.

[Unreleased]: https://github.com/juherr/datex4j/commits/main
[Keep a Changelog]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning]: https://semver.org/spec/v2.0.0.html
