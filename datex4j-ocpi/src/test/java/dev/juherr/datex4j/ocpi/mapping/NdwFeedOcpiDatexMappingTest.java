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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ConnectorTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureStation;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.model.v3_7.facilities.OrganisationSpecification;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import dev.juherr.datex4j.ocpi.support.JSON;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Exercises the OCPI &rarr; DATEX II mapper against a real, unmodified OCPI 2.2.1 location taken from
 * the Netherlands NAP open-data feed ({@code charging_point_locations_ocpi.json.gz}, CC0). The full
 * feed is a ~168 MB JSON array; the fixture is trimmed to one location. See {@code
 * ndw-locations.v2_2_1.README.txt} for provenance.
 *
 * <p>Although the fixture is OCPI 2.2.1 and the module ships an OCPI 2.3.0 model, the core
 * Location/EVSE/Connector fields are stable and deserialize cleanly through the module's own Jackson
 * mapper ({@link JSON}), which tolerates unknown properties.
 */
class NdwFeedOcpiDatexMappingTest {

    private static final String FIXTURE = "/ocpi/ndw-locations.v2_2_1.json";

    private final OcpiDatexMapping mapping = new OcpiDatexMapping();

    private static List<Location> readFixture() throws Exception {
        ObjectMapper mapper = JSON.getDefault().getMapper();
        try (InputStream in = NdwFeedOcpiDatexMappingTest.class.getResourceAsStream(FIXTURE)) {
            assertThat(in).as("fixture on classpath").isNotNull();
            return mapper.readValue(in, new TypeReference<List<Location>>() {});
        }
    }

    @Test
    void deserializesRealNdwLocation() throws Exception {
        List<Location> locations = readFixture();

        assertThat(locations).hasSize(1);
        Location location = locations.get(0);
        assertThat(location.getId()).isEqualTo("af101dc8-3629-40d6-9093-d5e83d9bfc47");
        assertThat(location.getName()).isEqualTo("Wibauthof");
        assertThat(location.getCountryCode()).isEqualTo("NL");
        assertThat(location.getPartyId()).isEqualTo("NUO");
        assertThat(location.getCoordinates().getLatitude()).isEqualTo("51.727916");
        assertThat(location.getEvses()).hasSize(2);
        assertThat(location.getEvses().get(0).getConnectors()).hasSize(1);
        assertThat(location.getEvses()
                        .get(0)
                        .getConnectors()
                        .get(0)
                        .getStandard()
                        .getString())
                .isEqualTo("IEC_62196_T2");
    }

    @Test
    void convertsRealNdwLocationToDatexSite() throws Exception {
        Location location = readFixture().get(0);

        EnergyInfrastructureSite site = mapping.toDatex(location);

        assertThat(site).isNotNull();
        assertThat(site.getId()).isEqualTo("af101dc8-3629-40d6-9093-d5e83d9bfc47");
        assertThat(site.getName().getValues().getValue()).isNotEmpty();
        assertThat(site.getLocationReference()).isNotNull();
        // eMI3 operator id built from OCPI country_code + party_id.
        assertThat(site.getOperator()).isInstanceOf(OrganisationSpecification.class);
        assertThat(((OrganisationSpecification) site.getOperator()).getNationalOrganisationNumber())
                .isEqualTo("NL*NUO");

        assertThat(site.getEnergyInfrastructureStation()).hasSize(2);
        EnergyInfrastructureStation station =
                site.getEnergyInfrastructureStation().get(0);
        assertThat(station.getId()).isEqualTo("9382d399-230d-49e5-a219-31fdd424750d");
        assertThat(station.getExternalIdentifier()).isEqualTo("NL*NUO*EALF*0065319*2");

        ElectricChargingPoint point =
                (ElectricChargingPoint) station.getRefillPoint().get(0);
        assertThat(point.getConnector()).hasSize(1);
        Connector connector = point.getConnector().get(0);
        assertThat(connector.getConnectorType().getValue()).isEqualTo(ConnectorTypeEnum.IEC_62196_T_2);
        assertThat(connector.getMaxPowerAtSocket()).isEqualTo(11040.0f);
        assertThat(connector.getVoltage()).isEqualTo(230.0f);
        assertThat(connector.getMaximumCurrent()).isEqualTo(16.0f);
    }

    @Test
    void marshalsRealNdwPublicationAsDatexXml() throws Exception {
        Location location = readFixture().get(0);

        EnergyInfrastructureTablePublication publication = mapping.toDatexPublication(location);
        DatexMarshaller marshaller = DatexXml.builder().prettyPrint(true).build();

        String xml = marshaller.writeToString(publication);

        assertThat(xml)
                .contains("payload")
                .contains("af101dc8-3629-40d6-9093-d5e83d9bfc47")
                .contains("NL*NUO*EALF*0065319*2");
    }

    @Test
    void roundTripsRealNdwLocationBackToOcpi() throws Exception {
        Location location = readFixture().get(0);

        Location roundTrip = mapping.toOcpi(mapping.toDatex(location));

        assertThat(roundTrip).isNotNull();
        assertThat(roundTrip.getId()).isEqualTo("af101dc8-3629-40d6-9093-d5e83d9bfc47");
        assertThat(roundTrip.getCountryCode()).isEqualTo("NL");
        assertThat(roundTrip.getPartyId()).isEqualTo("NUO");
        assertThat(roundTrip.getEvses()).hasSize(2);
    }
}
