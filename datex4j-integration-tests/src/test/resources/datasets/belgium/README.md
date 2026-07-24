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
