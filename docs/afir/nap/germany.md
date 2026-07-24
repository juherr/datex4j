# Germany

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
- **NOW GmbH announcement:**
  [Implementation of Article 20 AFIR: DATEX II data profile now ready for
  use](https://www.now-gmbh.de/en/news/pressreleases/implementation-of-article-20-afir-datex-2-data-profile-now-ready-for-use/) —
  the primary source for the profile version, mandatory-use date, and Mobilithek as the delivery
  channel.
- **FAQ:** [FAQ zur Datenbereitstellung durch Betreiber öffentlich zugänglicher Ladeinfrastruktur im
  DATEX‑II‑Standard](https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil/wiki/FAQ--zur-Datenbereitstellung-durch-Betreiber-%C3%B6ffentlich-zug%C3%A4nglicher-Ladeinfrastruktur-im-DATEX%E2%80%90II%E2%80%90Standard) —
  the GitHub wiki FAQ maintained alongside the profile; German-language, but the authoritative
  operational reference for delivery mechanics (see "Delta mechanism" below).
- **Examples:** no standalone example-payload bundle was found in the GitHub repository or on
  Mobilithek at the time of writing; the closest concrete, XSD-valid starting point is `datex4j`'s
  own synthetic fixture, see
  [`../official-datex-resources.md`](../official-datex-resources.md#official-examples) and
  [`../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md).
- **DATEX II schemas:** the base `EnergyInfrastructure` and AFIR-specific
  `AfirEnergyInfrastructure` / `AfirFacilities` schemas the German profile extends are the same v3.7
  schemas `datex4j` vendors — see [`../official-datex-resources.md`](../official-datex-resources.md#xsd-schemas).

## Available datasets

- **AFIR recharging data via Mobilithek** — individual CPOs (or aggregators publishing on behalf of
  several CPOs) each register a "Datenangebot" (data offer) on Mobilithek containing their static
  and dynamic recharging data in the DATEX II AFIR profile format. There is no single
  national-snapshot file; datasets are discovered and subscribed to per offer through the Mobilithek
  catalogue.
  - **Format:** DATEX II XML, structured per the `01-00-00` AFIR recharging profile.
  - **Licence:** set per data offer by the publishing CPO; not a single blanket licence for the
    platform. Confirm the specific offer's terms before redistributing any downloaded file.
  - **Update frequency:** driven by the **delta mechanism** (see below) — CPOs push delta messages
    as their charging-point state changes, with a full snapshot required at defined intervals
    regardless of whether anything changed.
  - **Access:** requires a free Mobilithek account and a subscription request to the specific data
    offer (see "Registration" above); this is why no such file is committed to
    [`datasets/germany/`](../../../datex4j-integration-tests/src/test/resources/datasets/germany) —
    it is documented there, not downloaded and checked in.

## Notes

- **The AFIR DATEX II recharging profile** (version `01-00-00`) is Germany's concrete instantiation
  of the base DATEX II Energy Infrastructure model for AFIR Article 20 reporting. It is developed in
  the open on GitHub, with both German- and English-language guidance documents describing exactly
  which DATEX II elements a CPO must populate and how.
- **The FAQ** (linked above) is the practical reference for implementers: it covers how data offers
  are structured (one per operator, or one aggregating several operators), how delivery to
  Mobilithek's interface works, and the detailed rules for delta messages.
- **The API** is Mobilithek's own data-offer interface — CPOs deliver via "die angebotene
  Schnittstelle der Mobilithek" (the interface Mobilithek offers), documented per-offer rather than
  as one public REST reference; consumers likewise pull from Mobilithek per subscribed offer rather
  than a single fixed endpoint.
- **The delta mechanism** is the one country-specific mechanic worth understanding before writing
  code against German data: per the FAQ, delta messages should contain **only the charging-point
  elements that changed** since the previous message ("Delta-Meldungen sollen nur geänderte Elemente
  enthalten, auf Basis der Ladepunkte"). Two operational constraints bound it:
  - Deltas must not be sent more often than **1 Hz** ("nicht mit einer höherer Frequenz als 1
    Hertz").
  - A **full snapshot (Gesamtabbild)** must be sent at least every **3,500 delta messages or every 6
    hours**, whichever comes first, so consumers can always resynchronise even after a missed delta.
  - Deltas must be "genuine" — resending unchanged data as if it were a delta is explicitly to be
    avoided.

  For `datex4j`, this means a client consuming live German data cannot assume every message is a
  full `EnergyInfrastructureStatusPublication`; it must track state across a snapshot-then-deltas
  sequence itself. This is tracked as future work — see
  ["Delta-update parsing"](../README.md#future-work) in the knowledge base's future-work list.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/germany/README.md) —
  download instructions and the current (empty) state of the committed Germany dataset directory.
- [`../official-datex-resources.md`](../official-datex-resources.md) — the general DATEX II/AFIR
  source list this page draws on for schemas and the profile/announcement links above.
