# Denmark

> Last verified: 2026-07-25.

This page records Denmark's verified NAP, AFIR datasets, access constraints, and test coverage.

## General information

- **Country:** Denmark
- **National Access Point (NAP):** the Danish Road Directorate's (**Vejdirektoratet**) data-exchange
  platform, at <https://du-portal-ui.dataudveksler.app.vd.dk> — Vejdirektoratet is reported as the
  "IDRO Authority" for Denmark's National Access Point.
- **Authority:** Vejdirektoratet (Danish Road Directorate); the **Danish Agency for Climate Data**
  (Klimadatastyrelsen) is separately developing a national overview of publicly accessible charging
  points, in collaboration with Vejdirektoratet — see
  [its page on the initiative](https://www.eng.klimadatastyrelsen.dk/green-transition-and-climate-adaptation/national-mapping-of-charging-point-data).
- **AFIR status:** Denmark is reported as one of the EU's operational NAPs. DATEX II becomes the
  mandatory EU-wide format for the AFIR Article 20(2) charging-point data from 14 April 2026 under
  [Commission Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en);
  the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025 (see
  [`../README.md#afir`](../README.md#afir)). **Neither a public dataset URL, a confirmed format, nor a
  license could be confirmed** for Denmark's AFIR recharging data at the time of writing — see
  "Available datasets" below.

## Useful links

- **Data-exchange portal:** <https://du-portal-ui.dataudveksler.app.vd.dk> — access requires
  requesting login credentials; no public, unauthenticated dataset browser was found.
- **Danish Agency for Climate Data — national charging-point mapping initiative:**
  <https://www.eng.klimadatastyrelsen.dk/green-transition-and-climate-adaptation/national-mapping-of-charging-point-data> —
  describes a **planned** system to "receive, display and distribute standardised data" (location,
  payment options, operational status, usage levels, availability); the page does not describe this
  as already operational, nor does it name a format or license.

## Available datasets

- **No public, directly downloadable AFIR/DATEX II dataset for Denmark could be confirmed** at the
  time of writing. The Vejdirektoratet data-exchange portal requires requesting credentials before
  any data can be accessed, and the Klimadatastyrelsen initiative does not yet provide a live,
  citable dataset. Update this page after confirming a concrete dataset URL, format, and license.

## Other DATEX II feeds (road traffic, beyond AFIR)

Vejdirektoratet's Dataudveksler (Data Exchanger) is Denmark's NAP for road-traffic DATEX II too, not
only AFIR. Its **catalogue metadata is browsable anonymously**, but the actual data pull is gated.

- **DCAT catalogue (verified anonymous):**
  <https://businessservice.dataudveksler.app.vd.dk/api/Metadata?format=dcat> — returns `HTTP 200`,
  `application/rdf+xml` (~1.4 MB) listing the platform's datasets. Several are tagged `datex-II`
  (e.g. traffic messages "Trafikmeldinger", lorry-parking, state-road speed limits, road-weather
  condition reports).

**Verified access limitation:** every dataset's `dcat:accessURL` points to a
`https://du-portal-ui.dataudveksler.app.vd.dk/data/<id>/overview` page, and the catalogue contains
**no anonymous `downloadURL`/`endpointURL`**. The metadata is public, but the DATEX II data is behind
the portal.

- **Data endpoints (registration-gated):** obtaining the data requires portal credentials /
  an API key from the Dataudveksler ("Kom godt i gang" / Get started). The related host
  `data.vd-nap.dk` did **not resolve** at the time of writing, and `nap.vd.dk` redirects to the
  du-portal login SPA — so **no anonymous DATEX II data endpoint could be confirmed**.

```bash
# Catalogue metadata is anonymous; data pull is not.
curl -sS --compressed \
  'https://businessservice.dataudveksler.app.vd.dk/api/Metadata?format=dcat' -o dk-catalogue.rdf
```

**How to obtain:** browse the DCAT catalogue above to identify the DATEX II dataset you need, then
request access credentials through the Vejdirektoratet Dataudveksler portal. No file is committed to
[`datasets/denmark/`](../../../datex4j-integration-tests/src/test/resources/datasets/denmark) because
no anonymous, reproducible fetch was found.

## Notes

No country-specific DATEX II/AFIR implementation details (extensions, national identifiers) could be
confirmed from official sources at the time of writing.

## See also

- [Denmark fixture metadata](../../../datex4j-integration-tests/src/test/resources/datasets/denmark/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
