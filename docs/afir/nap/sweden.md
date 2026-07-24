# Sweden

## General information

- **Country:** Sweden
- **National Access Point (NAP):** [trafficdata.se](https://trafficdata.se) (www.trafficdata.se),
  operational since 2017 per the ITS Directive's delegated regulations.
- **Authority:** operated by **Trafikverket** (the Swedish Transport Administration) — the
  site itself states it is "provided by [Swedish Transport Administration]". For charging-point
  data specifically, **Energimyndigheten** (the Swedish Energy Agency) maintains the underlying
  register.
- **AFIR status:** Sweden is reported as one of the EU's operational NAPs. DATEX II becomes the
  mandatory EU-wide format for the AFIR Article 20(2) charging-point data from 14 April 2026 under
  [Commission Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en);
  the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025 (see
  [`../README.md#afir`](../README.md#afir)). The charging-point dataset available today (see below)
  is API-based and its format is not documented as DATEX II on the catalogue page reviewed.

## Useful links

- **NAP:** <https://trafficdata.se>
- **Dataset entry:** <https://trafficdata.se/dataset/charging-points-for-electric-vehicles-nobil>
- **Developer portal (Energimyndigheten):**
  <https://www.energimyndigheten.se/klimat/transporter/laddinfrastruktur/registrera-din-laddstation/utvecklare/>
- **NOBIL database (underlying source, Norway-developed):** <https://info.nobil.no/english> — NOBIL
  itself is developed and maintained by the Norwegian Electric Vehicle Association; Sweden's
  Energy Agency runs its own register based on regular extracts from it.

## Available datasets

- **Charging points for electric vehicles — NOBIL** (listed on trafficdata.se)
  - **URL:** <https://trafficdata.se/dataset/charging-points-for-electric-vehicles-nobil>
  - **Format:** **API** (the catalogue entry describes it as "NOBIL database for charging points for
    electric vehicles, API"); DATEX II is **not** stated as the format on this entry.
  - **Licence:** listed as **"License not specified"** on the trafficdata.se catalogue entry itself.
    Note that registering directly for the underlying NOBIL API (via info.nobil.no) requires
    accepting Creative Commons terms of use, per NOBIL's own documentation — but that is a separate
    registration path from the trafficdata.se catalogue listing, and the licence terms of the
    Swedish register specifically were not confirmed.
  - **Update frequency:** **daily**, per the catalogue entry.
  - **Coverage:** location and conditions of charging points, and availability of charging points for
    electric vehicles, for Sweden.

## Other DATEX II feeds (road traffic, beyond AFIR)

For road-traffic data (situations, roadworks, measurement), Trafikverket offers two routes, **both
gated** — no anonymous DATEX II endpoint was found.

- **Open API** — `https://api.trafikinfo.trafikverket.se/v2/data.{xml,json}`. A POST-based query API
  covering situations, roadworks, weather stations, travel times, etc. It is **not** DATEX II (it
  uses Trafikverket's own XML/JSON schema), and it **requires a free API key**: probing it without a
  key returns `HTTP 401` with `<MESSAGE>Invalid authentication</MESSAGE>`. Keys are obtained by
  registering on Trafikverket's data portal (see the
  [open-API page](https://www.trafikverket.se/en/startpage/operations/Operations-road/Traffic-information/Real-time-traffic-information/)).
- **DATEX II node** — Trafikverket delivers traffic information in **DATEX II XML** via a separate
  data-exchange node. Access is **free of charge but requires a signed interchange agreement** with
  Trafikverket before credentials are issued (both companies and private persons may apply); this is
  described on the same real-time-traffic-information pages. No anonymous URL is published.

**How to obtain:** for the DATEX II feed, apply for the interchange agreement via Trafikverket's Data
Exchange portal ("Get started"); for the (non-DATEX) open API, register for an API key. Because both
are gated, nothing is committed to
[`datasets/sweden/`](../../../datex4j-integration-tests/src/test/resources/datasets/sweden).

## Notes

- Sweden's identification codes for charging points are reported (by Energimyndigheten, in the
  context of its "Identification Registration Organisation", IDRO) to be aligned with those defined
  by the **OCPI** protocol, which NOBIL also uses to provide dynamic data — see
  [Energimyndigheten's IDRO page](https://www.energimyndigheten.se/en/cooperation/charging-infrastructure-in-sweden/identification-registration-organisation-idro/).
  No DATEX II-specific national extension could be confirmed beyond this.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/sweden/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/sweden/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
