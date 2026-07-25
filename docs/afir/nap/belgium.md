# Belgium

> Last verified: 2026-07-25.

This page records Belgium's verified NAP, AFIR datasets, access constraints, and test coverage.

## General information

- **Country:** Belgium
- **National Access Point (NAP):** [transportdata.be](https://transportdata.be) — Belgium's National
  Access Point for Intelligent Transport Systems (ITS) information, launched February 2020 under the
  European ITS Directive.
- **Authority:** developed and managed by the **National Geographic Institute (NGI)**, a Belgian
  semi-governmental body under the Ministry of Defence's supervision, "at the request and on behalf
  of the ITS steering committee" (federal and regional transport authorities) — per
  [transportdata.be's "About" page](https://transportdata.be/en/pages/about).
- **AFIR status:** transportdata.be already lists at least one operator's charging-infrastructure
  dataset published directly in **DATEX II XML**, ahead of the EU-wide date from which DATEX II
  becomes the mandatory format for the AFIR Article 20(2) charging-point data — 14 April 2026, under
  [Commission Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en)
  (the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025; see
  [`../README.md#afir`](../README.md#afir)); other operators on the same portal currently publish in
  OCPI or plain JSON instead.

## Useful links

- **NAP:** <https://transportdata.be>
- **About / operator:** <https://transportdata.be/en/pages/about> — notes the platform "is not about
  open data" but an access point; per-dataset terms of use apply.
- **Confirmed DATEX II dataset:**
  <https://transportdata.be/en/dataset/indigo-open-data-evcharging> ("Static dataset – Electric
  Vehicle Charging Stations (DATEX II format)").
Other AFIR-related datasets on the same portal use formats other than DATEX II:

- EnergyVision Public Charging Network (AFIR / OCPI 2.2.1):
  `/en/dataset/energyvision-public-charging-network-locations-afir-ocpi-2-2-1`
- Public charging infrastructure static dataset, selected CPOs (AFIR), Eco-Movement (JSON):
  `/en/dataset/afir-static-dataset-selected-cpos`

## Available datasets

### Indigo — static EV charging-station dataset

- **URL (dataset page):** <https://transportdata.be/en/dataset/indigo-open-data-evcharging>
- **URL (direct file download):**
  <https://transportdata.be/dataset/27f1357d-71ee-48cb-84a1-96f3f4f034b8/resource/d4bc8ddd-c80f-4330-98e5-d86e5b2147c3/download/indigo-data-evcharging-static-datexii.xml>
- **Format:** DATEX II XML (static publication); the exact DATEX II minor version is not stated on
  the dataset page.
- **License:** **not stated** on the dataset page; transportdata.be's own "About" page explicitly
  says the portal "is not about open data" — confirm terms of use with Group INDIGO
  (`data.fr@group-indigo.com`) before redistributing this file.
- **Update frequency:** described as updated on weekdays (Monday–Friday); the page's quality
  section separately says "Daily".
- **Coverage:** Belgium-wide (Group INDIGO's charging network), covering location, operator
  identification, connector types and power specifications.

## Other DATEX II feeds (road traffic, beyond AFIR)

Beyond the AFIR/EV-charging datasets above, the Flemish traffic-control centre publishes a **fully
open, anonymous DATEX II v3 road-traffic feed** — the richest openly-reachable DATEX II source found
for Belgium, and a good test surface for the `situation`/`roadworks`/`srti` sides of the library.

### Verkeerscentrum Vlaanderen DATEX II v3 feed

- **Publisher:** Agentschap Wegen en Verkeer (Agency for Roads and Traffic), via the Flemish
  Traffic Centre ([verkeerscentrum.be/data](https://www.verkeerscentrum.be/data)); also catalogued
  on [transportdata.be](https://transportdata.be/dataset/datex2-feed-verkeerscentrum-vlaanderen-full-version)
  and [data.gov.be](https://data.gov.be/en/datasets/9e90a52f-dfbb-4bd0-bb50-e379ebba765c).
- **URL (verified live):** <https://www.verkeerscentrum.be/uitwisseling/datex2v3full>
- **Format / version (verified):** DATEX II **v3** XML — `HTTP 200`, ~360 KB,
  `modelBaseVersion="3"`, and DATEX II v3 namespace URIs,
  root `d2:payload` carrying `situation` records (e.g. `RoadOrCarriagewayOrLaneManagement`). It
  carries a national-extension namespace (`http://verkeerscentrum.be/tcc.backend/xsd/datex2/v3`)
  and `publicationCreator` `nationalIdentifier` `BETICV`. A DATEX II **v2** twin and an OTAP feed
  also exist (the v2 one this library's v3-only model does not read).
- **Content:** real-time traffic situations on Flemish motorways (and some regional roads) —
  incidents, current roadworks, special events and traffic-flow information; DATEX II v3 adds GML
  (Lambert-72) coordinates and active diversion/calamity routes.
- **License:** described by the Traffic Centre as **free open data** for developers of mobility/
  navigation apps; the exact license text is not restated on the download endpoint, and
  transportdata.be applies per-dataset terms — confirm before redistributing a captured file.
- **Update frequency:** the XML snapshot reflects the control-room situation **every minute**.
- **Access:** **anonymous**, no key. The endpoint even sets `Access-Control-Allow-Origin: *`.

```bash
curl -sS --compressed \
  https://www.verkeerscentrum.be/uitwisseling/datex2v3full -o be-vlaanderen-situations.xml
```

**Version note:** the feed is DATEX II **v3** with a Verkeerscentrum national extension, whereas the
bundled model is v3.6/v3.7. It shares the `http://datex2.eu/schema/3/…` namespaces and
`modelBaseVersion="3"`, so it parses, but strict XSD validation against the bundled schemas may
report version-drift and extension differences; treat it as a read/parse input rather than a clean
round-trip fixture until validated.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources beyond the datasets listed above at the time of writing.

## See also

- [Belgium fixture metadata](../../../datex4j-integration-tests/src/test/resources/datasets/belgium/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
