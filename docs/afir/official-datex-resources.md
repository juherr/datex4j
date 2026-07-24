# Official DATEX II resources

Curated, direct links to the official DATEX II and AFIR sources this knowledge base is built from.
Every URL below was checked (fetched or resolved) while writing this page; where an official,
citable link could not be confirmed, that is stated explicitly instead of guessing one.

> This page is AFIR-focused. For a cross-domain catalogue of open DATEX II data sources usable to
> test the whole library (situations, SRTI, parking, VMS, roadworks, …), see
> [`../datex-test-data-sources.md`](../datex-test-data-sources.md).

## DATEX II documentation portal

- **URL:** <https://docs.datex2.eu>
- **Why it matters:** the entry point to all DATEX II documentation, organized by expertise level
  (Basics → Using → Mastering → Expert), with downloads for every released version back to 2.0 and
  the current default, **v3.7**. Start here for anything not covered by the more specific links
  below.

## XSD schemas

- **URL:** <https://docs.datex2.eu/downloads/modelv37/> (per-namespace `.xsd`/`.json` files, UML
  models, and release notes for v3.7)
- **Why it matters:** this is the authoritative source of the schemas `datex4j` vendors under
  [`datex4j-model/src/main/resources/META-INF/datex4j/schema/v3.7/`](../../datex4j-model/src/main/resources/META-INF/datex4j/schema/v3.7)
  and generates the JAXB model from — including the AFIR-relevant
  `DATEXII_3_EnergyInfrastructure.xsd`, `DATEXII_3_AfirEnergyInfrastructure.xsd` and
  `DATEXII_3_AfirFacilities.xsd`. If a generated class's shape looks surprising, this is the schema
  to check against.

## Developer Guide

- **URL:** <https://docs.datex2.eu/levels/developers/> ("Developer's Corner")
- **Why it matters:** documents JAXB code-generation pitfalls directly relevant to how `datex4j`
  itself generates its model — e.g. the `underscoreBinding="asCharInWord"` binding customization and
  a known naming conflict between abstract and concrete `NamedArea` classes in the
  LocationReferencing module. Useful background if you ever need to regenerate or extend the model.

## User Guide

- **URL:** <https://docs.datex2.eu/user-guide/>
- **Why it matters:** the conceptual reference for DATEX II as a whole — data models, XML schemas,
  exchange mechanisms, location referencing, recommended profiles (MMTIS, RTTI, SRTI). Read this
  before diving into a specific publication type.
- **Energy Infrastructure section:** <https://docs.datex2.eu/levels/mastering/energy/> — the
  User Guide's page for the base `EnergyInfrastructureTablePublication` /
  `EnergyInfrastructureStatusPublication` model (sites → stations → refill points) that the AFIR
  packages extend. It does not mention AFIR by name (it predates the AFIR-specific packages), but it
  is the model AFIR builds on.

## Official examples

No dedicated, confirmable bundle of official example XML instance documents for DATEX II **v3.7**
was found while writing this page (the v3.7 downloads page lists schemas, UML models and release
notes, but no sample payloads). To get a validating example in practice:

- Use the **DATEX II Schema generation tool** wizard at <https://webtool.datex2.eu/wizard> to
  assemble and export a profile-conformant model/schema, or
- Check the **version-specific Downloads page** for the version you need (pattern
  `https://docs.datex2.eu/downloads/modelv<version>/`, e.g. `modelv23` for v2.3), since some older
  major versions' pages do bundle example payloads, or
- Use `datex4j`'s own committed, XSD-valid AFIR fixture as a concrete (if non-official, synthetic)
  starting point:
  [`datex4j-integration-tests/src/test/resources/datasets/synthetic/afir-recharging/table.xml`](../../datex4j-integration-tests/src/test/resources/datasets/synthetic/afir-recharging/table.xml).

## AFIR profile

- **GitHub repository:** <https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil>
  ("Datenmodell für die Bereitstellung von Daten bzgl. Ladeinfrastruktur gemäß AFIR") — the working
  repository for Germany's concrete AFIR DATEX II recharging profile (data model, German- and
  English-language guidance documents, release history). **No `LICENSE` file was found in this
  repository at the time of writing** — treat its contents as reference material only, and verify
  licensing terms yourself before reusing any file from it.
- **Announcement:** [NOW GmbH — "Implementation of Article 20 AFIR: DATEX II data profile now ready
  for use"](https://www.now-gmbh.de/en/news/pressreleases/implementation-of-article-20-afir-datex-2-data-profile-now-ready-for-use/) —
  confirms profile version `01-00-00`, its availability through Germany's NAP (Mobilithek, run by
  the Federal Ministry of Transport), and that DATEX II use becomes mandatory there from 14 April
  2026.
- **Underlying standard:** <https://datex2.eu/specifications/> lists **CEN/TS 16157-10:2022 —
  "Energy infrastructure publications"**, the DATEX II technical specification part that Article 20
  points implementers toward; it is a paid CEN document, not freely downloadable, but the
  specifications page confirms its scope and part number.

## Regulation and National Access Points

- **Regulation (EU) 2023/1804 (AFIR):**
  <https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX%3A32023R1804> — the legal text
  itself; Article 20 is the data-provision requirement this whole knowledge base exists to support.
- **European NAP catalogue:** <https://datex2.naps.inqms.tamtamresearch.com/> — a community survey
  of DATEX II availability across European National Access Points; useful as a starting point when
  looking for a specific country's NAP (see the [country pages](./README.md#country-pages)).
