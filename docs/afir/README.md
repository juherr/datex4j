# AFIR / DATEX II knowledge base

## What this is

This is a knowledge base for Java developers who need to consume or produce **AFIR-compliant
recharging/refuelling data in DATEX II format**: what the regulation requires, what DATEX II is,
where each EU country publishes its data, and how `datex4j` maps onto all of it. It is paired with
an **offline JUnit integration-test suite**, [`datex4j-integration-tests`](../../datex4j-integration-tests),
that round-trips real and synthetic AFIR datasets against the generated model — a working,
executable complement to the reference material linked from these pages.

## AFIR

[Regulation (EU) 2023/1804](https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX%3A32023R1804)
("AFIR", the Alternative Fuels Infrastructure Regulation) requires, in **Article 20**, that
operators of publicly accessible recharging and refuelling points make both **static** data
(location, connector types/count, operating hours, vehicle compatibility) and **dynamic** data
(current availability, occupancy, operating status) available free of charge and without
discrimination — static data updated within 24 hours of a change, dynamic data within one minute.
Member States must expose this data through a **National Access Point** (see below), and Article 20
points implementers to the DATEX II data model for alternative fuels (CEN/TS 16157‑10, "Energy
infrastructure publications" — see [official-datex-resources.md](./official-datex-resources.md)).

Germany was the first Member State to publish a concrete AFIR DATEX II profile: NOW GmbH's
[implementation announcement](https://www.now-gmbh.de/en/news/pressreleases/implementation-of-article-20-afir-datex-2-data-profile-now-ready-for-use/)
describes profile version `01-00-00`, made available through Mobilithek, with DATEX II use becoming
mandatory there from 14 April 2026. See [`nap/germany.md`](./nap/germany.md) for the details.

## DATEX II

[DATEX II](https://docs.datex2.eu) is the European standard (CEN/TS 16157, several parts now
promoted to full European Standards) for exchanging road-traffic and travel-information data,
covering everything from traffic situations to parking, urban-vehicle-access regulation, and —
relevant here — energy/refuelling infrastructure (`EnergyInfrastructureTablePublication` for static
data, `EnergyInfrastructureStatusPublication` for dynamic data). `datex4j` targets the current
**DATEX II v3.7** release by default and ships the **AFIR packages** that extend the base energy
model (`AfirEnergyInfrastructure`, `AfirFacilities`), generated straight from the official v3.7 XSD
schemas — see [official-datex-resources.md](./official-datex-resources.md) for exact schema
locations and links.

## National Access Points

A **National Access Point (NAP)** is the single, official online point through which a Member State
publishes its transport and mobility datasets — including, since AFIR Article 20(4), recharging and
refuelling infrastructure data. Each NAP has its own portal, access process (some require a free
account or API token) and publication cadence; the DATEX II payload format is common ground, but
concrete field usage still varies per country. The
[European NAP catalogue](https://datex2.naps.inqms.tamtamresearch.com/) is a community reference
that surveys DATEX II availability across NAPs — start there when looking for a specific country's
access point, then confirm details against that country's own NAP (see the
[country pages](#country-pages) below).

## How datex4j relates

- [`datex4j-model`](../../datex4j-model) — the generated DATEX II v3.7 (and v3.6) object model,
  including the AFIR energy-infrastructure and facilities packages, vendored straight from the
  official XSD schemas (never hand-edited).
- [`datex4j-xml`](../../datex4j-xml), [`datex4j-validation`](../../datex4j-validation),
  [`datex4j-json`](../../datex4j-json) — the facades for marshalling/unmarshalling, XSD validation
  and JSON (de)serialization of that model, used throughout this knowledge base's examples and by
  the test suite below.
- [`datex4j-ocpi`](../../datex4j-ocpi) — a bidirectional OCPI ↔ DATEX II (Energy Infrastructure)
  mapping. The normative field-level mapping it implements is documented in
  [`docs/references/`](../references/README.md) (the IDACS Deliverable 2.2.1 reference).
- [`datex4j-integration-tests`](../../datex4j-integration-tests) — the offline round-trip test
  suite this knowledge base accompanies: it parses, validates, re-serializes and diffs committed
  AFIR datasets (synthetic today, real per-country datasets as they are added — see
  [`datasets/README.md`](../../datex4j-integration-tests/src/test/resources/datasets/README.md)).

## Country pages

Per-country reference pages — NAP, authority, available AFIR datasets, and country-specific notes —
live under `nap/`:

- [`nap/germany.md`](./nap/germany.md) — deep reference (Mobilithek, the AFIR DATEX II recharging
  profile, delta updates)
- [`nap/france.md`](./nap/france.md)
- [`nap/netherlands.md`](./nap/netherlands.md)
- [`nap/denmark.md`](./nap/denmark.md)
- [`nap/finland.md`](./nap/finland.md)
- [`nap/belgium.md`](./nap/belgium.md)
- [`nap/austria.md`](./nap/austria.md)
- [`nap/sweden.md`](./nap/sweden.md)

## Official resources

For direct links to the DATEX II documentation portal, XSD schemas, developer/user guides, and the
AFIR profile sources — each verified and annotated with why it matters — see
[`official-datex-resources.md`](./official-datex-resources.md).

## Design principles

This knowledge base **links to and explains**; it does not copy or restate official documents. Each
page:

- **Cites the official source** for every factual claim (regulation article, schema, guide, NAP
  portal) rather than paraphrasing it as if it were `datex4j`'s own specification.
- **Explains where and why** a resource matters for a Java developer working with `datex4j` —
  e.g. why the AFIR packages sit on top of the base Energy Infrastructure model, or why a NAP's
  delta-update mechanism affects how you'd drive `datex4j-xml`.
- **Highlights pitfalls** encountered while building the integration-test suite (schema-version
  drift between draft and final specifications, NAPs that require authenticated downloads, licences
  that block committing a dataset outright) rather than hiding them.
- **Never invents a URL.** If an official source could not be confirmed at the time a page was
  written, the page says so and describes how to locate the resource instead of guessing a link.

## Future work

- **OCPI ↔ DATEX II mapping** — deepen `datex4j-ocpi` coverage of the AFIR-specific
  extensions (`AfirEnergyInfrastructure`, `AfirFacilities`) beyond the base Energy Infrastructure
  mapping already documented in [`docs/references/`](../references/README.md).
- **AFIR validation rules** — encode Article 20's specific data-quality and update-frequency
  requirements (not just XSD structural validity) as checks usable alongside `datex4j-validation`.
- **Country-specific extensions** — track and document each NAP's use of DATEX II extension points
  (Level A/B/C) as real per-country datasets are added.
- **Interoperability tests** — cross-country round-trip tests once multiple NAPs' real datasets are
  committed, to catch divergent interpretations of the same schema.
- **Conformance tests** — a dedicated suite asserting datasets satisfy the AFIR DATEX II profile
  (not only the underlying XSD).
- **Benchmark datasets** — larger, representative fixtures for performance/scale testing of the
  `datex4j-xml`/`datex4j-json` facades.
- **Delta-update parsing** — support for NAPs (starting with Germany's, see
  [`nap/germany.md`](./nap/germany.md)) that publish incremental/delta updates rather than full
  snapshots.
