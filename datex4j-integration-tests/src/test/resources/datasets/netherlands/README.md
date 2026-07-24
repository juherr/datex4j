# Dataset: netherlands

**No data file is committed in this directory yet.** See
[`../README.md`](../README.md) for why (licence/size policy) and how to contribute one.

## How to obtain a real dataset

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

Because the currently confirmed format is OCPI rather than DATEX II, no file is committed pending a
confirmed DATEX II publication from DOT-NL.
