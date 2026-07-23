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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import org.junit.jupiter.api.Test;

class OcpiDatexMappingTest {

    private final OcpiDatexMapping mapping = new OcpiDatexMapping();

    private Location sampleLocation() {
        Location location = new Location();
        location.setId("LOC-1");
        location.setName("Main Street Hub");
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("52.1");
        geo.setLongitude("4.3");
        location.setCoordinates(geo);
        return location;
    }

    @Test
    void roundTripsLocationThroughDatex() {
        Location roundTrip = mapping.toOcpi(mapping.toDatex(sampleLocation()));
        assertThat(roundTrip.getId()).isEqualTo("LOC-1");
        assertThat(roundTrip.getName()).isEqualTo("Main Street Hub");
    }

    @Test
    void buildsMarshallablePublication() {
        EnergyInfrastructureTablePublication publication = mapping.toDatexPublication(sampleLocation());
        DatexMarshaller marshaller = DatexXml.builder().prettyPrint(true).build();

        String xml = marshaller.writeToString(publication);

        assertThat(xml).contains("payload").contains("LOC-1");
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapping.toDatex(null)).isNull();
        assertThat(mapping.toOcpi(null)).isNull();
    }

    @Test
    void toDatexPublicationHandlesNullArray() {
        EnergyInfrastructureTablePublication publication = mapping.toDatexPublication((Location[]) null);

        assertThat(publication.getEnergyInfrastructureTable()).hasSize(1);
        assertThat(publication.getEnergyInfrastructureTable().get(0).getEnergyInfrastructureSite())
                .isEmpty();
    }

    @Test
    void toDatexPublicationSkipsNullElements() {
        EnergyInfrastructureTablePublication publication = mapping.toDatexPublication(sampleLocation(), null);

        assertThat(publication.getEnergyInfrastructureTable().get(0).getEnergyInfrastructureSite())
                .hasSize(1);
    }
}
