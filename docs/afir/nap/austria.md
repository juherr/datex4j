# Austria

> Last verified: 2026-07-25.

This page records Austria's verified NAP, AFIR datasets, access constraints, and test coverage.

## General information

- **Country:** Austria
- **National Access Point (NAP):** [Mobilitätsdaten Österreich](https://mobilitaetsdaten.gv.at/en)
  ("Mobilitydata Austria"), Austria's national access point for mobility data, implementing the
  national ITS law.
- **Authority:** managed and hosted by **AustriaTech GmbH** as a neutral body, co-financed by the
  European Climate, Infrastructure and Environment Executive Agency (CINEA) — per the platform's
  ["About" page](https://mobilitaetsdaten.gv.at/en/about-mobilitydatagvat).
- **AFIR status:** the platform's data catalogue lists a "Filling and charging stations" category
  covering location and availability of charging points. DATEX II becomes the mandatory EU-wide
  format for the AFIR Article 20(2) charging-point data from 14 April 2026 under
  [Commission Implementing Regulation (EU) 2025/655][afir-implementation];
  the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025 (see
  [`../README.md#afir`](../README.md#afir)). **No dataset in that category was confirmed to already
  be published in DATEX II format** at the time of writing — see "Available datasets" below.

## Useful links

- **NAP:** <https://mobilitaetsdaten.gv.at/en>
- **Data catalogue (filling and charging stations filter):**
  <https://mobilitaetsdaten.gv.at/en/data?filter=940>
- **Downloads / general licensing info:** <https://mobilitaetsdaten.gv.at/en/downloads>

## Available datasets

### ÖAMTC charging stations

This dataset provides geodata for charging points operated by ÖAMTC (Austria's automobile club)
together with roaming partners.

- **Format:** **JSON**, delivered via a REST API requiring **OAuth2** authentication — **not
  DATEX II** at the time of writing.
- **License:** not stated in the catalogue listing reviewed. The platform's general downloads page
  indicates Austria's standard open-data license (**Creative Commons, typically CC-BY**) is used
  for many datasets, but this was not confirmed specifically for the ÖAMTC entry.
- **Update frequency:** not specified in the catalogue listing.

- Several other datasets in the same filtered category describe car-sharing services with charging
  availability (e.g. tim-Zentralraum locations, floMOBIL, tim Graz, WienMobil) rather than dedicated
  AFIR recharging-point publications; none of them were confirmed as DATEX II.
- **No dataset confirmed as a DATEX II AFIR recharging publication** was found in Austria's catalogue
  at the time of writing.

## Other DATEX II feeds (road traffic, beyond AFIR)

Unlike its AFIR/charging catalogue, Austria's road-traffic side **does** publish DATEX II —
**ASFINAG** (the motorway/expressway operator) runs a single access point for its real-time traffic
data in DATEX II format, surfaced through mobilitaetsdaten.gv.at and delivered from ASFINAG's own
content portal.

- **Publisher / access point:** ASFINAG, via
  [contentportal.asfinag.at](https://contentportal.asfinag.at/) (data endpoint
  `https://contentportal.asfinag.at/data`), catalogued on mobilitaetsdaten.gv.at.
- **Domains (per the mobilitaetsdaten.gv.at catalogue):** traffic events ("Verkehrsmeldungen zu
  ungeplanten und sicherheitsrelevanten Ereignissen", modelled as DATEX II `Situation` /
  `SituationRecord`), planned events / roadworks, variable message signs
  ("Wechselverkehrszeichen"), section travel times, and toll information — spanning the
  situations, VMS, roadworks and measurement sides of the library. There is also an
  [Austrian SRTI reference profile](https://repo.datex2.eu/implementations/profile_directory/austrian-reference-profile-srti).
- **Format / version:** DATEX II XML (ASFINAG's own profile; `datex2` documentation at
  [contentportal.asfinag.at/about/datex2](https://contentportal.asfinag.at/about/datex2)). The
  mobilitaetsdaten catalogue also links a reduced sample (`en_asfinag_plannedevents_v4_0_reduced.xml`).
- **License (verified from the mobilitaetsdaten DCAT):** an ASFINAG **CC BY 4.0** variant
  (`contentportal.asfinag.at/assets/licenses/cc-by-40-asf/…`).
- **Update frequency:** 1 minute (pull, HTTP/HTTPS).
- **Access:** **registration-gated.** The ASFINAG content portal requires a (single) registration /
  contract before the DATEX II endpoint can be consumed; probing `https://contentportal.asfinag.at/`
  and its `…/data` path returns only the portal's SPA shell, and **no anonymous DATEX II endpoint
  was confirmed**. The mobilitaetsdaten.gv.at catalogue metadata itself (e.g.
  `https://www.mobilitydata.gv.at/api/mobility_dcat/de?organisation=ASFINAG`) is reachable
  anonymously, but it only points back to the gated content-portal endpoint.

**How to obtain:** register at [contentportal.asfinag.at](https://contentportal.asfinag.at/) to
obtain access to the DATEX II data interface, then pull the desired publication from
`https://contentportal.asfinag.at/data`. Nothing is committed to
[`datasets/austria/`](../../../datex4j-integration-tests/src/test/resources/datasets/austria) because
no anonymous, reproducible fetch was found.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources at the time of writing beyond the ASFINAG profile noted above.

## See also

- [Austria fixture metadata](../../../datex4j-integration-tests/src/test/resources/datasets/austria/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)

[afir-implementation]: https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en
