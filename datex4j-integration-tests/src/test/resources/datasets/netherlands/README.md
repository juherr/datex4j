# Dataset: netherlands

This directory holds one committed real-world DATEX II dataset (**truck-parking status**, below) and
documents a second Netherlands source (**AFIR charging points**) that is not committed yet.

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
