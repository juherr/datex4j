# Dataset: finland / afir-fintraffic

- **Source URL:** <https://afir.digitraffic.fi/api/charging-network/v1/locations/datex2-3.6>
  (Fintraffic / digitraffic.fi AFIR charging-network feed)
- **Licence:** Creative Commons 4.0 BY (CC BY 4.0) — attribution: **"Source: Fintraffic /
  digitraffic.fi, license CC 4.0 BY"**. Confirmed via Fintraffic's
  [Terms of use](https://www.fintraffic.fi/en/fintraffic/terms-use).
- **Download date:** 2026-07-24
- **DATEX version:** 3.6
- **Profile:** AFIR recharging — conformant DATEX II JSON, rooted at a `MessageContainer`
  (`egiEnergyInfrastructureTablePublication`)
- **Country:** Finland
- **Expected object counts:** 1 `EnergyInfrastructureTable`, 1 `EnergyInfrastructureSite`,
  1 `EnergyInfrastructureStation`, 1 `ElectricChargingPoint`, 1 `Connector`
- **Remarks:** Real values from the live Fintraffic AFIR feed, **trimmed** from the full response
  (~13 MB across 18 tables and thousands of sites) down to a single site so it is small enough to
  commit. Values are unmodified. The feed serves **JSON only** (no XML), so this dataset exercises
  the [`JsonRoundTrip`](../../../../java/dev/juherr/datex4j/it/support/JsonRoundTrip.java) path of
  the harness — parse into a `MessageContainer`, then an idempotent conformant-JSON round-trip that
  preserves the site's real identity, name and connector type. To refresh, re-fetch the endpoint and
  trim to one `energyInfrastructureTable` / `energyInfrastructureSite` entry. This is the same real
  fixture the `datex4j-json` codec uses as its Fintraffic oracle.
- **Known quirks:** The conformant JSON mapping is **best-effort and lossy**. This site's
  `locationReference` carries both an `locxFacilityLocation` (street address / postcode / city
  "Helsinki") and a point location; on read only the coordinates survive, because `FacilityLocation`
  is not a `LocationReference` subtype and the generated model has no field slot for it. The
  round-trip therefore drops the street address — the harness asserts the operator id, site name and
  connector type, not the city.

## Obtaining the full, live dataset

- **Endpoints:** see [`docs/afir/nap/finland.md`](../../../../../../docs/afir/nap/finland.md) for the
  full list (locations, statuses, operators, tariffs, MQTT).
- **How to obtain:** query `GET /api/charging-network/v1/locations/datex2-3.6` for the DATEX II JSON
  representation directly (no registration mentioned for the REST endpoints). Two headers are
  **required** by Digitraffic, or the endpoint answers `406`:
  - `Digitraffic-User: <a free-form identifier for your client>`
  - gzip: send `Accept-Encoding: gzip` (curl's `--compressed` does this and decompresses the reply).

  ```bash
  curl -s --compressed \
    -H 'Digitraffic-User: datex4j-integration-test' \
    -o /tmp/finland-full.json \
    https://afir.digitraffic.fi/api/charging-network/v1/locations/datex2-3.6
  ```
- **Format / DATEX version:** DATEX II v3.6 conformant JSON; GeoJSON also available.
- **Update frequency:** snapshots regenerated every minute.
- **Size:** roughly 13 MB — hence not committed (see the policy in
  [`../README.md`](../README.md)).

## Whole-feed read test (opt-in, never committed)

[`FinlandFullFeedReadTest`](../../../../java/dev/juherr/datex4j/it/FinlandFullFeedReadTest.java)
reads the **entire** downloaded feed into a `MessageContainer` and asserts the codec drops no
`energyInfrastructureSite` (parsed count equals the count in the raw JSON). It is **skipped** unless
you point it at a locally downloaded copy via the `datex4j.it.finland.full` system property:

```bash
# after the curl above
./mvnw -pl datex4j-integration-tests test \
  -Dtest=FinlandFullFeedReadTest \
  -Ddatex4j.it.finland.full=/tmp/finland-full.json
```

This keeps the committed suite offline and reproducible while still letting you validate a full,
real feed on demand. Last run against the live feed read **18 `energyInfrastructureTable`s, 3022
`energyInfrastructureSite`s, ~16.5k stations and connectors** with no loss (counts drift as the feed
updates every minute).

---

# Dataset: finland / roadworks (DATEX II v2)

A second, unrelated Finnish feed: Fintraffic / Digitraffic's **road-maintenance / roadworks**
traffic messages, published as **DATEX II v2**. Covered offline by
[`Datex2TrafficFeedReadValidateTest`](../../../../java/dev/juherr/datex4j/it/Datex2TrafficFeedReadValidateTest.java).

- **Source URL:**
  <https://tie.digitraffic.fi/api/traffic-message/v2/roadworks/datex2-2.2.3.xml> (send header
  `Digitraffic-User: <your client id>`)
- **Licence:** Creative Commons 4.0 BY (CC BY 4.0) — attribution: **"Source: Fintraffic /
  digitraffic.fi, license CC 4.0 BY"**.
- **Download date:** 2026-07-25
- **DATEX version:** 2.3 payload (`d2LogicalModel`, `modelBaseVersion="2"`, namespace
  `http://datex2.eu/schema/2/2_0`)
- **Profile / publication:** `SituationPublication` (roadworks / `MaintenanceWorks` records)
- **Country:** Finland
- **Expected object counts:** 1 `SituationPublication`, 1 `situation` with 3 `situationRecord`s
  (trimmed from 597 situations / 2298 records in the full ~6.4 MB feed).
- **Remarks:** Bare `d2LogicalModel` (no `exchange`). **Trimmed to the first `situation`**; values
  unmodified. Reads into a v2.3 `SituationPublication`; the situation id `GUID50467344` survives
  re-serialization.
- **Known quirks:** The feed's `xsi:schemaLocation` points at a **Finnish national-extension XSD**
  (`DATEXIISchema_2_2_3_with_definitions_FI.xsd`), **not** the plain DATEX II v2.3 schema. Validated
  against the **bundled plain v2.3** schema it is **invalid** (3 errors): the national profile omits
  the `exchange` element and the `payloadPublication`'s `lang` attribute and `publicationTime` that
  plain v2.3 mandates. This is national-profile drift, not a vendoring defect; the verdict is
  reported honestly (`expectedValid = false`).
