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

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.ocpi.model.v2_3.EVSE;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

    private final LocationMapper mapper = new LocationMapper();

    private Location sampleLocation() {
        Location location = new Location();
        location.setId("LOC-1");
        location.setName("Main Street Hub");
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("52.1");
        geo.setLongitude("4.3");
        location.setCoordinates(geo);
        EVSE evse = new EVSE();
        evse.setUid("EVSE-1");
        location.setEvses(List.of(evse));
        return location;
    }

    @Test
    void toDatexBuildsSiteWithNameAndStation() {
        EnergyInfrastructureSite site = mapper.toDatex(sampleLocation());

        assertThat(site.getId()).isEqualTo("LOC-1");
        assertThat(site.getName().getValues().getValue().get(0).getValue()).isEqualTo("Main Street Hub");
        assertThat(site.getEnergyInfrastructureStation()).hasSize(1);
        assertThat(site.getEnergyInfrastructureStation().get(0).getId()).isEqualTo("EVSE-1");
    }

    @Test
    void roundTripsIdNameAndEvseCount() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(sampleLocation()));

        assertThat(roundTrip.getId()).isEqualTo("LOC-1");
        assertThat(roundTrip.getName()).isEqualTo("Main Street Hub");
        assertThat(roundTrip.getEvses()).hasSize(1);
    }

    @Test
    void toDatexSkipsNullEvseElements() {
        Location location = sampleLocation();
        EVSE evse = location.getEvses().get(0);
        location.setEvses(Arrays.asList(evse, null));

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getEnergyInfrastructureStation()).hasSize(1).doesNotContainNull();
    }

    @Test
    void toOcpiSkipsNullStationElements() {
        EnergyInfrastructureSite site = mapper.toDatex(sampleLocation());
        site.getEnergyInfrastructureStation().add(null);

        Location location = mapper.toOcpi(site);

        assertThat(location.getEvses()).hasSize(1).doesNotContainNull();
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
