# Belgium

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
- **Other AFIR-related datasets on the same portal** (formats other than DATEX II, listed for
  completeness):
  - EnergyVision Public Charging Network (AFIR / OCPI 2.2.1):
    `/en/dataset/energyvision-public-charging-network-locations-afir-ocpi-2-2-1`
  - Public charging infrastructure static dataset, selected CPOs (AFIR), Eco-Movement (JSON):
    `/en/dataset/afir-static-dataset-selected-cpos`

## Available datasets

- **Indigo — static EV charging-station dataset**
  - **URL (dataset page):** <https://transportdata.be/en/dataset/indigo-open-data-evcharging>
  - **URL (direct file download):**
    <https://transportdata.be/dataset/27f1357d-71ee-48cb-84a1-96f3f4f034b8/resource/d4bc8ddd-c80f-4330-98e5-d86e5b2147c3/download/indigo-data-evcharging-static-datexii.xml>
  - **Format:** DATEX II XML (static publication); the exact DATEX II minor version is not stated on
    the dataset page.
  - **Licence:** **not stated** on the dataset page; transportdata.be's own "About" page explicitly
    says the portal "is not about open data" — confirm terms of use with Group INDIGO
    (`data.fr@group-indigo.com`) before redistributing this file.
  - **Update frequency:** described as updated on weekdays (Monday–Friday); the page's quality
    section separately says "Daily".
  - **Coverage:** Belgium-wide (Group INDIGO's charging network), covering location, operator
    identification, connector types and power specifications.

## Notes

No further country-specific DATEX II extensions or national-identifier conventions could be
confirmed from official sources beyond the dataset listed above at the time of writing.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/belgium/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/belgium/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
