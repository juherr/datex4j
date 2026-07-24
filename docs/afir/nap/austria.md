# Austria

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
  format for the AFIR Article 20(2) charging-point data from 14 April 2026 under [Commission
  Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en);
  the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025 (see
  [`../README.md#afir`](../README.md#afir)). **No dataset in that category was confirmed to already
  be published in DATEX II format** at the time of writing — see "Available datasets" below.

## Useful links

- **NAP:** <https://mobilitaetsdaten.gv.at/en>
- **Data catalogue (filling and charging stations filter):**
  <https://mobilitaetsdaten.gv.at/en/data?filter=940>
- **Downloads / general licensing info:** <https://mobilitaetsdaten.gv.at/en/downloads>

## Available datasets

- **ÖAMTC E-Ladestationen / ÖAMTC E-Power charging stations** — geodata for charging points operated
  by ÖAMTC (Austria's automobile club) together with roaming partners.
  - **Format:** **JSON**, delivered via a REST API requiring **OAuth2** authentication — **not
    DATEX II** at the time of writing.
  - **Licence:** not stated in the catalogue listing reviewed. The platform's general downloads page
    indicates Austria's standard open-data licence (**Creative Commons, typically CC-BY**) is used
    for many datasets, but this was not confirmed specifically for the ÖAMTC entry.
  - **Update frequency:** not specified in the catalogue listing.
- Several other datasets in the same filtered category describe car-sharing services with charging
  availability (e.g. tim-Zentralraum locations, floMOBIL, tim Graz, WienMobil) rather than dedicated
  AFIR recharging-point publications; none of them were confirmed as DATEX II.
- **No dataset confirmed as a DATEX II AFIR recharging publication** was found in Austria's catalogue
  at the time of writing.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources at the time of writing.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/austria/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/austria/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
