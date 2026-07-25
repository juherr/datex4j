# Dataset: netherlands

This directory holds committed real-world DATEX II v3 datasets from the Dutch National Data Warehouse
for Traffic Information (NDW, <http://opendata.ndw.nu>, all **CC0**): **traffic situations**, **SRTI
safety-related messages**, **emission zones (UVAR)** and **truck-parking status**. It also documents
opt-in, never-committed sources (the v3 **roadworks/events planning feed**, ~171 MB, and the
**DATEX II v2 measurement feeds**, up to ~390 MB) and a second Netherlands AFIR source (**AFIR
charging points**) that is not committed yet.

The situations / SRTI / emission-zones / roadworks feeds are all Exchange-2020 `mc:messageContainer`
documents (`modelBaseVersion="3"`). They are read into the v3.7 `MessageContainer` model and their
outcome is reported by
[`Datex3TrafficFeedReadValidateTest`](../../../../java/dev/juherr/datex4j/it/Datex3TrafficFeedReadValidateTest.java)
(offline) and [`NdwRoadworksReadTest`](../../../../java/dev/juherr/datex4j/it/NdwRoadworksReadTest.java)
(opt-in). The bundled validator compiles the Exchange-2020 `mc:messageContainer` root schema together
with `d2:payload` for v3.6/v3.7, so container feeds are validated on the same footing as bare
payloads — the earlier spurious `cvc-elt.1.a` "element `mc:messageContainer` not declared" root error
is gone, and the situations / SRTI feeds now validate cleanly (zero errors). The test asserts each
feed's now-known validity verdict.

## Committed dataset: situations

- **Source URL:** <http://opendata.ndw.nu/actueel_beeld.xml.gz>
- **Licence:** CC0 (public domain; redistribute freely, no attribution required)
- **Download date:** 2026-07-24
- **DATEX version:** v3, MessageContainer (`mc:messageContainer` → `sit:SituationPublication`)
- **Profile / publication:** `SituationPublication`
- **Country:** Netherlands
- **Remarks:** The full feed (~3 MB gzipped, 397 situations) is **trimmed to the first 2 situations**;
  the `mc:messageContainer` envelope, both `mc:payload` and `mc:exchangeInformation`, and all
  namespaces are preserved and values are unmodified. Reads cleanly into a `SituationPublication`
  (national `nle:`/`nlxe:`/`srx:` extensions are dropped on the lax read); the stable situation id
  `RWS01_SM1162215_D2_WWA` survives re-serialization. v3.7 validation reports it **valid (zero
  errors)** — the trimmed container and its situation payload are schema-clean.

## Committed dataset: srti

- **Source URL:** <http://opendata.ndw.nu/veiligheidsgerelateerde_berichten_srti.xml.gz>
- **Licence:** CC0
- **Download date:** 2026-07-24
- **DATEX version:** v3, MessageContainer (`mc:messageContainer` → `sit:SituationPublication`)
- **Profile / publication:** `SituationPublication` (Safety-Related Traffic Information)
- **Country:** Netherlands
- **Remarks:** Trimmed to a single `sit:situation` (the source snapshot carried one situation with 159
  records). Envelope and namespaces preserved, values unmodified. Reads into a `SituationPublication`;
  the situation id `NDW08_9d8afcc1-fd11-44f5-9653-0efae246856a_SIT` survives re-serialization. v3.7
  validation reports it **valid (zero errors)**.

## Committed dataset: emission-zones

- **Source URL:** <http://opendata.ndw.nu/emissiezones.xml.gz>
- **Licence:** CC0
- **Download date:** 2026-07-24
- **DATEX version:** v3, MessageContainer (`mc:messageContainer` → `cz:ControlledZoneTablePublication`)
- **Profile / publication:** `ControlledZoneTablePublication` (UVAR / low-emission zones)
- **Country:** Netherlands
- **Remarks:** Trimmed from 43 zones to the first 2. Envelope/namespaces preserved, values unmodified.
- **Known quirks:** The feed's zone records are `<cz:urbanVehicleAccessRegulation>` elements, a name
  that is **absent from every bundled DATEX II v3.0–v3.7 schema** (the released model uses
  `cz:controlledZone` / `cz:uvarZone`). The `ControlledZoneTablePublication` envelope therefore reads,
  but the UVAR zone records are **dropped** on the lax read — only the `controlledZoneTable` shell (with
  its `tableVersionTime`) survives, which is the token the test asserts. v3.7 validation reports it
  **invalid (~145 errors)**, dominated by `cvc-complex-type.2.4.a` on the unknown
  `urbanVehicleAccessRegulation` element (plus related producer errors); the `mc:messageContainer`
  root itself is now recognised. This is cross-minor / national drift, not a vendoring defect.

## Opt-in (never committed): roadworks / events planning

- **Source URL:** <http://opendata.ndw.nu/planningsfeed_wegwerkzaamheden_en_evenementen.xml.gz>
- **Licence:** CC0
- **DATEX version:** v3, MessageContainer (`mc:messageContainer` → `sit:SituationPublication`)
- **Size:** ~15 MB gzipped, **~171 MB uncompressed** — far too large to commit.
- **How to obtain and run:** see
  [`NdwRoadworksReadTest`](../../../../java/dev/juherr/datex4j/it/NdwRoadworksReadTest.java). It is
  skipped unless `-Ddatex4j.it.ndw.roadworks=<path>` points at a downloaded, gunzipped copy; run it
  with a generous heap (`MAVEN_OPTS=-Xmx6g`).
- **Observed (2026-07-24):** reads **13 793 situations** in one `SituationPublication`; v3.7 validation
  reports it invalid (previously observed ~10 766 errors), now dominated by NDW's
  `sit:roadworksExtension` national-extension elements — the `mc:messageContainer` root is recognised
  and no longer contributes an error.

## Committed dataset: truckparking-status

## Committed dataset: truckparking-status

- **Source URL:** http://opendata.ndw.nu/Truckparking_Parking_Status.xml
- **Licence:** CC0 (public domain; redistribute freely, no attribution required)
- **Download date:** 2026-07-24
- **DATEX version:** 3 (NDW v3.x; `modelBaseVersion="3"`, namespaces under `http://datex2.eu/schema/3/...`)
- **Profile:** Parking status (`ParkingStatusPublication`)
- **Country:** Netherlands
- **Expected object counts:** 1 `parkingTableReference` (parking table `NL-12`) and 8
  `parkingRecordStatus` entries (each a `ParkingSiteStatus` carrying a `parkingRecordReference`).
- **Remarks:** The National Data Warehouse for Traffic Information (NDW) publishes this endpoint
  openly under CC0, so the (small, ~18 KB) snapshot is committed. It is exercised **read-only** by
  the round-trip suite (`Dataset.Format.XML_READ_ONLY`): asserted well-formed, parsed into
  `ParkingStatusPublication` via `datex4j-xml`, then re-serialized to prove the stable parking-table
  identifier `NL-12` survives the read. No XSD validation and no round-trip diff are performed.
  Because it is a live snapshot, the exact record ids and occupancy values differ if re-downloaded.
- **Known quirks:** The feed emits `targetClass="par:ParkingTable"` (and `par:ParkingRecord`) on its
  references, whereas the bundled DATEX II XSD fixes that prefix as `prk:`. This real-world prefix
  drift makes the payload **XSD-invalid** even though it **parses** cleanly into the model — hence
  the read-only treatment rather than the seven mandatory XML checks.

## Opt-in (never committed): DATEX II v2 measurement feeds

NDW also publishes **DATEX II v2** feeds. Three are covered by
[`NdwMeasurementV2ReadTest`](../../../../java/dev/juherr/datex4j/it/NdwMeasurementV2ReadTest.java):

- **Source URLs:** <http://opendata.ndw.nu/measurement_current.xml.gz> (`MeasurementSiteTablePublication`),
  <http://opendata.ndw.nu/trafficspeed.xml.gz> and <http://opendata.ndw.nu/traveltime.xml.gz>
  (`MeasuredDataPublication`).
- **Licence:** CC0 (public domain; redistribute freely, no attribution required).
- **DATEX version:** 2.0 (`d2LogicalModel`, `modelBaseVersion="2"`, namespace
  `http://datex2.eu/schema/2/2_0`).
- **Size:** ~12 MB gzipped / **~390 MB uncompressed** for `measurement_current` — far too large to
  commit.
- **SOAP wrapper — important:** unlike the small committed v2 fixtures, **these feeds are
  SOAP-wrapped**: the DATEX II `d2LogicalModel` is nested inside a `<SOAP:Envelope>`/`<SOAP:Body>`
  (`http://schemas.xmlsoap.org/soap/envelope/`). **datex4j reads a `d2LogicalModel` root, not a SOAP
  envelope**, so you must strip the envelope first (slice out the inner `d2LogicalModel` element)
  before handing the bytes to the codec. `NdwMeasurementV2ReadTest` does exactly this and documents
  the technique.
- **How to obtain and run:** the test is skipped unless `-Ddatex4j.it.ndw.v2measurement=<path>`
  points at a downloaded copy (a `.gz` path is gunzipped transparently). Run with a generous heap:

  ```bash
  curl -sL -o /tmp/ndw-measurement.xml.gz http://opendata.ndw.nu/measurement_current.xml.gz
  MAVEN_OPTS=-Xmx6g ./mvnw -pl datex4j-integration-tests test \
    -Dtest=NdwMeasurementV2ReadTest \
    -DargLine=-Xmx6g \
    -Ddatex4j.it.ndw.v2measurement=/tmp/ndw-measurement.xml.gz
  ```
- **Observed (2026-07-25):** `measurement_current` (~390 MB uncompressed) strips, reads into a v2.0
  `MeasurementSiteTablePublication`, and **validates cleanly against v2.0 (zero errors)**.

## Not committed: AFIR charging points (DOT-NL)

- **Source:** DOT-NL, the Netherlands' National Access Point for charging-point data, documented at
  <https://docs.ndw.nu/en/faq/DOT-NL/> (operated by NDW).
- **How to obtain:** consult the Charging Points API (DAFNE) interface description at
  <https://docs.ndw.nu/en/data-uitwisseling/interface-beschrijvingen/dafne-api/> for the pull-based
  API to query; the FAQ states access is free of charge and open to any consumer.
- **Licence:** not explicitly stated in the NDW documentation reviewed.
- **Format / DATEX version:** currently **OCPI 2.2.1** (per NDW's own FAQ, which notes this "does not
  fully meet the European AFIR requirements"); a transition to DATEX II is planned but not yet
  confirmed live. See
  [`../../../../../../docs/afir/nap/netherlands.md`](../../../../../../docs/afir/nap/netherlands.md)
  for details.
- **Update frequency:** pull-based access today; push delivery planned for late 2026.

Because the currently confirmed AFIR format is OCPI rather than DATEX II, no AFIR file is committed
pending a confirmed DATEX II publication from DOT-NL.
