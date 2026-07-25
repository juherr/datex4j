# datex4j

**A modular Java SDK for the European [DATEX II](https://datex2.eu) standard.**

datex4j reads, writes, validates, and converts DATEX II publications without exposing JAXB through
its public facades. The SDK bundles generated models for DATEX II 2.0–2.3 and 3.0–3.7, provides
helpers for common user domains, and has no application-framework dependency.

- **Java 21+**, JPMS-compatible, and framework-free.
- **Generated DATEX II models** built reproducibly from vendored official XML Schemas.
- **Small public facades** for XML, JSON, and structured validation.
- **Optional domain modules** for traffic, SRTI, parking, EV charging, and UVAR.
- **Apache-2.0** licensed and versioned as a multi-module SDK.

> Project status: `0.1.0-SNAPSHOT`. The architecture and public facades are in place, while domain
> convenience APIs and DATEX II profile support continue to grow.

## Quick start

datex4j publishes several artifacts with the same project version. Import the BOM, then add the
facade and model dependencies that your application needs.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>dev.juherr.datex4j</groupId>
      <artifactId>datex4j-bom</artifactId>
      <version>0.1.0-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>dev.juherr.datex4j</groupId>
    <artifactId>datex4j-xml</artifactId>
  </dependency>
  <dependency>
    <groupId>dev.juherr.datex4j</groupId>
    <artifactId>datex4j-model-v3_7</artifactId>
  </dependency>
</dependencies>
```

Use `datex4j-model` instead of `datex4j-model-v3_7` when one application must handle every bundled
DATEX II version. A missing version module produces an explicit error when the facade discovers the
available models through `ServiceLoader`.

```java
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;

DatexMarshaller marshaller = DatexXml.builder().validating(true).build();

SituationPublication publication = new SituationPublication();
publication.setLang("en");
publication.setModelBaseVersion("3");

byte[] xml = marshaller.write(publication);
SituationPublication restored = marshaller.read(xml, SituationPublication.class);
```

The runnable [XML example](examples/src/main/java/dev/juherr/datex4j/examples/DatexExample.java)
builds a complete publication. The [documentation index](docs/README.md) links to examples for JSON,
validation, domain builders, location helpers, and OCPI mapping.

## Version and model selection

| DATEX II family | Bundled versions | Java package pattern | Notes |
|---|---|---|---|
| 2.x | 2.0, 2.1, 2.2, 2.3 | `dev.juherr.datex4j.model.v2_X.*` | Legacy `d2LogicalModel` publications |
| 3.x | 3.0–3.7 | `dev.juherr.datex4j.model.v3_X.*` | Default: 3.7 |

Each version lives in a separate `datex4j-model-vX_Y` artifact. The `datex4j-model` aggregate pulls
in every version transitively. The AFIR-specific `AfirEnergyInfrastructure` and `AfirFacilities`
packages are part of the 3.7 model; earlier versions expose only the modules defined by their own
root schema.

Domain builders currently target DATEX II 3.7. The XML and validation facades can target any
bundled version when the corresponding model artifact is present. Conformant JSON fixtures cover
DATEX II 3.6 and 3.7.

See [Models and versions](docs/guides/models-and-versions.md) for dependency choices, version
selection, and the differences between DATEX II 2.x and 3.x.

## Modules

| Area | Modules | Purpose |
|---|---|---|
| Model | `datex4j-model-spi`, `datex4j-model-vX_Y`, `datex4j-model` | Version-neutral SPI, generated per-version models, and the all-version aggregate |
| Facades | `datex4j-xml`, `datex4j-json`, `datex4j-validation` | XML/JSON conversion and structured XSD validation |
| Helpers | `datex4j-builders`, `datex4j-location` | Shared publication builders and location helpers |
| Domains | `datex4j-domain-traffic`, `-srti`, `-parking`, `-evcharging`, `-uvar` | Convenience APIs for official DATEX II user domains |
| Integration | `datex4j-ocpi` | Generated OCPI 2.3.0 model and OCPI ↔ DATEX II mapping |
| Dependency management | `datex4j-bom` | One version for every published datex4j artifact |
| Verification | `examples`, `datex4j-integration-tests` | Runnable examples and real/synthetic feed tests; not published |

The [architecture guide](docs/architecture.md) documents the dependency direction, generated-code
boundaries, SPI discovery, and JPMS strategy.

## Documentation

- [Documentation index](docs/README.md) — user and contributor learning paths.
- [XML, JSON, and validation](docs/guides/xml-json-validation.md) — public facade usage.
- [Domain builders and locations](docs/guides/domain-builders-and-location.md) — convenience APIs.
- [OCPI mapping](docs/guides/ocpi-mapping.md) — supported mappings and known limitations.
- [AFIR / NAP knowledge base](docs/afir/README.md) — regulation, country access points, and datasets.
- [Open DATEX II test data](docs/datex-test-data-sources.md) — cross-domain source catalogue.

## Upstream standards and profiles

- [DATEX II on GitHub](https://github.com/DATEX-II-EU) — official model, profile, extension, and
  issue repositories.
- [Mobilithek AFIR DATEX II recharging profile](https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil)
  — Germany's AFIR recharging data model, guidance, releases, and FAQ.

## Build from source

The project requires Java 21 or newer. The committed Maven wrapper pins Maven, while
[mise](https://mise.jdx.dev) provisions the JDK.

```bash
mise install
./mvnw verify
```

Run the documented examples with:

```bash
./mvnw -pl examples -am test
./mvnw -q -pl examples -am install -DskipTests
./mvnw -q -pl examples exec:java
```

## Roadmap

The current roadmap focuses on richer domain builders, multi-version domain helpers, DATEX II
profiles and extensions, and deeper AFIR/OCPI conformance coverage. See
[CHANGELOG.md](CHANGELOG.md) for delivered and unreleased changes.

## Contributing

[CONTRIBUTING.md](CONTRIBUTING.md) explains generated-code boundaries, model regeneration, testing,
documentation ownership, and the release workflow.

## License

Apache License 2.0 — see [LICENSE](LICENSE).

DATEX II schemas are © the DATEX II programme and are redistributed for code generation and
validation; see <https://datex2.eu>.
