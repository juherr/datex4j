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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureStation;
import dev.juherr.datex4j.ocpi.model.v2_3.Connector;
import dev.juherr.datex4j.ocpi.model.v2_3.ConnectorFormat;
import dev.juherr.datex4j.ocpi.model.v2_3.ConnectorType;
import dev.juherr.datex4j.ocpi.model.v2_3.EVSE;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import java.util.List;
import org.junit.jupiter.api.Test;

class EvseMapperTest {

    private final EvseMapper mapper = new EvseMapper();

    private EVSE sampleEvse() {
        EVSE evse = new EVSE();
        evse.setUid("EVSE-1");
        evse.setEvseId("NL*TNM*E0001");
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("52.1");
        geo.setLongitude("4.3");
        evse.setCoordinates(geo);
        Connector connector = new Connector();
        ConnectorType standard = new ConnectorType();
        standard.setActualInstance("IEC_62196_T2");
        connector.setStandard(standard);
        connector.setFormat(ConnectorFormat.SOCKET);
        evse.setConnectors(List.of(connector));
        return evse;
    }

    @Test
    void toDatexBuildsStationWithChargingPointAndConnector() {
        EnergyInfrastructureStation station = mapper.toDatex(sampleEvse());

        assertThat(station.getId()).isEqualTo("EVSE-1");
        assertThat(station.getExternalIdentifier()).isEqualTo("NL*TNM*E0001");
        assertThat(station.getRefillPoint()).hasSize(1);
        ElectricChargingPoint point =
                (ElectricChargingPoint) station.getRefillPoint().get(0);
        assertThat(point.getConnector()).hasSize(1);
    }

    @Test
    void roundTripsIdentityAndConnectorCount() {
        EVSE roundTrip = mapper.toOcpi(mapper.toDatex(sampleEvse()));

        assertThat(roundTrip.getUid()).isEqualTo("EVSE-1");
        assertThat(roundTrip.getEvseId()).isEqualTo("NL*TNM*E0001");
        assertThat(roundTrip.getConnectors()).hasSize(1);
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
