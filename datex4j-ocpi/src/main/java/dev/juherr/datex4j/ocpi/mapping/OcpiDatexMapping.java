/*
 * Copyright 2026 the datex4j authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.juherr.datex4j.ocpi.mapping;

import dev.juherr.datex4j.domain.evcharging.EnergyInfrastructureTablePublicationBuilder;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTable;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;

/**
 * Public entry point converting the OCPI {@link Location} &harr; DATEX II {@link
 * EnergyInfrastructureSite} object graph. Stateless and thread-safe. Delegates to {@link
 * LocationMapper} and, transitively, the EVSE/connector/geo-location/organisation/energy-mix/hours
 * mappers it composes. Can also wrap the result in a ready-to-marshal {@link
 * EnergyInfrastructureTablePublication}.
 *
 * <p>{@link StatusMapper} and {@link TariffMapper} are independent, directly-instantiated mappers
 * for the separate OCPI {@code Status} and {@code Tariff} resources; they are not reachable
 * through this facade.
 */
public final class OcpiDatexMapping {

    private final LocationMapper locationMapper = new LocationMapper();

    /** Converts an OCPI location to a DATEX II site (null → null). */
    public EnergyInfrastructureSite toDatex(Location location) {
        return locationMapper.toDatex(location);
    }

    /** Converts a DATEX II site to an OCPI location (null → null). */
    public Location toOcpi(EnergyInfrastructureSite site) {
        return locationMapper.toOcpi(site);
    }

    /** Wraps one or more OCPI locations in a marshallable DATEX II publication. */
    public EnergyInfrastructureTablePublication toDatexPublication(Location... locations) {
        EnergyInfrastructureTable table = new EnergyInfrastructureTable();
        if (locations != null) {
            for (Location location : locations) {
                EnergyInfrastructureSite site = toDatex(location);
                if (site != null) {
                    table.getEnergyInfrastructureSite().add(site);
                }
            }
        }
        return EnergyInfrastructureTablePublicationBuilder.energyInfrastructureTablePublication()
                .addEnergyInfrastructureTable(table)
                .build();
    }
}
