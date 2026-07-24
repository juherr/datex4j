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
  recharging-network data alongside a GeoJSON one, ahead of the EU-wide date from which DATEX II
  becomes the mandatory format for the AFIR Article 20(2) charging-point data — 14 April 2026, under
  [Commission Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en)
  (the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025; see
  [`../README.md#afir`](../README.md#afir)) — Finland is, together with Germany, one of the countries
  with a concretely citable DATEX II AFIR endpoint at the time of writing.

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
  - **Committed in this repository:** a **trimmed excerpt** of the `datex2-3.6` feed (one site) is
    committed as the integration suite's **first real-world dataset**, at
    [`datex4j-integration-tests/.../datasets/finland/afir-messagecontainer.v3_6.json`](../../../datex4j-integration-tests/src/test/resources/datasets/finland/afir-messagecontainer.v3_6.json).
    The feed serves conformant DATEX II JSON (not XML), so the harness round-trips it through
    `datex4j-json`. See its [dataset README](../../../datex4j-integration-tests/src/test/resources/datasets/finland/README.md)
    for provenance, attribution and the trimming.

## Other Fintraffic DATEX II feeds (road traffic, beyond AFIR)

Beyond the AFIR charging feed, Digitraffic publishes general **road-traffic** data as DATEX II — a
useful test surface for the `situation`, `srti` and `roadTrafficData` sides of the library.
Verified live at the time of writing (base host `tie.digitraffic.fi`, licence **CC BY 4.0**):

| Feed | URL | DATEX II | Root |
|---|---|---|---|
| Roadworks | `/api/traffic-message/v2/roadworks/datex2-3.5.xml` | **v3.5** XML | `SituationPublication` |
| Traffic announcements | `/api/traffic-message/v2/traffic-announcements/datex2-3.5.xml` | v3.5 XML | `SituationPublication` |
| Weight restrictions | `/api/traffic-message/v2/weight-restrictions/datex2-3.5.xml` | v3.5 XML | `SituationPublication` |
| Exempted transports | `/api/traffic-message/v2/exempted-transports/datex2-3.5.xml` | v3.5 XML | `SituationPublication` |
| Traffic data (SRTI/RTTI) | `/api/traffic-message/v2/traffic-data/datex2-3.5.xml` | v3.5 XML | `SituationPublication` |
| TMS measurement stations | `/api/tms/v1/stations/datex2` (`.xml`) and `/api/tms/v1/stations/data/datex2` (`.xml`) | v3.5 XML + JSON | measurement data |

Each `datex2-3.5.xml` feed also has a legacy `datex2-2.2.3.xml` twin (DATEX II **v2**, which this
library's v3-only model does not read).

### How to obtain (required headers)

Digitraffic rejects requests without two headers (`406` otherwise) — the same for the AFIR feed:

```bash
curl -s --compressed -H 'Digitraffic-User: <your-client-id>' \
  https://tie.digitraffic.fi/api/traffic-message/v2/roadworks/datex2-3.5.xml
```

**Version note:** these feeds are **v3.5**, whereas the bundled model is v3.6/v3.7. They share the
`http://datex2.eu/schema/3/…` namespaces and `modelBaseVersion="3"`, so they parse, but strict XSD
validation against the bundled 3.6/3.7 schemas may report version-drift differences; treat them as
read/parse inputs rather than clean round-trip fixtures until validated.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources beyond the endpoints listed above at the time of writing.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/finland/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/finland/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
