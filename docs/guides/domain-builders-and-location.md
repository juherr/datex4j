# Domain builders and locations

Domain modules provide small DATEX II 3.7 convenience APIs on top of the generated model. They set
common publication headers and leave the generated object graph available for fields that do not
yet have dedicated helpers.

## Choose a domain module

| Module | Primary publication |
|---|---|
| `datex4j-domain-traffic` | `SituationPublication` |
| `datex4j-domain-srti` | `SituationPublication` with SRTI records |
| `datex4j-domain-parking` | `ParkingTablePublication` |
| `datex4j-domain-evcharging` | `EnergyInfrastructureTablePublication` |
| `datex4j-domain-uvar` | `ControlledZoneTablePublication` |

Add only the domain artifacts your application uses. Domain builders currently target the default
DATEX II 3.7 package tree.

## Build a publication

The shared `PublicationBuilder` initializes language, model version, publication time, and creator
metadata. A domain builder adds its publication-specific collections.

```java
SituationPublication publication =
        SituationPublicationBuilder.situationPublication()
                .publishedBy("fr", "my-traffic-service")
                .build();
```

Generated collections remain live JAXB lists. Continue populating the returned publication with
generated DATEX II types when no fluent helper exists.

## Create coordinates

`datex4j-location` contains cross-domain helpers:

```java
PointCoordinates paris =
        Locations.pointCoordinates(48.8566, 2.3522);
```

The helper rejects non-finite or out-of-range coordinates. DATEX II stores latitude and longitude as
32-bit floats, so the conversion intentionally narrows the input values.

The runnable
[DomainBuilderExample](../../examples/src/main/java/dev/juherr/datex4j/examples/DomainBuilderExample.java)
keeps both APIs compiled and tested.
