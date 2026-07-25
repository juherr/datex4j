<!--
  Copyright 2026 the datex4j authors. Licensed under the Apache License, Version 2.0.
-->

# Open DATEX II data sources for testing datex4j

> Last verified: 2026-07-25. This page is the cross-domain source index; country-specific access
> details live under [`docs/afir/nap/`](./afir/nap/), and immutable fixture metadata lives beside
> each committed dataset.

A catalogue of **real, openly-licensed DATEX II data sources** usable to test `datex4j` across
**all** its domains — not only AFIR/EV-charging. It complements the AFIR-specific knowledge base
under [`afir/`](./afir/README.md) and the committed fixtures under
[`datex4j-integration-tests`](../datex4j-integration-tests/src/test/resources/datasets/README.md).

## Selection principles

A source is worth cataloguing here when it is:

1. **Official** — published by a road authority / National Access Point (NAP), not a scrape.
2. **A bundled DATEX II version** — the SDK includes v2.0–v2.3 and v3.0–v3.7. Exact-minor XSD
   validation still depends on the producer's schema/profile and national extensions.
3. **Anonymously reachable** — no account/token, so it can be documented as a reproducible fetch.
4. **Clearly licensed** — a file may be **committed** as a fixture only under a redistribution-
   permitting license (CC0, CC BY, an open government license); otherwise it is **documented, not
   committed** (see the [dataset policy][dataset-policy]).

> **Version-drift reality (verified).** A DATEX II **v3 instance only declares its major version**
> (`modelBaseVersion="3"`); the exact **minor** (3.0 … 3.7) is the producer's schema/profile choice
> and is normally **not present in the document**. Parsing is cross-minor tolerant — a v3.5 feed
> parses into the v3.7 model (verified) — but **strict XSD validation needs the producer's exact
> minor**. Real feeds also carry national extensions and producer data errors: even with the whole
> v3 family (3.0–3.7) now bundled, NDW's truck-parking feed still fails strict validation — see the
> [NDW note](#ndw-netherlands--cc0--all-domains) for the two distinct, verified reasons. Treat
> cross-minor feeds as real-world
> **read/parse** inputs rather than clean round-trip fixtures; for an exact-version, extension-free
> validating example, generate one with the DATEX II wizard (below). See
> [DATEX II version coverage & gaps](#datex-ii-version-coverage--gaps) for what this means for the
> library.

## Which country offers what — at a glance

One row per source shows its domains, DATEX II version, access conditions, and current library
support. **✅** means the bundled model parses and validates the fixture. **⚠️** means parsing works
but strict XSD validation fails because of a national profile, extension, producer error, or minor
mismatch. **❌** means access is gated or no reproducible fixture is available.

| Source (country) | Feed types / domain | DATEX II version | Access | License | Library support |
|---|---|---|---|---|---|
| [NDW](#ndw-netherlands--cc0--all-domains) (NL) | situations, SRTI, roadworks, **parking**, VMS, measurement, emission zones | **v3** — mixed minors per NDW profiles | anonymous | **CC0** | ✅ situations & SRTI (`mc:messageContainer`) validate clean against v3.7; ⚠️ emission-zones drops `urbanVehicleAccessRegulation`; truck-parking strict XSD fails on a producer `targetClass` prefix error (`par:` vs fixed `prk:`) |
| [Fintraffic AFIR](#fintraffic-finland--cc-by-40) (FI) | EV charging (AFIR) | **v3.6** (conformant JSON, MessageContainer) | anonymous¹ | CC BY 4.0 | ✅ committed dataset |
| [Fintraffic traffic](#fintraffic-finland--cc-by-40) (FI) | situations, SRTI, roadworks, TMS measurement | **v3.5** (XML) | anonymous¹ | CC BY 4.0 | ✅ validates (real feed has producer data errors) |
| Fintraffic legacy twins (FI) | same | **v2.2.3** (XML) | anonymous¹ | CC BY 4.0 | ⚠️ reads with the bundled v2.3 model; the national profile fails plain-v2.3 XSD validation |
| [Verkeerscentrum Vlaanderen](#verkeerscentrum-vlaanderen-belgium--open-data) (BE) | situations, roadworks, events | **v3** (minor not declared) | anonymous | open data² | ⚠️ parses (v3.5–3.7) |
| [DiaLog](#dialog-france--open-license-20) (FR) | traffic regulation (TRO) | **v3** (minor not declared) | anonymous | Open License 2.0 | ⚠️ parses (v3.5–3.7) |
| TIPI / Bison Futé (FR) | speeds, road events, TRAFICOLOR | **v2.2.2** (XML) | anonymous | License Ouverte | ✅ committed v2.2 fixtures parse and validate |
| [Statens vegvesen](#statens-vegvesen-norway--registration-gated) (NO) | situations, measurement, travel time, CCTV, weather | **v3.1** (+ v2.3 legacy) | **registration** (401) | NLOD | ❌ gated (v3.1 is now bundled, but access is registration-only) |
| [Mobilithek / Autobahn](#other-registration-gated-national-access-points) (DE) | roadworks, traffic-data (Autobahn REST = JSON) | v3 | **registration** (mTLS) | mixed | ❌ gated |
| [Vejdirektoratet](#other-registration-gated-national-access-points) (DK) | situations, etc. | v3 | **registration** | — | ❌ gated |
| [ASFINAG](#other-registration-gated-national-access-points) (AT) | events, roadworks, VMS, travel times | v3 (ASFINAG profile) | **registration** | CC BY 4.0 (variant) | ❌ gated |
| [Trafikverket](#other-registration-gated-national-access-points) (SE) | DATEX node | v3.x | **registration** | — | ❌ gated |
| [DATEX II wizard](#datex-ii-wizard--synthetic-but-exact-version) | any (you assemble a profile) | any minor you pick | anonymous | tool | ✅ produces exact-version examples |

¹ Digitraffic requires a `Digitraffic-User` header and gzip (`--compressed`), else `406`.
² Exact license text not restated on the download endpoint; confirm before redistributing.

## DATEX II version coverage & gaps

**Bundled versions:** DATEX II **v2.0, v2.1, v2.2, v2.3** and the full v3 family **v3.0–v3.7**
(default 3.7). Each version has its own artifact and generated package tree. The committed fixtures
cover French v2.2 feeds, Finnish v2.3 and v3.5/v3.6 feeds, and several Dutch and Belgian v3 feeds.

Two constraints still prevent clean validation of every documented source:

1. **Producer profiles, extensions, and data errors.** A bundled version guarantees that the
   standard schema is available; it cannot make a national extension or non-conformant producer
   payload validate against the plain standard schema.
2. **Access gating.** Germany, Denmark, Austria, Sweden, and Norway require an account, mTLS, or an
   interchange agreement. Their feeds remain document-only until a redistributable fixture can be
   obtained.

> **Note on NDW truck-parking.** Adding v3.0 did *not* make the committed NDW truck-parking dataset
> validate, contrary to the earlier expectation. Two independently verified reasons: (a) DATEX II
> **did not publish a Parking module until v3.3**, so a `ParkingStatusPublication` is not even
> expressible in v3.0–v3.2; and (b) on every minor that *does* have Parking (v3.3–v3.7, including the
> default), the feed emits `targetClass="par:ParkingTable"` while the schema fixes that attribute to
> `prk:ParkingTable` — a producer data error, not a version gap. The dataset therefore stays
> `XML_READ_ONLY`. See the [truck-parking validation test][truck-parking-test],
> which pins both errors.

**Bottom line:** supported Java models now cover every DATEX II minor documented on this page.
Remaining gaps come from producer-specific profiles, extensions, payload errors, or access
conditions rather than a missing DATEX II family.

## NDW (Netherlands) — CC0 — all domains

The richest openly-licensed DATEX II v3 source found. Portal: <https://opendata.ndw.nu/>. License
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
`ParkingStatusPublication`, but strict XSD validation **fails against every bundled v3 minor**, for
two distinct, verified reasons: (a) DATEX II did not publish a Parking module until **v3.3**, so the
payload type is unresolvable in v3.0–v3.2 (`cvc-elt.4.2`); and (b) from v3.3 onward the payload type
resolves but the feed emits `targetClass="par:ParkingTable"` where the schema fixes that attribute to
`prk:ParkingTable` (`cvc-complex-type.3.1`) — a producer data error, not a version gap. Bundling v3.0
did **not** change this; the dataset stays read-only. See
[truck-parking validation test][truck-parking-test].
The situations and SRTI `mc:messageContainer` feeds now **read and validate cleanly** against v3.7
(zero errors): the validator compiles the `mc:messageContainer` root together with `d2:payload` for
v3.6/v3.7, so the earlier spurious "element `mc:messageContainer` not declared" root error is gone.
The `emissiezones` feed reads only its table envelope and **stays invalid**, because its zone records
use `<cz:urbanVehicleAccessRegulation>` — an element absent from every bundled v3.x schema. These
per-feed outcomes are asserted by
[v3 traffic-feed test][v3-traffic-test].
See the [Netherlands NAP page](./afir/nap/netherlands.md#other-ndw-datex-ii-feeds-open-data-portal) and
[version coverage & gaps](#datex-ii-version-coverage--gaps).

## Fintraffic (Finland) — CC BY 4.0

Digitraffic (<https://www.digitraffic.fi/>). License **CC BY 4.0**, attribution "Source: Fintraffic /
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
- **License:** described as **free open data**; exact license text not restated on the endpoint —
  confirm before committing a captured file. Anonymous, `Access-Control-Allow-Origin: *`.

```bash
curl -sS --compressed https://www.verkeerscentrum.be/uitwisseling/datex2v3full -o be-situations.xml
```

See the [Belgium NAP page](./afir/nap/belgium.md#other-datex-ii-feeds-road-traffic-beyond-afir).

## DiaLog (France) — open license 2.0

France's national traffic-regulation database (DiaLog, MTES/MCT), published on
transport.data.gouv.fr, serves DATEX II **v3** anonymously.

- **URL (verified live):** <https://dialog.beta.gouv.fr/api/regulations.xml> — `HTTP 200`,
  `text/xml`, ~10.7 MB, `modelBaseVersion="3"`, `xmlns="http://datex2.eu/schema/3/trafficRegulation"`,
  root `TrafficRegulationPublication` (national extension under `github.com/MTES-MCT/dialog`).
- **License:** **License Ouverte 2.0** (Etalab). Anonymous. Note DiaLog states the published
  restrictions have no legal value (only municipal ordinances do).
- The other French NAP road-traffic feeds (TIPI/Bison Futé real-time speeds and events) are DATEX II
  **v2.2.2**, which this v3-only model does not read — see the
  [France NAP page](./afir/nap/france.md#other-datex-ii-feeds-road-traffic-beyond-afir).

```bash
curl -sS --compressed https://dialog.beta.gouv.fr/api/regulations.xml -o fr-dialog-tro.xml
```

## Statens vegvesen (Norway) — registration-gated

Norwegian Public Roads Administration DATEX node. Data uses the Norwegian License for Open
Government Data (NLOD), but access requires registration — the v3.1 pull endpoints answer
`401` anonymously, so this is a **document-only** source (not fetchable in an offline test). The
v3.1 schema set is now bundled, so a Norwegian feed would validate once credentials are provided;
the blocker is access, not version.

- **Overview / access:** <https://www.vegvesen.no/en/about-us/about-us/open-data/datex/>
- **v3.1 base (auth required):** `https://datex-server-get-v3-1.atlas.vegvesen.no/datexapi/…`
- Data is v3.1 (weather, travel times, CCTV, situations), with a v2.3 legacy set.

## Other registration-gated national access points

Several more NAPs publish DATEX II but behind registration, so they are **document-only** (no
reproducible anonymous fetch). Each has a per-country NAP page with the verified probe results.

| Country | NAP / operator | DATEX II | Access reality (verified) | NAP page |
|---|---|---|---|---|
| Germany | Mobilithek (roadworks/traffic-data profiles); Autobahn GmbH | v3 | Mobilithek needs account + per-offer subscription over **mTLS**; Autobahn REST is anonymous but **JSON, not DATEX II** | [germany](./afir/nap/germany.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Denmark | Vejdirektoratet Dataudveksler | v3 | DCAT catalogue anonymous, but every `accessURL` is a login-gated portal page; **no anonymous data endpoint** | [denmark](./afir/nap/denmark.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Austria | ASFINAG (via mobilitaetsdaten.gv.at) | v3 (ASFINAG profile) | content portal requires registration/contract; license CC BY 4.0 (ASFINAG variant); catalogue metadata anonymous only | [austria](./afir/nap/austria.md#other-datex-ii-feeds-road-traffic-beyond-afir) |
| Sweden | Trafikverket | v3.x (DATEX node); open API is non-DATEX | open API `401` without key; DATEX node needs a signed interchange agreement | [sweden](./afir/nap/sweden.md#other-datex-ii-feeds-road-traffic-beyond-afir) |

## DATEX II wizard — synthetic but exact-version

When an **exact-version, extension-free** validating example is needed (to avoid the drift above),
assemble and export one with the official schema-generation wizard at
<https://webtool.datex2.eu/wizard>, or reuse the project's own committed synthetic AFIR fixture at
  [synthetic AFIR fixture][synthetic-fixture].

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
  [integration-test dataset catalog](../datex4j-integration-tests/src/test/resources/datasets/README.md).

[dataset-policy]: ../datex4j-integration-tests/src/test/resources/datasets/README.md#policy-what-gets-committed
[synthetic-fixture]: ../datex4j-integration-tests/src/test/resources/datasets/synthetic/afir-recharging/table.xml
[truck-parking-test]:
  ../datex4j-integration-tests/src/test/java/dev/juherr/datex4j/it/TruckParkingV30ValidationTest.java
[v3-traffic-test]:
  ../datex4j-integration-tests/src/test/java/dev/juherr/datex4j/it/Datex3TrafficFeedReadValidateTest.java
