# Dataset: france

This directory holds one committed real-world DATEX II v3 dataset (**DiaLog traffic regulations**,
below). A separate AFIR charging-point source is documented but not committed (it is CSV/GeoJSON, not
DATEX II).

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
