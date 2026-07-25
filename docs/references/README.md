# Reference documents

External specifications the datex4j mappers are built against. Archived here so the
build stays reproducible and reviewable offline.

## IDACS deliverable 2.2.1 — unlocked data through National Access Points in DATEX II format

- **File:** [`idacs-2.2.1-unlocked-data-nap-datex-ii.pdf`](./idacs-2.2.1-unlocked-data-nap-datex-ii.pdf)
- **Project:** IDACS — *ID and Data Collection for Sustainable Fuels in Europe*
  (Grant Agreement MOVE/B4/SUB/2018-498/CEF/PSA/SI2.792684)
- **Authors:** J. Wegener (NOW GmbH), H. Schurer & J. Vrooland (RVO / Netherlands Enterprise Agency)
- **Version / date:** V1.0 (final) / 30-06-2022
- **Source:** [RVO publication][idacs-pdf]

This deliverable (and its embedded *Annex 1 — Deployment scenarios for IDACS datasets in
DATEX II format*, U-Trex b.v. / RVO, 2021) is the reference that defines how EV-charging,
hydrogen and alternative-fuel data — as exchanged over roaming protocols such as **OCPI** —
map onto the **DATEX II Energy Infrastructure** publication (a.k.a. DATEX II v3.2 / v3.x
`EnergyInfrastructureTablePublication` + `EnergyInfrastructureStatusPublication`).

It is the normative basis for the `datex4j-ocpi` mapping module. The field-level mapping
lives in the deliverable's **Annex 1 (static)** and **Annex 2 (dynamic)** tables and the
**RSP class diagrams** (§6.3–6.6). The relevant structure is:

[idacs-pdf]: https://english.rvo.nl/files/file/2023-07/20220630%5FIDACSActivity%5F2.2Deliverable%5F2.2.1%5F0.pdf

```text
EnergyInfrastructureSite            (= OCPI Location)
 ├─ name, siteLocation, operator/owner, operatingHours, lastUpdate
 ├─ siteLocation → LocationReference → FacilityLocation → { timeZone, Address }
 └─ EnergyInfrastructureStation*     (= OCPI EVSE)
     └─ ElectricChargingPoint*       (= OCPI charge point / refill point)
         ├─ externalIdentifier, ElectricEnergyMix
         └─ Connector*               (= OCPI Connector)
```

### Normative static mapping (Annex 1 — EV charging)

| IDACS element | M/O | DATEX II class → element → attribute |
|---|---|---|
| Station latitude / longitude | M | EnergyInfrastructureSite → siteLocation → pointByCoordinates → pointCoordinates.latitude / .longitude |
| Station name | M | EnergyInfrastructureSite → name |
| Street / house number / postcode / city / country | M (city/country), M-if-available (rest) | EnergyInfrastructureSite → siteLocation → **FacilityLocation → Address** → addressLine / postcode / city / countryCode |
| Timezone | M | EnergyInfrastructureSite → siteLocation → **FacilityLocation → timeZone** |
| Opening time | M | EnergyInfrastructureSite → operatingHours |
| Capabilities (identification & payment) | M | (Organisation / refill point; list) |
| Telephone | M | operator → OrganisationSpecification.organisationUnit.contactInformation → telephoneNumber |
| Operator name | M | operator → OrganisationSpecification → name |
| Suboperator name | M-if-available | OrganisationSpecification.subOrganisation → name |
| Operator code | O | OrganisationSpecification → nationalOrganisationNumber |
| Owner name | O | owner → OrganisationSpecification → name |
| Operator website | O | OrganisationSpecification → linkToGeneralInformation |
| Last-static-data-update-timestamp | O | EnergyInfrastructureSite → lastUpdate |
| Charging point ID | M | ElectricChargingPoint → externalIdentifier |
| Mode | M | Connector → chargingMode |
| Power | M | ElectricChargingPoint → availableChargingPower |
| Type of charging interface | M | Connector → connectorType |
| Energy source | O | ElectricChargingPoint → ElectricEnergyMix → ElectricEnergySourceRatio |
| Max power at socket | O | Connector → maxPowerAtSocket |
| Voltage | O | Connector → voltage |
| Current | O | Connector → maximumCurrent |
| Connector format | O | Connector → connectorFormat |

### Normative dynamic mapping (Annex 2)

| IDACS element | M/O | DATEX II class → attribute |
|---|---|---|
| Availability | M | EnergyInfrastructureStatusPublication → …SiteStatus → …StationStatus → RefillPointStatus → currentStatus |
| Ad-hoc price | O | RefillPointStatus → pricePerUnit / pricePerHour |
| Dynamic-data-update-timestamp | O | EnergyInfrastructureSiteStatus → lastUpdate |

> The deliverable notes its annexes are based on a **non-stable May-2021 draft** of the
> DATEX II Energy publication (pre-CEN ballot); some sub-element names differ from final
> v3.7 (e.g. `detailedAddressInformation` → `addressLine`). datex4j targets the **released
> v3.7** model, so we follow the *structure* the deliverable prescribes, not its draft names.

The reference metadata and normative tables above remain stable. The current implementation
coverage, executable test links, and known mapping limitations live in the
[OCPI mapping guide](../guides/ocpi-mapping.md).
