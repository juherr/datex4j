# France

## General information

- **Country:** France
- **National Access Point (NAP):** [transport.data.gouv.fr](https://transport.data.gouv.fr) — "Le
  Point d'Accès National aux données ouvertes de transport" (the National Access Point for open
  transport data), covering all transport modes, not only recharging infrastructure.
- **Authority:** operated for the French Ministry of Transport; the recharging-specific data
  collection process is additionally supported by
  [QualiCharge](https://www.qualicharge.beta.gouv.fr/), a French government "startup d'État" run
  under the DGEC (Direction Générale de l'Énergie et du Climat).
- **AFIR status:** since 14 April 2025, AFIR Article 20 requires operators of publicly accessible
  charging points to make static data (location, connectors, access/payment conditions) and dynamic
  data (real-time availability, operating status, price) available free of charge via the NAP (see
  [`../README.md#afir`](../README.md#afir)); DATEX II then becomes the mandatory EU-wide format for
  that Article 20(2) data from 14 April 2026 under [Commission Implementing Regulation (EU)
  2025/655](https://transport.ec.europa.eu/news-events/news/commission-enhances-interoperability-and-transparency-alternative-fuels-infrastructure-data-2025-04-11_en).
  **No France-specific public DATEX II endpoint for
  recharging data was confirmed at the time of writing** — see "Available datasets" below for what
  is confirmed today.

## Useful links

- **NAP:** <https://transport.data.gouv.fr>
- **IRVE dataset landing page:**
  <https://transport.data.gouv.fr/datasets/fichier-consolide-des-bornes-de-recharge-pour-vehicules-electriques>
- **QualiCharge:** <https://www.qualicharge.beta.gouv.fr/> — the data-collection platform CPOs
  connect to (an open API for direct-current charging operators has been available since May 2024);
  QualiCharge relays required information to transport.data.gouv.fr. See also its
  [integration process page](https://www.qualicharge.beta.gouv.fr/integrerqualicharge/).
- **QualiCharge organisation on data.gouv.fr:** <https://www.data.gouv.fr/organizations/qualicharge>

## Available datasets

- **Base nationale des IRVE (Infrastructures de Recharge pour Véhicules Électriques)** — the
  consolidated national dataset of publicly accessible charging points, published on
  transport.data.gouv.fr.
  - **URL:** <https://transport.data.gouv.fr/datasets/fichier-consolide-des-bornes-de-recharge-pour-vehicules-electriques>
  - **Format:** **CSV and GeoJSON** (two consolidation CSV files plus a GeoJSON export) — **not
    DATEX II** at the time of writing.
  - **Licence:** Licence Ouverte / Open Licence, version 1.0.
  - **Update frequency:** daily.
  - **Basis:** static-data structure defined by the
    [decree of 4 May 2021](https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000043475441) (not the
    AFIR DATEX II profile).
- **A dedicated DATEX II feed for AFIR recharging data could not be confirmed** on
  transport.data.gouv.fr as of this writing. QualiCharge's role (collecting operator data and
  relaying it onward) suggests a DATEX II-format publication may follow the EU-wide 14 April 2026
  DATEX II deadline (Commission Implementing Regulation (EU) 2025/655, cited above), but no such
  endpoint, URL, or schema reference was found to cite here — this page will be updated once one is
  confirmed rather than guessing at a URL.

## Other DATEX II feeds (road traffic, beyond AFIR)

Although no AFIR/EV-charging DATEX II feed is confirmed yet, transport.data.gouv.fr does publish
**road-traffic** data as DATEX II, all under **Licence Ouverte 2.0**, most of it anonymously
downloadable. The single **DATEX II v3** source (the only one the library's v3-only model can read)
is the DiaLog traffic-regulation database; the Bison Futé / TIPI real-time feeds are still DATEX II
**v2.2.2**.

| Feed | URL (verified live) | DATEX II | Root / notes |
|---|---|---|---|
| **DiaLog traffic regulations (TRO)** | `https://dialog.beta.gouv.fr/api/regulations.xml` | **v3** XML | `TrafficRegulationPublication` (`modelBaseVersion="3"`, `http://datex2.eu/schema/3/trafficRegulation`), ~10.7 MB |
| Real-time speeds & flows | `https://transport.data.gouv.fr/resources/79165/download` | **v2.2.2** XML | `MeasuredDataPublication` (supplier `TIPI`) |
| Road events (RRN) | `https://transport.data.gouv.fr/resources/79173/download` | v2.2.2 XML | `SituationPublication`; hourly aggregate also at `http://tipi.bison-fute.gouv.fr/bison-fute-ouvert/publicationsDIR/Evenementiel-DIR/grt/RRN/content.xml` |
| Traffic status ("TRAFICOLOR") per DIR | `http://tipi.bison-fute.gouv.fr/bison-fute-ouvert/publicationsDIR/TRAFICOLOR-DIR/` | v2.x XML | open Apache directory, one sub-folder per Direction Interdépartementale des Routes |

- **DiaLog** (`https://dialog.beta.gouv.fr`) is the "Base de données nationale de la réglementation
  de circulation" (DiaLog, MTES/MCT), verified live returning a DATEX II **v3**
  `TrafficRegulationPublication` — a genuine openly-licensed v3 source for the traffic-regulation
  domain (it adds a national extension namespace under `github.com/MTES-MCT/dialog`). Note DiaLog
  itself states the published restrictions have no legal value (only the municipal ordinances do).
- The **Bison Futé / TIPI** publication tree at
  `http://tipi.bison-fute.gouv.fr/bison-fute-ouvert/publicationsDIR/` is browsable **anonymously**;
  its sub-trees (`TRAFICOLOR-DIR`, `Evenementiel-DIR`, `QTV-DIR`, `TP-DIR`) hold per-operator (DIR)
  DATEX II **v2** publications and HTML summaries.

```bash
curl -sS --compressed https://dialog.beta.gouv.fr/api/regulations.xml -o fr-dialog-tro.xml   # v3
curl -sS --compressed https://transport.data.gouv.fr/resources/79165/download -o fr-speed.xml # v2
```

**Version note:** only DiaLog is DATEX II **v3** (readable by the bundled v3.6/v3.7 model, subject to
version-drift/extension differences under strict XSD validation). The TIPI speed/event feeds are
DATEX II **v2.2.2**, which the library's v3-only model does not read; they are listed for
completeness and as documentation of the French NAP's current publication state.

## Notes

No further country-specific DATEX II/AFIR implementation details (e.g. extensions, national
identifiers) could be confirmed from official sources at the time of writing beyond the DiaLog
national extension noted above.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/france/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/france/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
