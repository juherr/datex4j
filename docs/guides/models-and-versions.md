# Models and versions

datex4j publishes one generated model artifact per DATEX II version. Choose one version artifact for
a small classpath, or use the aggregate when an application must process several producer versions.

## Supported versions

| DATEX II family | Versions | Root shape | Package pattern |
|---|---|---|---|
| 2.x | 2.0–2.3 | `d2LogicalModel` | `dev.juherr.datex4j.model.v2_X.*` |
| 3.x | 3.0–3.7 | `payload` or Exchange 2020 `messageContainer` | `dev.juherr.datex4j.model.v3_X.*` |

`DatexVersion.current()` returns 3.7. Domain builders and location helpers target 3.7, while the XML,
XML and validation facades can select any bundled model. Conformant JSON fixtures currently cover
DATEX II 3.6 and 3.7.

AFIR-specific `AfirEnergyInfrastructure` and `AfirFacilities` packages belong to the 3.7 model.
Earlier 3.x releases can expose the base Energy Infrastructure model without those extensions.

## Choose a dependency

Use one version artifact when the producer version is controlled:

```xml
<dependency>
  <groupId>dev.juherr.datex4j</groupId>
  <artifactId>datex4j-model-v3_7</artifactId>
</dependency>
```

Use the aggregate when input can arrive in several supported versions:

```xml
<dependency>
  <groupId>dev.juherr.datex4j</groupId>
  <artifactId>datex4j-model</artifactId>
</dependency>
```

The aggregate pulls every `datex4j-model-vX_Y` artifact transitively. It does not generate another
copy of the classes.

## Select a facade version

Facades use 3.7 by default. Select another bundled version explicitly:

```java
import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;

DatexMarshaller v23 = DatexXml.builder()
        .version(DatexVersion.V2_3)
        .build();
```

Every model artifact registers a `DatexModelProvider` through `ServiceLoader`. If the requested
artifact is absent, the facade reports the missing `datex4j-model-vX_Y` dependency.

## Match producer schemas

DATEX II 3.x documents normally declare only the major version. Strict validation requires the
producer's exact minor schema/profile, even when a newer model can parse the payload. National
extensions and producer errors can also prevent validation against the plain official schema.

Use the [test-data catalogue](../datex-test-data-sources.md) to see observed compatibility for real
feeds.
