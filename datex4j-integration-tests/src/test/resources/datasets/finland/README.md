# Dataset: finland

**No data file is committed in this directory yet.** See
[`../README.md`](../README.md) for why (licence/size policy) and how to contribute one.

## How to obtain a real dataset

- **Source:** Fintraffic's Digitraffic service, AFIR / charging-network endpoints at
  <https://www.digitraffic.fi/en/road-traffic/afir/>.
- **How to obtain:** query `GET /api/charging-network/v1/locations/datex2-3.6` for the DATEX II
  representation directly (no registration mentioned for the REST endpoints); see
  [`../../../../../../docs/afir/nap/finland.md`](../../../../../../docs/afir/nap/finland.md) for the
  full endpoint list (statuses, operators, tariffs, MQTT).
- **Licence:** **Creative Commons 4.0 BY (CC BY 4.0)** — confirmed via Fintraffic's
  [Terms of use](https://www.fintraffic.fi/en/fintraffic/terms-use); attribute as "Source: Fintraffic
  / digitraffic.fi, license CC 4.0 BY" if a file from this source is added.
- **Format / DATEX version:** **DATEX II v3.6** (the `datex2-3.6` endpoint); GeoJSON also available.
- **Update frequency:** snapshots regenerated every minute.

This is the one country in this suite (alongside Germany) with a **confirmed, live DATEX II
endpoint and an explicit open licence** — a good first candidate for an actual committed real-world
fixture once someone fetches and reviews a concrete response for size and content.
