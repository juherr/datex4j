# Netherlands

## General information

- **Country:** Netherlands
- **National Access Point (NAP):** **DOT-NL**, documented at
  [docs.ndw.nu](https://docs.ndw.nu/en/faq/DOT-NL/).
- **Authority:** operated by **NDW** (Nationaal Dataportaal Wegverkeer / the Dutch national traffic
  information service) on behalf of the Ministry of Infrastructure & Water Management.
- **AFIR status:** DOT-NL currently exchanges charging-point data in **OCPI 2.2.1**, which the NDW
  documentation itself notes "does not fully meet the European AFIR requirements" — a migration to
  OCPI 2.3 is planned, alongside a transition to **DATEX II**, which becomes the mandatory EU-wide
  format for the AFIR Article 20(2) charging-point data from 14 April 2026 under [Commission
  Implementing Regulation (EU) 2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en)
  (the underlying AFIR Article 20 data-via-NAP obligation has applied since 14 April 2025; see
  [`../README.md#afir`](../README.md#afir)). At the time of writing, DOT-NL's public interface is
  OCPI-based, not DATEX II.

## Useful links

- **NAP documentation:** <https://docs.ndw.nu/en/faq/DOT-NL/>
- **Charging Points API (DAFNE) reference:** <https://docs.ndw.nu/en/data-uitwisseling/interface-beschrijvingen/dafne-api/>

## Available datasets

- **DOT-NL charging-point data** — location, availability and tariff information for publicly
  accessible charging points.
  - **Format:** **OCPI 2.2.1** today (per the NDW FAQ); GeoJSON is also mentioned as an access
    format. A DATEX II-format publication is planned but **not yet confirmed as live** at the time of
    writing — no DATEX II endpoint URL was found to cite.
  - **Licence:** not explicitly stated in the NDW documentation reviewed; data is described as
    accessible "free of charge" via pull-based APIs, with push delivery planned for late 2026.
  - **Update frequency:** not specified beyond "pull-based" access; push delivery is planned.
  - **Access:** open to any consumer (manufacturers, service providers, researchers, governments)
    without charge, per the NDW FAQ.

## Notes

- NDW is separately reported (via third-party AFIR guidance, not NDW's own documentation) to operate
  an OCPI-to-DATEX II converter for NAP reporting purposes; this could not be independently confirmed
  against an NDW-published source at the time of writing, so it is noted here as unconfirmed rather
  than stated as fact.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/netherlands/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/netherlands/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
