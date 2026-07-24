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

## Other NDW DATEX II feeds (open data portal)

Separately from the AFIR/charging story above, NDW runs a **public open-data portal** at
<https://opendata.ndw.nu/> that publishes real, nationwide **DATEX II v3** road-traffic data. This is
the richest openly-licensed DATEX II source found for testing this library — it spans several of the
domains the library models (situations, SRTI, parking, VMS, roadworks).

- **Licence:** **Creative Commons Zero (CC0)** — public domain, redistribution allowed, no
  attribution required (per NDW's [copyright statement](https://www.ndw.nu/copyright); the CC0 waiver
  excludes images/video, which do not apply to these data files).
- **Access:** **anonymous**, no login. Files are gzip-compressed (`.xml.gz`) except where noted;
  fetch with e.g. `curl -s http://opendata.ndw.nu/<file>` then `gunzip`.
- **Format:** DATEX II XML, `http://datex2.eu/schema/3/…` namespaces, `modelBaseVersion="3"`. The
  situation feeds are wrapped in an Exchange-2020 **`MessageContainer`**; some carry NDW national
  extensions (`nlExtensions`, `nlxExtensions`, `situationRecordExtension`).

Verified live at the time of writing:

| File | Data | Root / notes |
|---|---|---|
| `actueel_beeld.xml.gz` | Current situations snapshot (~3 MB) | `MessageContainer` → `SituationPublication` |
| `planningsfeed_wegwerkzaamheden_en_evenementen.xml.gz` | Roadworks & events | `SituationPublication` |
| `veiligheidsgerelateerde_berichten_srti.xml.gz` | Safety-related messages (SRTI) | `SituationPublication` |
| `trafficspeed.xml.gz`, `measurement.xml.gz`, `measurement_current.xml.gz`, `traveltime.xml.gz` | Traffic speed / measurement / travel time | `MeasuredDataPublication` |
| `Matrixsignaalinformatie.xml.gz`, `dynamische_route_informatie_paneel.xml.gz` | Matrix signs / route-information panels (VMS) | VMS publication |
| `emissiezones.xml.gz` | Emission zones (relevant to UVAR) | — |
| `tijdelijke_verkeersmaatregelen_afsluitingen.xml.gz`, `tijdelijke_verkeersmaatregelen_maximum_snelheden.xml.gz` | Temporary closures / speed limits | — |
| `planningsfeed_brugopeningen.xml.gz` | Bridge-opening schedules | — |
| `Truckparking_Parking_Status.xml` (~18 KB, not gzipped) | Truck-parking availability | `ParkingStatusPublication` |
| `charging_point_locations*.geojson.gz`, `..._ocpi.json.gz` | EV charging (GeoJSON / OCPI, **not** DATEX II) | — |

**Library-compatibility note (verified):** against the bundled v3.6/v3.7 model, the small
`Truckparking_Parking_Status.xml` **parses** into a `ParkingStatusPublication`, but **strict XSD
validation fails** on version/prefix drift (e.g. NDW emits `targetClass="par:ParkingTable"` where the
bundled schema fixes `prk:ParkingTable`), and unmarshalling the situations `MessageContainer` fails
(national extensions + drift). Treat these as real-world **read/parse** inputs for hardening, not as
clean round-trip fixtures without accommodating the drift.

## Notes

- NDW is separately reported (via third-party AFIR guidance, not NDW's own documentation) to operate
  an OCPI-to-DATEX II converter for NAP reporting purposes; this could not be independently confirmed
  against an NDW-published source at the time of writing, so it is noted here as unconfirmed rather
  than stated as fact.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/netherlands/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/netherlands/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
