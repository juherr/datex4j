# Dataset: france

**No data file is committed in this directory yet.** See
[`../README.md`](../README.md) for why (licence/size policy) and how to contribute one.

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
