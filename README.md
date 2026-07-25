# datex4j

**The reference Java SDK for the European [DATEX II](https://datex2.eu) standard.**

datex4j aims to be to DATEX II what Jackson is to JSON: a robust, modular, production-ready and
easy-to-use library that stays out of your way. It is a reusable SDK — not an application — and
carries no framework dependencies.

- **Java 25**, JPMS-compatible, no Spring, no framework coupling.
- **100% generated model** from the official DATEX II XML Schemas — reproducible, never hand-edited.
- **Small, JAXB-hiding API** for reading, writing, validating and pretty-printing DATEX II XML.
- **Apache-2.0** licensed, Semantic Versioning, reproducible builds.

> Status: `0.1.0-SNAPSHOT`. The goal of 0.1 is a solid long-term architecture, not full coverage of
> the DATEX II specification. The public API is intended to remain stable.

## Modules

**Core**

| Module            | Description                                                                      |
|-------------------|----------------------------------------------------------------------------------|
| `datex4j-model`   | Aggregate that pulls in every `datex4j-model-vX_Y` module (v2.0–v2.3 and v3.0–v3.7) transitively. |
| `datex4j-model-vX_Y` | One module per DATEX II version (`datex4j-model-v2_0` … `datex4j-model-v2_3`, `datex4j-model-v3_0` … `datex4j-model-v3_7`): its own XSDs, JAXB generation and `DatexModelProvider`. Depend on one directly to bundle a single version. |
| `datex4j-model-spi` | The `DatexModelProvider` SPI the facades discover through `ServiceLoader`.         |
| `datex4j-core`    | Framework-free utilities: version, namespace and schema-resource constants.       |
| `datex4j-xml`     | Marshalling, unmarshalling, schema validation and formatting — hides JAXB.         |
| `datex4j-json`    | JSON (de)serialization of the model (Jackson, honours the JAXB annotations).       |
| `datex4j-validation`| Structured, error-collecting validation against the official XSDs.               |
| `datex4j-builders`| Fluent `PublicationBuilder` foundation shared by the domain modules.               |
| `datex4j-location`| Cross-cutting helpers for DATEX II location referencing.                          |
| `examples`        | Runnable end-to-end examples (not published).                                     |

**Domain modules** — one per official [DATEX II user domain](https://datex2.eu/user-domains/), each
a fluent builder on top of the generic model:

| Module               | User domain                          | Primary publication                    |
|----------------------|--------------------------------------|----------------------------------------|
| `datex4j-domain-traffic`    | Traffic Management                   | `SituationPublication`                 |
| `datex4j-domain-srti`       | Safety Related Traffic Information    | `SituationPublication`                 |
| `datex4j-domain-parking`    | Parking                              | `ParkingTablePublication`              |
| `datex4j-domain-evcharging` | EV Charging (energy infra., AFIR)    | `EnergyInfrastructureTablePublication` |
| `datex4j-domain-uvar`       | Urban Vehicle Access Regulations     | `ControlledZoneTablePublication`       |

Domain builders currently target the default DATEX II version (3.7).

**Integrations**

| Module         | Description                                                                        |
|----------------|------------------------------------------------------------------------------------|
| `datex4j-ocpi` | Generated OCPI 2.3.0 model plus a bidirectional OCPI ↔ DATEX II mapping (Energy Infrastructure). |

Bundled DATEX II versions: the full v3 family **3.0, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6 and 3.7**
(default 3.7). Each is generated side by side into version-scoped packages
(`dev.juherr.datex4j.model.v3_0.*` … `...v3_7.*`); pick one per marshaller via
`DatexXml.builder().version(...)`. Each version publishes only the modules reachable from its root
schema, and that set changes across minors (for example the Parking module first appears in v3.3,
and v3.5 predates ControlledZone, TrafficRegulation and the MessageContainer family), so a given
version's model contains only the modules that version defines.

## Requirements

- **Java 25** (managed via [mise](https://mise.jdx.dev): `mise install`).
- Maven — the bundled wrapper pins the version; just use `./mvnw`.

## Build

```bash
mise install     # provisions Java 25 (Maven comes from the wrapper)
./mvnw verify    # builds, generates the model, runs Spotless/Checkstyle/JaCoCo and all tests
```

## Example

```java
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;

// A single marshaller is immutable and thread-safe; share it across your application.
DatexMarshaller marshaller = DatexXml.builder().validating(true).build();

SituationPublication publication = new SituationPublication();
publication.setLang("en");
publication.setModelBaseVersion("3");
// ... populate the publication ...

// Write — the publication is wrapped in the DATEX II <payload> root element automatically.
byte[] xml = marshaller.write(publication);

// Read — parse a document back into a typed publication.
SituationPublication restored = marshaller.read(xml, SituationPublication.class);
```

Run the bundled example (installs the modules locally first, then runs it):

```bash
./mvnw -q -pl examples -am install -DskipTests
./mvnw -q -pl examples exec:java
```

## Architecture

The project deliberately separates the **generated model** from **hand-written support code** so
that upgrading to a newer DATEX II release is mostly automatic: drop in the new schemas, regenerate,
and the model updates without touching any logic. See [docs/architecture.md](docs/architecture.md)
and the [version-upgrade runbook](docs/version-upgrade.md). For AFIR (EV charging) specifically, see
the [AFIR / NAP knowledge base and integration-test suite](docs/afir/README.md). For open DATEX II
data sources to test the library across all domains, see
[docs/datex-test-data-sources.md](docs/datex-test-data-sources.md).

## Roadmap

Already delivered: multi-version support (the full v3 family 3.0 through 3.7), fluent builders (`datex4j-builders`), one
module per official DATEX II user domain (traffic, SRTI, parking, EV charging, UVAR), a cross-cutting
location module, JSON serialization (`datex4j-json`), a structured validation API
(`datex4j-validation`), the generated OCPI 2.3.0 model (`datex4j-ocpi`) and a bidirectional OCPI ↔
DATEX II charging-infrastructure mapping.

Still planned but **not yet implemented**:

- Richer per-domain builders and helpers (deeper coverage of each user domain, DATEX II profiles).
- Multi-version domain builders (domain modules currently target the default version).
- A profile/extension mechanism.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), especially the rules on generated sources and the model
regeneration process.

## License

Apache License 2.0 — see [LICENSE](LICENSE).

DATEX II schemas are © the DATEX II programme and are redistributed here for code generation and
validation; see <https://datex2.eu>.
