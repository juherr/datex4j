# Adding or upgrading a DATEX II version

datex4j bundles several DATEX II versions at once, each generated into its own version-scoped package
tree (`dev.juherr.datex4j.model.v3_6.*`, `...v3_7.*`). Adding a new version is additive and mostly
mechanical: vendor its schemas, add a bindings file and a generation execution, and register the
version. Existing hand-written code does not change.

The steps below use a hypothetical `3.8` as the new version.

## Steps

1. **Vendor the new schemas.** Download every XSD for the target version from
   <https://docs.datex2.eu/downloads/> into a new version directory:

   ```
   datex4j-model/src/main/resources/META-INF/datex4j/schema/v3.8/
   ```

   Keep the existing version directories — they are still supported.

2. **Add a bindings file.** Copy `datex4j-model/src/main/xjb/bindings-v3.7.xjb` to
   `bindings-v3.8.xjb` and:

   - repoint every `schemaLocation` to `.../schema/v3.8/...`;
   - change every `<jaxb:package>` to `dev.juherr.datex4j.model.v3_8.<module>`;
   - add/remove `<jaxb:bindings>` blocks if the version added or dropped modules;
   - keep the `_namedAreaExtension` rename (and add any new collision workaround the version needs).

3. **Add a generation execution.** In `datex4j-model/pom.xml`, add a `generate-v3.8` execution of
   `jaxb-maven-plugin` mirroring the existing ones: `schemaDirectory` = `.../schema/v3.8`,
   `bindingIncludes` = `bindings-v3.8.xjb`, `generateDirectory` =
   `${project.build.directory}/generated-sources/xjc-v3.8`.

4. **Register the version.**

   - Add the constant to `DatexVersion` (`V3_8("3.8")` in `datex4j-core`); update
     `DatexVersion.current()` if it becomes the default.
   - Add a `V3_8` constant to `VersionModel` (`datex4j-xml`) with its `packageSegment`, module set,
     and the version-specific `PayloadPublication` / `ObjectFactory` references, and a `case` in
     `VersionModel.of(...)`.
   - Adjust `Namespaces` only if the base namespace scheme changed (stable at
     `http://datex2.eu/schema/3/<module>`).

5. **Regenerate, test, document.**

   ```bash
   ./mvnw -pl datex4j-model clean generate-sources   # inspect the new v3_8 packages
   ./mvnw verify                                      # full build, formatting, tests
   ```

   Add a round-trip test for the new version (see `DatexMultiVersionTest`) and update the README's
   bundled-versions line.

## Versions can differ in module set

A version publishes only the modules reachable from its root `DATEXII_3_D2Payload.xsd`, and that set
changes between versions. Always derive the file list by following the `<xs:include>`/`<xs:import>`
graph from the root rather than copying another version's directory. For example v3.5 has 15
schemas — it predates `ControlledZone`, `TrafficRegulation` and the MessageContainer family
(`MessageContainer`, `ExchangeInformation`, `CISInformation`, `InformationManagement`) as well as
the AFIR modules. When a version drops modules, remove their `<jaxb:bindings>` blocks and leave them
out of that version's `VersionModel` module set (see how `VersionModel.ModelPackages` layers the
v3.6 and AFIR additions onto the v3.5 base).

## What should *not* change

- The XML façade (`DatexXml`, `DatexMarshaller`) — it is version-neutral by design.
- The classpath layout convention (`META-INF/datex4j/schema/v<version>`), encoded once in
  `DatexSchemas`.
- Any business logic — there is none in the generated model.

## Dropping a version

Delete its schema directory, bindings file and `jaxb-maven-plugin` execution, remove its
`DatexVersion` and `VersionModel` constants, and update any tests. Because versions live in separate
packages, removing one never affects the others.
