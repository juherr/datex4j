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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ConnectorFormatTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ConnectorTypeEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.ConnectorFormat;
import dev.juherr.datex4j.ocpi.model.v2_3.ConnectorType;
import org.junit.jupiter.api.Test;

class ConnectorMapperTest {

    private final ConnectorMapper mapper = new ConnectorMapper();

    @Test
    void toDatexMapsStandardFormatAndPower() {
        dev.juherr.datex4j.ocpi.model.v2_3.Connector ocpi = new dev.juherr.datex4j.ocpi.model.v2_3.Connector();
        ConnectorType standard = new ConnectorType();
        standard.setActualInstance("IEC_62196_T2");
        ocpi.setStandard(standard);
        ocpi.setFormat(ConnectorFormat.SOCKET);
        ocpi.setMaxElectricPower(22000); // watts

        dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector datex = mapper.toDatex(ocpi);

        assertThat(datex.getConnectorType().getValue()).isEqualTo(ConnectorTypeEnum.IEC_62196_T_2);
        assertThat(datex.getConnectorFormat().getValue()).isEqualTo(ConnectorFormatTypeEnum.SOCKET);
        assertThat(datex.getMaxPowerAtSocket()).isEqualTo(22000f);
    }

    @Test
    void toOcpiReadsBackStandardFormatAndPower() {
        dev.juherr.datex4j.ocpi.model.v2_3.Connector ocpi = new dev.juherr.datex4j.ocpi.model.v2_3.Connector();
        ConnectorType standard = new ConnectorType();
        standard.setActualInstance("IEC_62196_T2");
        ocpi.setStandard(standard);
        ocpi.setFormat(ConnectorFormat.SOCKET);
        ocpi.setMaxElectricPower(22000);

        dev.juherr.datex4j.ocpi.model.v2_3.Connector roundTrip = mapper.toOcpi(mapper.toDatex(ocpi));

        assertThat(roundTrip.getStandard().getActualInstance()).isEqualTo("IEC_62196_T2");
        assertThat(roundTrip.getFormat()).isEqualTo(ConnectorFormat.SOCKET);
        assertThat(roundTrip.getMaxElectricPower()).isEqualTo(22000);
    }

    @Test
    void unknownStandardFallsBackToOther() {
        dev.juherr.datex4j.ocpi.model.v2_3.Connector ocpi = new dev.juherr.datex4j.ocpi.model.v2_3.Connector();
        ConnectorType standard = new ConnectorType();
        standard.setActualInstance("SOME_FUTURE_PLUG");
        ocpi.setStandard(standard);

        dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector datex = mapper.toDatex(ocpi);

        assertThat(datex.getConnectorType().getValue()).isEqualTo(ConnectorTypeEnum.OTHER);
        assertThat(datex.getConnectorType().get_ExtendedValue()).isEqualTo("SOME_FUTURE_PLUG");
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
