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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import org.junit.jupiter.api.Test;

class EnergyMixMapperTest {

    private final EnergyMixMapper mapper = new EnergyMixMapper();

    @Test
    void toDatexMapsGreenFlagAndProductName() {
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(true);
        ocpi.setEnergyProductName("GreenPower");

        ElectricEnergyMix datex = mapper.toDatex(ocpi);

        assertThat(datex.isIsGreenEnergy()).isTrue();
        assertThat(datex.getEnergyProductName().getValues().getValue().get(0).getValue())
                .isEqualTo("GreenPower");
    }

    @Test
    void roundTripsFlatFields() {
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(true);
        ocpi.setEnergyProductName("GreenPower");

        EnergyMix roundTrip = mapper.toOcpi(mapper.toDatex(ocpi));

        assertThat(roundTrip.getIsGreenEnergy()).isTrue();
        assertThat(roundTrip.getEnergyProductName()).isEqualTo("GreenPower");
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
