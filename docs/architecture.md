# Architecture

datex4j is a Maven multi-module SDK. It separates generated standards models from handwritten
facades and domain helpers, allowing several DATEX II versions to coexist without coupling public
APIs to one generated package tree.

## Module groups

| Group | Modules | Responsibility |
|---|---|---|
| Foundation | `datex4j-core`, `datex4j-model-spi` | Version metadata, schema paths, namespaces, and the model-provider contract |
| Generated models | `datex4j-model-v2_0` … `-v2_3`, `datex4j-model-v3_0` … `-v3_7` | One generated JAXB model, schema set, and provider per DATEX II version |
| Model aggregate | `datex4j-model` | Convenience dependency that brings every version model onto the classpath |
| Facades | `datex4j-xml`, `datex4j-json`, `datex4j-validation` | Version-aware XML/JSON conversion and structured XSD validation |
| Shared helpers | `datex4j-builders`, `datex4j-location` | Builder foundation and cross-domain location helpers |
| User domains | `datex4j-domain-traffic`, `-srti`, `-parking`, `-evcharging`, `-uvar` | DATEX II 3.7 convenience APIs for common publication types |
| Integration | `datex4j-ocpi` | Generated OCPI 2.3.0 model and OCPI ↔ DATEX II mapping |
| Dependency management | `datex4j-bom` | One project version for all published artifacts |
| Verification | `examples`, `datex4j-integration-tests` | Runnable public-API examples and real/synthetic feed verification |

The `examples` and `datex4j-integration-tests` modules are reactor participants but are not
published.

## Production dependency direction

Arrows point from a module to its production dependency:

```text
datex4j-model-vX_Y ──► datex4j-model-spi ──► datex4j-core
          ▲
          │
datex4j-model (aggregate of every version)

datex4j-xml ─────────► datex4j-model-spi + datex4j-core
datex4j-json ────────► datex4j-core
datex4j-validation ──► datex4j-xml + datex4j-core
datex4j-builders ────► datex4j-model-v3_7 + datex4j-core
datex4j-location ────► datex4j-model-v3_7

datex4j-domain-* ────► datex4j-builders + datex4j-model-v3_7
datex4j-ocpi ────────► datex4j-model-v3_7 + datex4j-location
                        + datex4j-domain-evcharging
```

Domain modules use `datex4j-xml` only in tests. Keeping that edge out of their production
dependencies prevents convenience builders from depending on a serialization technology.

The XML and JSON facades do not pull the all-version aggregate. Consumers add one or more
`datex4j-model-vX_Y` artifacts explicitly; `datex4j-consumer-tests` verifies that selecting v3.7
does not expose any other provider.

## Generated model boundary

Each `datex4j-model-vX_Y` module owns one self-contained version:

```text
datex4j-model-v3_7/
├── src/main/resources/META-INF/datex4j/schema/v3.7/  official XSDs
├── src/main/xjb/bindings-v3.7.xjb                    XJC customizations
├── src/main/java/.../spi/DatexModelProviderV37.java handwritten provider
├── src/main/resources/META-INF/services/...         ServiceLoader registration
└── target/generated-sources/jaxb/                   generated JAXB classes
```

Generated classes are never committed or edited. The provider is deliberately small but
handwritten: it supplies the generated context path, payload root types, and schema information to
version-neutral facades.

The `datex4j-model` aggregate generates no model classes. It depends transitively on every
`datex4j-model-vX_Y` artifact, making all bundled versions available to applications that need broad
coverage. Applications that handle one version can depend directly on that version artifact and
avoid the remaining generated models.

## Version discovery

`datex4j-model-spi` defines `DatexModelProvider`. Every version artifact registers one
implementation through Java `ServiceLoader`.

`datex4j-xml` and related facades discover providers at runtime. No facade contains a hard-coded
reference to a generated version package. Selecting a version follows this flow:

```text
DatexVersion
    │
    ▼
ServiceLoader<DatexModelProvider>
    │
    ▼
matching version provider
    │
    ├──► generated JAXB context
    └──► bundled schemas
```

If the requested version artifact is absent, the facade reports which
`datex4j-model-vX_Y` dependency is missing.

## DATEX II version boundaries

The reactor bundles DATEX II 2.0–2.3 and 3.0–3.7. `DatexVersion.current()` returns 3.7.

Each version contains only the modules reachable from its official root schema. Module availability
is not monotonic across minor versions. For example, Parking first appears in DATEX II 3.3, while
some MessageContainer-related modules appear, disappear, or change between releases.

The AFIR-specific `AfirEnergyInfrastructure` and `AfirFacilities` schemas are present in the 3.7
model. Older 3.x models can contain the base Energy Infrastructure model without those AFIR
extensions.

Domain builders target generated 3.7 types. The version-neutral facades work with every bundled
model provider.

## Facade boundaries

- `datex4j-xml` owns cached JAXB context creation, root-envelope handling, schema resolution,
  hardened XML parsing, marshalling, and unmarshalling. Public callers use `DatexXml` and
  `DatexMarshaller`, not JAXB types.
- `datex4j-json` owns conformant DATEX II JSON configuration and generated-model adaptations. Public
  callers use `DatexJson` and `DatexJsonMapper`.
- `datex4j-validation` wraps XSD validation in `ValidationResult` and `ValidationMessage`, preserving
  all errors instead of failing on the first one.
- `datex4j-builders`, domain modules, and `datex4j-location` create or modify model objects without
  depending on XML or JSON.
- `datex4j-ocpi` owns mapping policy between two standards. Generated OCPI types remain separate
  from handwritten mapping classes.

## JPMS strategy

`datex4j-core` has a real `module-info.java` because it has no external runtime dependencies. Other
published JARs declare stable `Automatic-Module-Name` values, including every version model. The BOM
is a POM artifact and has no module name.

This arrangement keeps artifacts usable on the module path while avoiding descriptors that would
complicate JAXB reflection and generated-source ordering. The project can revisit full descriptors
when a fully named module graph provides a concrete consumer benefit.

## Build guarantees

- The Maven wrapper and `mise.toml` pin the build tools.
- Plugin versions and `project.build.outputTimestamp` support reproducible builds.
- Spotless and Checkstyle inspect handwritten sources.
- JaCoCo measures handwritten code; generated models are excluded.
- Unit tests verify each facade and helper in isolation.
- Consumer-classpath tests ensure facades never pull unrequested model versions transitively.
- Integration tests exercise real and synthetic DATEX II 2.x/3.x XML and JSON feeds offline.
- The release profile publishes sources, Javadoc, and signed artifacts through the Central Portal.

See [CONTRIBUTING.md](../CONTRIBUTING.md) for contribution rules and
[version-upgrade.md](version-upgrade.md) for the per-version module workflow.
