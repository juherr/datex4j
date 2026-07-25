# datex4j integration tests

The integration-test module verifies datex4j against committed real-world and synthetic DATEX II
feeds. The default suite is offline and reproducible; large or access-gated feeds run only when a
local file path is supplied explicitly.

This module is part of the Maven reactor but is not published.

## Run the offline suite

```bash
./mvnw -pl datex4j-integration-tests -am test
```

Committed fixtures live under
[`src/test/resources/datasets/`](src/test/resources/datasets/README.md). Each dataset README records
its source, license, download date, DATEX version, expected object counts, and known quirks.

## Dataset catalog

[`DatasetCatalog`](src/test/java/dev/juherr/datex4j/it/support/DatasetCatalog.java) registers every
fixture exercised by the parameterized round-trip suite.

| Format | Behavior |
|---|---|
| `XML` | Parse, validate against the selected official XSD, serialize, parse again, and compare stable content |
| `XML_READ_ONLY` | Check well-formedness, parse, and preserve a stable token without claiming XSD conformance |
| `JSON` | Parse a conformant MessageContainer, serialize, parse again, and compare stable content |

Use `XML_READ_ONLY` only for a documented producer/profile incompatibility. Do not weaken an `XML`
fixture to hide a regression.

## Opt-in feeds

Large feeds are skipped unless their system property points to a downloaded file:

| Property | Test | Input |
|---|---|---|
| `datex4j.it.finland.full` | `FinlandFullFeedReadTest` | Full Fintraffic AFIR JSON feed |
| `datex4j.it.fintraffic.roadworks` | `FintrafficTrafficReadTest` | Fintraffic traffic XML |
| `datex4j.it.be.verkeerscentrum` | `BeVerkeerscentrumReadTest` | Belgian v3 traffic XML |
| `datex4j.it.ndw.roadworks` | `NdwRoadworksReadTest` | Decompressed NDW roadworks XML |
| `datex4j.it.ndw.v2measurement` | `NdwMeasurementV2ReadTest` | NDW v2 XML or `.xml.gz` |

Run one opt-in test with:

```bash
./mvnw -pl datex4j-integration-tests test \
  -Dtest=FinlandFullFeedReadTest \
  -Ddatex4j.it.finland.full=/tmp/finland-full.json
```

Country fixture READMEs contain the authoritative download commands, license notes, and observed
resource requirements.

## Add a fixture

1. Confirm that the license permits redistribution and the trimmed file is suitable for Git.
2. Add the file below `src/test/resources/datasets/<country>/`.
3. Add or update the adjacent README with the metadata template from the
   [dataset policy](src/test/resources/datasets/README.md#metadata-template).
4. Register the file in `DatasetCatalog`.
5. Add focused assertions for behavior that the generic round-trip cannot express.
6. Run the module test and `./mvnw verify`.

Current NAP access information belongs in [`docs/afir/nap/`](../docs/afir/nap/), not in fixture
metadata. The cross-domain source index belongs in
[`docs/datex-test-data-sources.md`](../docs/datex-test-data-sources.md).
