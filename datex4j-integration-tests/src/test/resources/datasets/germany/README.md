# Dataset: germany

**No data file is committed in this directory yet.** See
[`../README.md`](../README.md) for why (licence/size policy) and how to contribute one.

## How to obtain a real dataset

Germany's AFIR recharging data is published per-operator (or per-aggregator) as individual
"Datenangebote" (data offers) on **Mobilithek**, not as one downloadable national file:

1. Register a free account at <https://mobilithek.info/registration-request> using an institutional
   email address.
2. Browse the Mobilithek catalogue for AFIR recharging data offers (structured per the
   `01-00-00` AFIR DATEX II recharging profile — see
   <https://github.com/MobilithekDE/AFIR-DATEX-II-Recharging-Profil>) and request a subscription to
   the offer(s) you need.
3. Once subscribed, pull the offer's data via Mobilithek's delivery interface. Expect **DATEX II
   XML**, and be prepared to handle the **delta mechanism** (snapshot + delta messages, at most 1 Hz,
   full snapshot at least every 3,500 deltas or 6 hours) — see
   [`../../../../../../docs/afir/nap/germany.md`](../../../../../../docs/afir/nap/germany.md#notes)
   for details.
4. **Licence** is set per data offer by the publishing operator, not by Mobilithek as a whole —
   confirm the specific offer's terms before committing any downloaded file here.

## Synthetic fixture (already committed)

Until a real, licence-confirmed German dataset is added, the round-trip suite exercises
[`../synthetic/afir-recharging/`](../synthetic/afir-recharging) — a hand-authored, XSD-valid,
Apache-2.0-licensed fixture that is not country-specific but shares the same AFIR
`EnergyInfrastructureTablePublication` shape.

## Official examples

`../official/examples/` (not created — git does not track empty directories, and none of its
contents has been confirmed yet) is where an official DATEX II example payload would go, if and when
one with a confirmed, redistributable licence is found (see
[`../../../../../../docs/afir/official-datex-resources.md`](../../../../../../docs/afir/official-datex-resources.md#official-examples)).
The AFIR-DATEX-II-Recharging-Profil GitHub repository has no `LICENSE` file, so no file from it is
committed here either.
