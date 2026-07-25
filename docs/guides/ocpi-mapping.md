# OCPI mapping

`datex4j-ocpi` generates an OCPI 2.3.0 model and maps charging infrastructure to the DATEX II 3.7
Energy Infrastructure model. `OcpiDatexMapping` is the stateless, thread-safe entry point for OCPI
`Location` and DATEX II `EnergyInfrastructureSite`.

The mapping follows the IDACS Deliverable 2.2.1 structure documented in the
[reference catalogue](../references/README.md). It targets the released DATEX II 3.7 model where
the older IDACS draft uses different field names.

## Map a location

```java
OcpiDatexMapping mapping = new OcpiDatexMapping();

EnergyInfrastructureSite site = mapping.toDatex(ocpiLocation);
Location restored = mapping.toOcpi(site);
EnergyInfrastructureTablePublication publication =
        mapping.toDatexPublication(ocpiLocation);
```

`StatusMapper` and `TariffMapper` are separate public mappers for OCPI status and tariff resources;
they are not exposed through `OcpiDatexMapping`.

See the runnable
[OcpiMappingExample](../../examples/src/main/java/dev/juherr/datex4j/examples/OcpiMappingExample.java).

## Conformance matrix

| Area | Status |
|---|---|
| Coordinates, name, operator/owner name, last-updated | Conformant |
| Connector type, format, power, voltage, and current | Conformant |
| Energy mix and green-energy ratio | Conformant |
| Opening hours | Conformant for 24/7 and the supported regular-weekly subset |
| Availability status | Conformant |
| Ad-hoc price | Conformant |
| Address and time zone | Conformant when a location reference exists |
| Suboperator and operator code | Conformant |
| Telephone | Not mappable because OCPI `BusinessDetails` has no phone field |
| Operator website | Mapped to `linkToWebform`; IDACS uses `linkToGeneralInformation` |

The [mapping tests](../../datex4j-ocpi/src/test/java/dev/juherr/datex4j/ocpi/mapping) are the
executable source of truth for this matrix.

## Known boundaries

Address and time-zone values attach to DATEX II `FacilityLocation` through the site's location
reference. A location without coordinates has no current anchor for that extension, so those values
are dropped.

OCPI `country_code` and `party_id` form the DATEX II national organisation number as
`<countryCode>*<partyId>`. Round-tripping expects that form. Telephone has no OCPI source, and the
website target differs from the older IDACS recommendation.

AFIR-specific extension coverage remains separate from the base Energy Infrastructure mapping. See
the [AFIR knowledge base](../afir/README.md) for planned conformance work.
