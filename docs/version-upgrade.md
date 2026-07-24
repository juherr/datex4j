# Adding or upgrading a DATEX II version

datex4j ships one Maven module per DATEX II version — `datex4j-model-v3_0` … `datex4j-model-v3_7` —
each generated into its own version-scoped package tree (`dev.juherr.datex4j.model.v3_0.*` …
`...v3_7.*`). The `datex4j-model` aggregate depends on all of them and re-exposes them transitively,
so consumers keep depending on `datex4j-model` and see every version, while a consumer that needs
just one version can depend on a single `datex4j-model-vX_Y` module. Each version module registers a
`DatexModelProvider` (from `datex4j-model-spi`) through `ServiceLoader`; the facades discover the
versions present on the classpath, with no hard-coded per-version references.

Adding a new version is additive: create a new module, generate the model, ship a provider, and
register the module in the aggregate. Existing hand-written code does not change.

The steps below use a hypothetical `3.8` as the new version.

## Steps

1. **Create the version module.** Copy an existing module directory (for example
   `datex4j-model-v3_7/`) to `datex4j-model-v3_8/` as a template. In its `pom.xml`, set the
   `<artifactId>` to `datex4j-model-v3_8`, the single `jaxb-maven-plugin` execution id/paths to the
   `v3.8` directory and `bindings-v3.8.xjb`, and the `Automatic-Module-Name` to
   `dev.juherr.datex4j.model.v3_8`.

2. **Vendor the new schemas.** Download every XSD for the target version from
   <https://docs.datex2.eu/downloads/> into the module's schema directory:

   ```
   datex4j-model-v3_8/src/main/resources/META-INF/datex4j/schema/v3.8/
   ```

3. **Add a bindings file.** Copy `datex4j-model-v3_7/src/main/xjb/bindings-v3.7.xjb` to
   `datex4j-model-v3_8/src/main/xjb/bindings-v3.8.xjb` and:

   - repoint every `schemaLocation` to `.../schema/v3.8/...`;
   - change every `<jaxb:package>` to `dev.juherr.datex4j.model.v3_8.<module>`;
   - add/remove `<jaxb:bindings>` blocks if the version added or dropped modules;
   - keep the `_namedAreaExtension` rename (and add any new collision workaround the version needs).

4. **Register the version.**

   - Add the constant to `DatexVersion` (`V3_8("3.8")` in `datex4j-core`); update
     `DatexVersion.current()` if it becomes the default.
   - Add a `DatexModelProviderV38` under `datex4j-model-v3_8/src/main/java/.../v3_8/spi/`,
     implementing `DatexModelProvider` with this version's module set (the colon-separated
     `contextPath`) and its `PayloadPublication` / `d2payload.ObjectFactory` references (plus the
     `messagecontainer` overrides if the version bundles the Exchange 2020 family). Register it in
     `datex4j-model-v3_8/src/main/resources/META-INF/services/dev.juherr.datex4j.model.spi.DatexModelProvider`.
   - Add the `<module>datex4j-model-v3_8</module>` to the reactor `pom.xml`, a `dependencyManagement`
     entry for it, and a `<dependency>` on it in the `datex4j-model` aggregate `pom.xml`.
   - Adjust `Namespaces` only if the base namespace scheme changed (stable at
     `http://datex2.eu/schema/3/<module>`).

5. **Regenerate, test, document.**

   ```bash
   ./mvnw -pl datex4j-model-v3_8 clean generate-sources   # inspect the new v3_8 packages
   ./mvnw verify                                           # full build, formatting, tests
   ```

   Add a round-trip test for the new version (see `DatexMultiVersionTest`) and update the README's
   bundled-versions line.

## Versions can differ in module set

A version publishes only the modules reachable from its root `DATEXII_3_D2Payload.xsd`, and that set
changes between versions. Always derive the file list by following the `<xs:include>`/`<xs:import>`
graph from the root rather than copying another version's directory. For example v3.5 has 15
schemas — it predates `ControlledZone`, `TrafficRegulation` and the MessageContainer family
(`MessageContainer`, `ExchangeInformation`, `CISInformation`, `InformationManagement`) as well as
the AFIR modules. The set is not even monotonic across minors: the `Parking` module first appears in
v3.3, and `TrafficRegulation` appears in v3.2/v3.3, is dropped in v3.4/v3.5, then returns in v3.6.
When a version drops modules, remove their `<jaxb:bindings>` blocks and leave them out of that
version's provider `contextPath` (each `DatexModelProviderV3X` lists exactly the module packages its
own version publishes).

### v3.0 schemaLocation rewrite

From v3.1 onward the published module schemas reference each other with bare relative file names
(`schemaLocation="DATEXII_3_Common.xsd"`), which resolve directly against the vendored directory. The
**v3.0** schemas are the exception: their `<xs:import>`s use absolute URLs
(`http://datex2.eu/schema/3/Common/3_0/DATEXII_3_Common.xsd`). Those were rewritten to bare file
names when vendoring so that XJC generation resolves offline against the local files, exactly as the
consortium itself did from v3.1 on. (The runtime schema resolver already strips imports to their file
name, so validation is unaffected either way; the rewrite is only needed for offline code
generation.) This is the only content edit made to any vendored schema.

## What should *not* change

- The XML façade (`DatexXml`, `DatexMarshaller`) — it is version-neutral by design.
- The classpath layout convention (`META-INF/datex4j/schema/v<version>`), encoded once in
  `DatexSchemas`.
- Any business logic — there is none in the generated model.

## Dropping a version

Delete its `datex4j-model-vX_Y` module (schemas, bindings, provider and service file all live
inside it), drop it from the reactor `pom.xml`, the `dependencyManagement` block and the
`datex4j-model` aggregate, and remove its `DatexVersion` constant. Because versions live in separate
modules and packages, removing one never affects the others.
