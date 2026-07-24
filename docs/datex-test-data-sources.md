<!--
  Copyright 2026 the datex4j authors. Licensed under the Apache License, Version 2.0.
-->

# Open DATEX II data sources for testing datex4j

A catalogue of **real, openly-licensed DATEX II data sources** usable to test `datex4j` across
**all** its domains — not only AFIR/EV-charging. It complements the AFIR-specific knowledge base
under [`afir/`](./afir/README.md) and the committed fixtures under
[`datex4j-integration-tests`](../datex4j-integration-tests/src/test/resources/datasets/README.md).

## Selection principles

A source is worth cataloguing here when it is:

1. **Official** — published by a road authority / National Access Point (NAP), not a scrape.
2. **DATEX II v3** — the bundled model is **v3.6 / v3.7** (`http://datex2.eu/schema/3/…`,
   `modelBaseVersion="3"`). v2.x feeds are noted but the v3-only model cannot read them.
3. **Anonymously reachable** — no account/token, so it can be documented as a reproducible fetch.
4. **Clearly licensed** — a file may be **committed** as a fixture only under a redistribution-
   permitting licence (CC0, CC BY, an open government licence); otherwise it is **documented, not
   committed** (see the datasets [policy](../datex4j-integration-tests/src/test/resources/datasets/README.md#policy-what-gets-committed)).

> **Version-drift reality (verified).** Real NAP feeds are often v3.5 or a vendor-specific v3.x and
> may carry national extensions. They generally **parse** into the model, but **strict XSD
> validation against the bundled 3.6/3.7 schemas can fail**, and Exchange-2020 `MessageContainer`
> documents with national extensions may fail to unmarshal. Treat most of these as real-world
> **read/parse** inputs for hardening rather than clean round-trip fixtures. Where an exact-version,
> extension-free example is needed, generate one with the DATEX II wizard (below).

## Sources at a glance

| Source | Country | Licence | Access | DATEX II | Domains |
|---|---|---|---|---|---|
| [NDW open data](#ndw-netherlands--cc0--all-domains) | NL | **CC0** | anonymous | v3 (+ nat. ext.) | situations, SRTI, parking, VMS, roadworks, measurement, emission zones |
| [Fintraffic / Digitraffic](#fintraffic-finland--cc-by-40) | FI | CC BY 4.0 | anonymous¹ | v3.6 (AFIR JSON), v3.5 (traffic XML) | EV charging (AFIR), situations/SRTI, roadTrafficData (TMS) |
| [Verkeerscentrum Vlaanderen](#verkeerscentrum-vlaanderen-belgium--open-data) | BE | free open data² | anonymous | v3 (+ nat. ext.) | situations, roadworks, events (Flemish motorways) |
| [DiaLog](#dialog-france--licence-ouverte-20) | FR | Licence Ouverte 2.0 | anonymous | v3 | traffic regulations (TRO) |
| [Statens vegvesen](#statens-vegvesen-norway--registration-gated) | NO | NLOD | **registration** | v3.1 (v2.3 legacy) | situations, measurement, travel time, CCTV, weather |
| [Other NAPs (gated)](#other-naps--registration-gated) | DE/DK/AT/SE | mixed | **registration** | v3.x / v2.x | situations, roadworks, VMS, measurement, AFIR |
| [DATEX II wizard](#datex-ii-wizard--synthetic-but-exact-version) | — | tool | anonymous | any v3.x you pick | any (you assemble the profile) |

¹ Digitraffic requires a `Digitraffic-User` header and gzip (`--compressed`), else `406`.
² Exact licence text not restated on the download endpoint; confirm before redistributing.

## NDW (Netherlands) — CC0 — all domains

The richest openly-licensed DATEX II v3 source found. Portal: <https://opendata.ndw.nu/>. Licence
**Creative Commons Zero** ([copyright statement](https://www.ndw.nu/copyright)) — public domain,
redistribution allowed, no attribution required. Anonymous; files are gzip (`.xml.gz`) except where
noted.

| File | Data | Library module | Root (✓ = opened & confirmed) |
|---|---|---|---|
| `actueel_beeld.xml.gz` (~3 MB) | Current situations | `datex4j-domain-traffic` | ✓ `MessageContainer` → `SituationPublication` |
| `veiligheidsgerelateerde_berichten_srti.xml.gz` | Safety messages (SRTI) | `datex4j-domain-srti` | `SituationPublication` |
| `planningsfeed_wegwerkzaamheden_en_evenementen.xml.gz` | Roadworks & events | `datex4j-domain-traffic` | `SituationPublication` |
| `Truckparking_Parking_Status.xml` (~18 KB, plain) | Truck-parking availability | `datex4j-domain-parking` | ✓ `ParkingStatusPublication` |
| `trafficspeed.xml.gz`, `measurement.xml.gz`, `measurement_current.xml.gz`, `traveltime.xml.gz` | Speed / measurement / travel time | (`roadTrafficData`) | measurement publication |
| `Matrixsignaalinformatie.xml.gz`, `dynamische_route_informatie_paneel.xml.gz` | Matrix signs / route panels (VMS) | (`vms`) | VMS publication |
| `emissiezones.xml.gz` | Emission zones | `datex4j-domain-uvar` (related) | — |
| `tijdelijke_verkeersmaatregelen_*.xml.gz` | Temporary closures / speed limits | `datex4j-domain-traffic` | — |
| `charging_point_locations*.geojson.gz`, `..._ocpi.json.gz` | EV charging | — | GeoJSON / OCPI, **not** DATEX II |

```bash
curl -s http://opendata.ndw.nu/Truckparking_Parking_Status.xml -o parking.xml           # plain
curl -s http://opendata.ndw.nu/actueel_beeld.xml.gz | gunzip > situations.xml            # gzipped
```

**Verified compatibility.** `Truckparking_Parking_Status.xml` **parses** into a
`ParkingStatusPublication`; strict XSD validation **fails** on version/prefix drift (NDW emits
`targetClass="par:ParkingTable"` where the bundled schema fixes `prk:ParkingTable`), and the
situations `MessageContainer` **fails to unmarshal** (national extensions + drift). See the
[Netherlands NAP page](./afir/nap/netherlands.md#other-ndw-datex-ii-feeds-open-data-portal).

## Fintraffic (Finland) — CC BY 4.0

Digitraffic (<https://www.digitraffic.fi/>). Licence **CC BY 4.0**, attribution "Source: Fintraffic /
digitraffic.fi, license CC 4.0 BY". Anonymous but **requires** `Digitraffic-User` header + gzip.

- **AFIR / EV charging (v3.6, conformant JSON)** — `https://afir.digitraffic.fi/api/charging-network/v1/locations/datex2-3.6`.
  A trimmed excerpt is **already committed** as the suite's first real dataset
  ([finland](../datex4j-integration-tests/src/test/resources/datasets/finland/README.md)); the full
  feed has an opt-in read test.
- **Road traffic (v3.5, XML)** — `SituationPublication` feeds under
  `https://tie.digitraffic.fi/api/traffic-message/v2/{roadworks,traffic-announcements,weight-restrictions,exempted-transports,traffic-data}/datex2-3.5.xml`,
  plus TMS measurement `…/api/tms/v1/stations/data/datex2[.xml]`. See the
  [Finland NAP page](./afir/nap/finland.md#other-fintraffic-datex-ii-feeds-road-traffic-beyond-afir).

```bash
curl -s --compressed -H 'Digitraffic-User: datex4j-test' \
  https://tie.digitraffic.fi/api/traffic-message/v2/roadworks/datex2-3.5.xml -o fi-roadworks.xml
```

## Verkeerscentrum Vlaanderen (Belgium) — open data

The Flemish Traffic Centre publishes a fully open, **anonymous DATEX II v3** road-traffic feed —
the richest openly-reachable DATEX II source found for Belgium.

- **URL (verified live):** <https://www.verkeerscentrum.be/uitwisseling/datex2v3full> — `HTTP 200`,
  `application/xml`, ~360 KB, `modelBaseVersion="3"`, root `d2:payload` with `situation` records
  (e.g. `RoadOrCarriagewayOrLaneManagement`); national-extension namespace
  `http://verkeerscentrum.be/tcc.backend/xsd/datex2/v3`, creator `nationalIdentifier` `BETICV`.
- **Content:** real-time incidents, roadworks, events and traffic flow on Flemish motorways (some
  regional roads); v3 adds GML (Lambert-72) coordinates and active diversion routes. Updated every
  minute. Also catalogued on
  [transportdata.be](https://transportdata.be/dataset/datex2-feed-verkeerscentrum-vlaanderen-full-version)
  and [data.gov.be](https://data.gov.be/en/datasets/9e90a52f-dfbb-4bd0-bb50-e379ebba765c). A DATEX II
  **v2** twin and an OTAP feed also exist (v2 not read by this v3-only model).
- **Licence:** described as **free open data**; exact licence text not restated on the endpoint —
  confirm before committing a captured file. Anonymous, `Access-Control-Allow-Origin: *`.

```bash
curl -sS --compressed https://www.verkeerscentrum.be/uitwisseling/datex2v3full -o be-situations.xml
```

See the [Belgium NAP page](./afir/nap/belgium.md#other-datex-ii-feeds-road-traffic-beyond-afir).

## DiaLog (France) — Licence Ouverte 2.0

France's national traffic-regulation database (DiaLog, MTES/MCT), published on
transport.data.gouv.fr, serves DATEX II **v3** anonymously.

- **URL (verified live):** <https://dialog.beta.gouv.fr/api/regulations.xml> — `HTTP 200`,
  `text/xml`, ~10.7 MB, `modelBaseVersion="3"`, `xmlns="http://datex2.eu/schema/3/trafficRegulation"`,
  root `TrafficRegulationPublication` (national extension under `github.com/MTES-MCT/dialog`).
- **Licence:** **Licence Ouverte 2.0** (Etalab). Anonymous. Note DiaLog states the published
  restrictions have no legal value (only municipal ordinances do).
- The other French NAP road-traffic feeds (TIPI/Bison Futé real-time speeds and events) are DATEX II
  **v2.2.2**, which this v3-only model does not read — see the
  [France NAP page](./afir/nap/france.md#other-datex-ii-feeds-road-traffic-beyond-afir).

```bash
curl -sS --compressed https://dialog.beta.gouv.fr/api/regulations.xml -o fr-dialog-tro.xml
```

## Statens vegvesen (Norway) — registration-gated

Norwegian Public Roads Administration DATEX node. Data under the **Norwegian Licence for Open
Government Data (NLOD)**, but access **requires registration** — the v3.1 pull endpoints answer
`401` anonymously, so this is a **document-only** source (not fetchable in an offline test).

- **Overview / access:** <https://www.vegvesen.no/en/about-us/about-us/open-data/datex/>
- **v3.1 base (auth required):** `https://datex-server-get-v3-1.atlas.vegvesen.no/datexapi/…`
- Data is v3.1 (weather, travel times, CCTV, situations), with a v2.3 legacy set.

## Other NAPs — registration-gated

Several more NAPs publish DATEX II but behind registration, so they are **document-only** (no
reproducible anonymous fetch). Each has a per-country NAP page with the verified probe results.

| Country | NAP / operator | DATEX II | Access reality (verified) | NAP page |
|---|---|---|---|---|
| Germany | Mobilithek (roadworks/traffic-data profiles); Autobahn GmbH | v3 | Mobilithek needs account + per-offer subscription over **mTLS**; Autobahn REST is anonymous but **JSON, not DATEX II** | [germany](./afir/nap/germany.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Denmark | Vejdirektoratet Dataudveksler | v3 | DCAT catalogue anonymous, but every `accessURL` is a login-gated portal page; **no anonymous data endpoint** | [denmark](./afir/nap/denmark.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Austria | ASFINAG (via mobilitaetsdaten.gv.at) | v3 (ASFINAG profile) | content portal requires registration/contract; licence CC BY 4.0 (ASFINAG variant); catalogue metadata anonymous only | [austria](./afir/nap/austria.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Sweden | Trafikverket | v3.x (DATEX node); open API is non-DATEX | open API `401` without key; DATEX node needs a signed interchange agreement | [sweden](./afir/nap/sweden.md#other-datex-ii-feeds-road-traffic-beyond-afir) |

## DATEX II wizard — synthetic but exact-version

When an **exact-version, extension-free** validating example is needed (to avoid the drift above),
assemble and export one with the official schema-generation wizard at
<https://webtool.datex2.eu/wizard>, or reuse the project's own committed synthetic AFIR fixture at
[`datasets/synthetic/afir-recharging/table.xml`](../datex4j-integration-tests/src/test/resources/datasets/synthetic/afir-recharging/table.xml).

## Coverage by library domain

| Domain / module | Open source to test it |
|---|---|
| `datex4j-domain-evcharging` (AFIR) | Fintraffic AFIR (committed), NDW OCPI (non-DATEX) |
| `datex4j-domain-traffic` (situations) | NDW `actueel_beeld`, NDW roadworks, Fintraffic traffic-announcements, Verkeerscentrum Vlaanderen (BE) |
| `datex4j-domain-srti` | NDW SRTI, Fintraffic `traffic-data` |
| `datex4j-domain-parking` | NDW `Truckparking_Parking_Status` |
| `datex4j-domain-uvar` | NDW `emissiezones` (related), temporary traffic measures |
| Traffic regulation / TRO (model-level) | DiaLog (FR) `TrafficRegulationPublication` |
| VMS / roadTrafficData (model-level) | NDW matrix signs, NDW measurement/speed, Fintraffic TMS |

## See also

- AFIR knowledge base: [`afir/README.md`](./afir/README.md) and per-country NAP pages under
  [`afir/nap/`](./afir/nap).
- Official DATEX II resources: [`afir/official-datex-resources.md`](./afir/official-datex-resources.md).
- Committed fixtures & dataset policy:
  [`datex4j-integration-tests/.../datasets/README.md`](../datex4j-integration-tests/src/test/resources/datasets/README.md).
