# Dataset: france

This directory holds committed real-world DATEX II datasets: one **v3** feed (**DiaLog traffic
regulations**) and two **v2** feeds (**speeds** and **events**, both from Bison Futé / Tipi). A
separate AFIR charging-point source is documented but not committed (it is CSV/GeoJSON, not DATEX
II).

The two v2 feeds are covered offline by
[`Datex2TrafficFeedReadValidateTest`](../../../../java/dev/juherr/datex4j/it/Datex2TrafficFeedReadValidateTest.java),
which reads each into the version-scoped `D2LogicalModel` (a v2 document is rooted at
`d2LogicalModel`, not at a bare publication) and reports its XSD verdict.

## Committed dataset: speeds

- **Source URL:** <https://transport.data.gouv.fr/resources/79165/download> (Bison Futé / Tipi,
  published on France's National Access Point transport.data.gouv.fr)
- **Licence:** Licence Ouverte / Open Licence, version 2.0 — attribution: **"Source: Bison Futé /
  Tipi (transport.data.gouv.fr), Licence Ouverte 2.0"**.
- **Download date:** 2026-07-25
- **DATEX version:** 2.2 (`d2LogicalModel`, `modelBaseVersion="2"`, namespace
  `http://datex2.eu/schema/2/2_0`)
- **Profile / publication:** `MeasuredDataPublication` (measured traffic flow / speed)
- **Country:** France
- **Expected object counts:** 1 `exchange`, 1 `MeasuredDataPublication`, **2 `siteMeasurements`**
  (trimmed from 1184 in the full ~1.2 MB feed).
- **Remarks:** Bare `d2LogicalModel` on the wire. **Trimmed to the first 2 `siteMeasurements`**; the
  `exchange`, publication header and all values are unmodified. Reads into a v2.2
  `MeasuredDataPublication`; the measurement-site reference id `MUM76.h1` survives re-serialization.
- **Known quirks:** v2.2 validation reports it **valid (zero errors)**.

## Committed dataset: events

- **Source URL:** <https://transport.data.gouv.fr/resources/79173/download> (Bison Futé / Tipi
  "Evenementiel-DIR" event feed, transport.data.gouv.fr)
- **Licence:** Licence Ouverte / Open Licence, version 2.0 — attribution: **"Source: Bison Futé /
  Tipi (transport.data.gouv.fr), Licence Ouverte 2.0"**.
- **Download date:** 2026-07-25
- **DATEX version:** 2.2 (`d2LogicalModel`, `modelBaseVersion="2"`, namespace
  `http://datex2.eu/schema/2/2_0`)
- **Profile / publication:** `SituationPublication`
- **Country:** France
- **Expected object counts:** 1 `exchange`, 1 `SituationPublication`, 1 `situation` with 1
  `situationRecord` (a `VehicleObstruction`).
- **Remarks:** **The `79173/download` resource is an Apache directory index**, not a single feed;
  each entry (e.g. `3405697.xml`) is one publication **wrapped in a `<soap:Envelope>`**. datex4j
  reads `d2LogicalModel`, not SOAP envelopes, so the committed fixture is the **SOAP-unwrapped**
  inner `d2LogicalModel` — only the `<soap:Envelope>`/`<soap:Body>` wrapper was stripped; all
  DATEX II values are unmodified. Reads into a v2.2 `SituationPublication`; the situation id
  `260724-000456` survives re-serialization.
- **Known quirks:** v2.2 validation reports it **valid (zero errors)**. If you fetch a fresh file
  yourself, strip the `<soap:Envelope>`/`<soap:Body>` wrapper before handing the bytes to datex4j.

## Committed dataset: dialog-regulations

- **Source URL:** <https://dialog.beta.gouv.fr/api/regulations.xml> (DiaLog, MTES-MCT — French
  Ministry for the Ecological Transition)
- **Licence:** Licence Ouverte / Open Licence, version 2.0 — attribution: **"Source: DiaLog,
  MTES-MCT (dialog.beta.gouv.fr), Licence Ouverte 2.0"**.
- **Download date:** 2026-07-24
- **DATEX version:** v3 bare payload (`d2:payload` → `TrafficRegulationPublication`). Authored against
  an **older DATEX II minor**: the feed uses `xsi:type="ValidityCondition"`, a `TrafficRegulation`
  `Condition` subtype that exists only in **v3.2/v3.3** and was removed in v3.4+. It is therefore read
  and validated against **v3.3** (it does **not** read into v3.7 at all — JAXB cannot instantiate the
  abstract `Condition` because `ValidityCondition` is unknown there).
- **Profile / publication:** `TrafficRegulationPublication`
- **Country:** France
- **Expected object counts:** 2 `trafficRegulationOrder` entries (trimmed from ~11 465 in the full
  feed).
- **Remarks:** The full response is ~98 MB; this fixture is **trimmed to the first 2
  `trafficRegulationOrder`s** inside the single `trafficRegulationsFromCompetentAuthorities` container,
  with all namespaces (including the `dx:` DiaLog extension) preserved and values unmodified. Reads
  into a v3.3 `TrafficRegulationPublication`; the order id `018a45df-58c3-740c-b712-37d3d2ca25f8`
  survives re-serialization. Covered offline by
  [`Datex3TrafficFeedReadValidateTest`](../../../../java/dev/juherr/datex4j/it/Datex3TrafficFeedReadValidateTest.java).
- **Known quirks:** v3.3 validation reports it invalid (~13 errors) — chiefly the producer using
  `commercial` as a `VehicleTypeEnum` value, which is not in the DATEX II enumeration. This is a
  producer data-quality issue, not a vendoring defect.

## Not committed: AFIR charging points

## How to obtain a real dataset

- **Source:** Base nationale des IRVE (Infrastructures de Recharge pour Véhicules Électriques),
  published on France's National Access Point,
  <https://transport.data.gouv.fr/datasets/fichier-consolide-des-bornes-de-recharge-pour-vehicules-electriques>.
- **How to obtain:** download the consolidated CSV or GeoJSON resource directly from that dataset
  page — no registration or authentication is required.
- **Licence:** Licence Ouverte / Open Licence, version 1.0.
- **Format / DATEX version:** **CSV and GeoJSON**, structured per the
  [decree of 4 May 2021](https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000043475441) — **not DATEX
  II**. No DATEX II-format endpoint for French AFIR recharging data was confirmed at the time of
  writing; see
  [`../../../../../../docs/afir/nap/france.md`](../../../../../../docs/afir/nap/france.md) for what
  was and wasn't confirmed.
- **Update frequency:** daily.

Because the currently confirmed dataset is CSV/GeoJSON rather than DATEX II, it does not fit this
suite's DATEX II round-trip harness as-is; no file is committed pending either a confirmed DATEX II
publication or a decision to add a CSV/GeoJSON-to-DATEX-II conversion step.
