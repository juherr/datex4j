# datex-json test fixtures

## finland-afir-messagecontainer.v3_6.json
- **Source:** Fintraffic / digitraffic.fi AFIR feed
  `https://afir.digitraffic.fi/api/charging-network/v1/locations/datex2-3.6`
- **Licence:** CC BY 4.0 — attribution: "Source: Fintraffic / digitraffic.fi, license CC 4.0 BY"
- **DATEX version:** 3.6 · **Encoding:** conformant DATEX II JSON · **Root:** MessageContainer
- **Trimmed:** to one `energyInfrastructureTable` entry containing one `energyInfrastructureSite`
  (the live feed is ~13 MB across 18 tables and thousands of sites). Values are unmodified.
- **Role:** oracle for the conformant DATEX II JSON codec (read→write→read round-trip).

## Known lossy cases
- **`FacilityLocation` (street address / postcode):** this fixture's `energyInfrastructureSite`
  carries a `locationReference` with both `locxFacilityLocation` (street, postcode, city) and
  `locPointLocation` (coordinates). On read, only the coordinate location survives: `FacilityLocation`
  is not a `LocationReference` subtype, so the generated model has no field slot for it alongside
  `locPointLocation` on the single `locationReference` property, and it is silently dropped.
