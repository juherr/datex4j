# Germany

> Last verified: 2026-07-25.

This page records Germany's verified NAP, AFIR datasets, access constraints, and test coverage.

## General information

- **Country:** Germany
- **National Access Point (NAP):** [Mobilithek](https://mobilithek.info) ("Mobilitätsdaten
  Deutschland") — the federal marketplace and access point for mobility data.
- **Authority:** Mobilithek is operated on behalf of the **Federal Ministry for Digital and
  Transport (BMDV)**; day-to-day platform development and the AFIR recharging profile itself are
  driven by **NOW GmbH** (Nationale Organisation Wasserstoff- und Brennstoffzellentechnologie),
  which runs Germany's "Nationale Leitstelle Ladeinfrastruktur" (National Charging Infrastructure
  Control Centre) — see its
  [implementation announcement](https://www.now-gmbh.de/en/news/pressreleases/implementation-of-article-20-afir-datex-2-data-profile-now-ready-for-use/).
- **AFIR status:** Germany was the **first EU Member State to publish a concrete AFIR DATEX II
  profile**. Per the NOW GmbH announcement, profile version `01-00-00` is available through
  Mobilithek, and DATEX II use is **mandatory there from 14 April 2026**; operators (CPOs) of
  publicly accessible charging points have been required to provide static and dynamic data via an
  API since 14 April 2025 under AFIR Article 20.

## Useful links

- **NAP:** <https://mobilithek.info>
- **Registration:** <https://mobilithek.info/registration-request> — a free account is required
  (register with an institutional/organisational email address); consuming a specific data offer
  additionally requires requesting a subscription ("Datenangebot abonnieren") to that offer.
- **AFIR DATEX II recharging profile (GitHub):**
  <https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil> — "Datenmodell für die
  Bereitstellung von Daten bzgl. Ladeinfrastruktur gemäß AFIR", the working repository for the
  German profile (data model, release history under `Releases/Version 01-00-00`, and both a German
  and an English guidance PDF: `Mobilithek-Leitfaden_fuer_AFIR-Recharging-Datenangebote_250805.pdf`
  / `Mobilithek_Guideline_AFIR_Recharging_Data_offers_250805.pdf`). **No `LICENSE` file is present
  in this repository** — treat it as reference material and verify licensing before reusing any file
  from it directly.
- **NOW GmbH announcement:** [Implementation of Article 20 AFIR: DATEX II data profile now ready for
  use][now-announcement] —
  the primary source for the profile version, mandatory-use date, and Mobilithek as the delivery
  channel.
- **FAQ:** [FAQ zur Datenbereitstellung durch Betreiber öffentlich zugänglicher Ladeinfrastruktur im
  DATEX‑II‑Standard][mobilithek-faq] —
  the GitHub wiki FAQ maintained alongside the profile; German-language, but the authoritative
  operational reference for delivery mechanics (see "Delta mechanism" below).
- **Examples:** no standalone example-payload bundle was found in the GitHub repository or on
  Mobilithek at the time of writing; the closest concrete, XSD-valid starting point is `datex4j`'s
  own synthetic fixture, see
  [`../official-datex-resources.md`](../official-datex-resources.md#official-examples) and
  [Germany fixture metadata](../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md).
- **DATEX II schemas:** the base `EnergyInfrastructure` and AFIR-specific
  `AfirEnergyInfrastructure` / `AfirFacilities` schemas the German profile extends are the same v3.7
  schemas `datex4j` vendors — see [`../official-datex-resources.md`](../official-datex-resources.md#xsd-schemas).

## Available datasets

### AFIR recharging data via Mobilithek

Individual CPOs, or aggregators publishing on their behalf, each register a "Datenangebot" on
Mobilithek containing static and dynamic recharging data in the DATEX II AFIR profile. There is no
single national snapshot; consumers discover and subscribe to each offer through the catalogue.

- **Format:** DATEX II XML, structured per the `01-00-00` AFIR recharging profile.
- **License:** set per data offer by the publishing CPO; not a single blanket license for the
  platform. Confirm the specific offer's terms before redistributing any downloaded file.
- **Update frequency:** driven by the **delta mechanism** (see below) — CPOs push delta messages
  as their charging-point state changes, with a full snapshot required at defined intervals
  regardless of whether anything changed.
- **Access:** requires a free Mobilithek account and a subscription request to the specific data
  offer (see "Registration" above); this is why no such file is committed to
  [`datasets/germany/`](../../../datex4j-integration-tests/src/test/resources/datasets/germany) —
  it is documented there, not downloaded and checked in.

## Notes

### AFIR recharging profile

Version `01-00-00` is Germany's concrete instantiation of the base DATEX II Energy Infrastructure
model for AFIR Article 20 reporting. German- and English-language guidance documents describe which
DATEX II elements a CPO must populate and how.

### FAQ and API

The FAQ is the practical reference for implementers. It covers how data offers are structured, how
delivery to Mobilithek works, and the detailed rules for delta messages. Mobilithek documents its
data-offer interface per offer rather than as one public REST reference.

### Delta mechanism

Per the FAQ, delta messages should contain only the charging-point elements that changed since the
previous message. Three operational constraints apply:

- Deltas must not be sent more often than **1 Hz**.
- A **full snapshot** must be sent at least every **3,500 delta messages or every 6 hours**,
  whichever comes first, so consumers can resynchronise after a missed delta.
- Deltas must be genuine; unchanged data should not be resent as a delta.

For `datex4j`, a client consuming live German data cannot assume every message is a full
`EnergyInfrastructureStatusPublication`; it must track state across a snapshot-then-deltas
sequence itself. This is tracked as future work — see
["Delta-update parsing"](../README.md#future-work).

## Other DATEX II feeds (road traffic, beyond AFIR)

Germany also exchanges road-traffic data (situations, roadworks, traffic data) as DATEX II, but the
**DATEX II route is registration-gated** and the widely-used open route is **not** DATEX II.

- **Mobilithek (DATEX II, gated):** the same platform that carries the AFIR profile also carries
  road-traffic DATEX II offers — Germany publishes national DATEX II profiles including a
  [German Roadworks Profile](https://repo.datex2.eu/implementations/profile_directory/german-roadworks-profile)
  and a [German Traffic Data Profile](https://repo.datex2.eu/implementations/profile_directory/german-traffic-data-profile).
  Mobilithek replaced the former MDM (Mobilitäts Daten Marktplatz, run by BASt) and mCLOUD. Access
  requires a **free account plus a per-offer subscription**, and delivery is pull over **mTLS** —
  there is **no anonymous DATEX II endpoint**. See "Registration" above; the same gating applies to
  road-traffic offers as to the AFIR ones.
- **Autobahn GmbH REST API (open, but JSON — not DATEX II):** for the federal motorway network,
  Autobahn GmbH exposes a public REST API at
  [`https://verkehr.autobahn.de/o/autobahn/`](https://verkehr.autobahn.de/o/autobahn/) — verified
  `HTTP 200`, `application/json`, **anonymous**. It covers roadworks, closures, warnings, parking and
  webcams, but serves **JSON, not DATEX II**, so it is useful as an open German traffic source only
  for non-DATEX comparison, not as a DATEX II fixture for this library.

**How to obtain (DATEX II):** register on [mobilithek.info](https://mobilithek.info) and subscribe to
the specific road-traffic data offer; delivery is per-offer over mTLS. No file is committed to
[`datasets/germany/`](../../../datex4j-integration-tests/src/test/resources/datasets/germany) for the
same reason as the AFIR data — it is gated, not anonymously fetchable.

## See also

- [Germany fixture metadata](../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md) —
  download instructions and the current (empty) state of the committed Germany dataset directory.
- [`../official-datex-resources.md`](../official-datex-resources.md) — the general DATEX II/AFIR
  source list this page draws on for schemas and the profile/announcement links above.

[mobilithek-faq]: https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil/wiki/FAQ--zur-Datenbereitstellung-durch-Betreiber-%C3%B6ffentlich-zug%C3%A4nglicher-Ladeinfrastruktur-im-DATEX%E2%80%90II%E2%80%90Standard
[now-announcement]: https://www.now-gmbh.de/en/news/pressreleases/implementation-of-article-20-afir-datex-2-data-profile-now-ready-for-use/
