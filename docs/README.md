# datex4j documentation

datex4j serves two audiences: application developers who consume the SDK and contributors who
maintain its generated models, facades, mappings, and test data. Choose the path that matches your
goal.

## Use datex4j

1. Start with the [project README](../README.md) to add the BOM and one model artifact.
2. Read [Models and versions](guides/models-and-versions.md) to choose an aggregate or per-version
   dependency.
3. Use [XML, JSON, and validation](guides/xml-json-validation.md) for the public facades.
4. Use [Domain builders and locations](guides/domain-builders-and-location.md) for DATEX II 3.7
   convenience APIs.
5. Read [OCPI mapping](guides/ocpi-mapping.md) when converting charging infrastructure between OCPI
   2.3 and DATEX II 3.7.

The [examples module](../examples/src/main/java/dev/juherr/datex4j/examples) contains runnable,
tested sources for every guide.

## Contribute to datex4j

1. Read [CONTRIBUTING.md](../CONTRIBUTING.md) for the build, code-generation boundary, tests, and
   release process.
2. Read [Architecture](architecture.md) before changing dependencies or module responsibilities.
3. Follow [Adding or upgrading a DATEX II version](version-upgrade.md) for per-version model work.
4. Read the [integration-test guide](../datex4j-integration-tests/README.md) before adding a fixture
   or live-feed test.
5. Record user-visible changes in [CHANGELOG.md](../CHANGELOG.md).

## Standards and test data

- [AFIR / DATEX II knowledge base](afir/README.md) explains the regulation, National Access Points,
  and country-specific availability.
- [Official DATEX II resources](afir/official-datex-resources.md) links to schemas and standards
  documentation.
- [Open DATEX II data sources](datex-test-data-sources.md) provides a cross-domain compatibility
  matrix.
- [Reference documents](references/README.md) records the external specifications used by mappings.

Country pages own current access information. Fixture README files own immutable snapshot
provenance and expected test behavior.
