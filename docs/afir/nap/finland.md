# Finland

## General information

- **Country:** Finland
- **National Access Point (NAP):** **Fintraffic's Digitraffic service**, at
  <https://www.digitraffic.fi/en/road-traffic/afir/>, part of Fintraffic's wider Traffic Data
  Catalogue (Liikennedatakatalogi).
- **Authority:** **Fintraffic** (the Finnish state-owned traffic-control company), operating
  Digitraffic as Finland's open-data access point for road, rail, and marine traffic data — including
  the AFIR recharging-network endpoints covered here.
- **AFIR status:** Digitraffic already publishes a **DATEX II v3.6** endpoint for AFIR
  recharging-network data alongside a GeoJSON one, ahead of the EU-wide 14 April 2026 mandatory-use
  date (see [`../README.md#afir`](../README.md#afir)) — Finland is, together with Germany, one of the
  countries with a concretely citable DATEX II AFIR endpoint at the time of writing.

## Useful links

- **AFIR overview page:** <https://www.digitraffic.fi/en/road-traffic/afir/>
- **API/technical docs:** described on the same AFIR overview page; endpoints observed there:
  - `/api/charging-network/v1/locations` — GeoJSON, static location data.
  - `/api/charging-network/v1/locations/datex2-3.6` — the **DATEX II v3.6** equivalent.
  - `/api/charging-network/v1/locations/statuses` — real-time availability status.
  - `/api/charging-network/v1/operators` — operator metadata.
  - `/api/charging-network/v1/tariffs` — tariff/pricing data.
  - MQTT (real-time push): `wss://afir.digitraffic.fi:443/mqtt` (production),
    `wss://afir-test.digitraffic.fi:443/mqtt` (test).
- **Terms of service:** <https://www.digitraffic.fi/en/terms-of-service/>

## Available datasets

- **Digitraffic AFIR / charging-network data**
  - **Format:** **DATEX II v3.6** (`/locations/datex2-3.6`) and GeoJSON (`/locations`); tariff and
    MQTT payloads in JSON.
  - **Licence:** **Creative Commons 4.0 BY (CC BY 4.0)** — Fintraffic's stated licence for all
    Digitraffic open data, per its
    [Terms of use](https://www.fintraffic.fi/en/fintraffic/terms-use); attribution format suggested
    by Fintraffic: "Source: Fintraffic / digitraffic.fi, license CC 4.0 BY".
  - **Update frequency:** paginated location snapshots are regenerated **every minute**; real-time
    status and MQTT push data update continuously.
  - **Access:** open, no registration mentioned for the REST endpoints; MQTT has separate
    production/test hosts.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources beyond the endpoints listed above at the time of writing.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/finland/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/finland/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
