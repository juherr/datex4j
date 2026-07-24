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
  data (real-time availability, operating status, price) available free of charge; DATEX II becomes
  the mandatory exchange format across the EU from 14 April 2026 (see
  [`../README.md#afir`](../README.md#afir)). **No France-specific public DATEX II endpoint for
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
  deadline, but no such endpoint, URL, or schema reference was found to cite here — this page will be
  updated once one is confirmed rather than guessing at a URL.

## Notes

No further country-specific DATEX II/AFIR implementation details (e.g. extensions, national
identifiers) could be confirmed from official sources at the time of writing.

## See also

- [`../../../datex4j-integration-tests/src/test/resources/datasets/france/README.md`](../../../datex4j-integration-tests/src/test/resources/datasets/france/README.md)
- [`../official-datex-resources.md`](../official-datex-resources.md)
