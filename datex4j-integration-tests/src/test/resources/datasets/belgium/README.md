# Dataset: belgium

**No data file is committed in this directory yet.** See
[`../README.md`](../README.md) for why (licence/size policy) and how to contribute one.

## How to obtain a real dataset

- **Source:** Group INDIGO's static EV charging-station dataset on transportdata.be (Belgium's
  National Access Point), dataset page
  <https://transportdata.be/en/dataset/indigo-open-data-evcharging>.
- **How to obtain:** download directly from
  <https://transportdata.be/dataset/27f1357d-71ee-48cb-84a1-96f3f4f034b8/resource/d4bc8ddd-c80f-4330-98e5-d86e5b2147c3/download/indigo-data-evcharging-static-datexii.xml> —
  no registration observed as required.
- **Licence:** **not stated** on the dataset page. transportdata.be's "About" page explicitly says
  the portal "is not about open data"; confirm terms of use with Group INDIGO
  (`data.fr@group-indigo.com`) before committing this file here.
- **Format / DATEX version:** **DATEX II XML** (static publication); the exact minor version is not
  stated on the dataset page — inspect the downloaded file's namespace/schema references to confirm
  before registering it in `DatasetCatalog.all()`.
- **Update frequency:** weekdays (Monday–Friday), per the dataset page; "Daily" per its quality
  section.

See
[`../../../../../../docs/afir/nap/belgium.md`](../../../../../../docs/afir/nap/belgium.md) for the
full picture, including other (non-DATEX-II) datasets on the same portal. No file is committed here
yet pending confirmation of redistribution rights from Group INDIGO.

## Opt-in (never committed): Verkeerscentrum datex2v3full

- **Source URL:** <https://www.verkeerscentrum.be/uitwisseling/datex2v3full> (Flemish traffic centre)
- **Licence:** **not restated** on the endpoint — treated as uncertain, so the feed is **never
  committed** and only exercised opt-in via
  [`BeVerkeerscentrumReadTest`](../../../../java/dev/juherr/datex4j/it/BeVerkeerscentrumReadTest.java)
  (`-Ddatex4j.it.be.verkeerscentrum=<path>`).
- **Format / DATEX version:** advertised as DATEX II v3 (bare `d2:payload`), ~350 KB.
- **Known quirks (verified 2026-07-24):** despite the `datex2v3full` name, the feed does **not**
  conform to the DATEX II payload model as bundled. Its root is a bare `<d2:payload>` **without** an
  `xsi:type` (the payload type is abstract, so JAXB cannot instantiate it), it places `<situation>`
  elements in the `d2Payload` namespace rather than the `situation` namespace, and it mixes in a
  proprietary `verkeerscentrum.be/tcc.backend` namespace. `datex4j-xml` therefore **cannot** read it
  into a `SituationPublication`, and v3.7 validation rejects it (~120 errors, starting with
  `cvc-type.2` "type definition must not be abstract for element `ns4:payload`"). The opt-in test
  asserts only well-formedness and that the codec/validator run, and reports this outcome honestly.
