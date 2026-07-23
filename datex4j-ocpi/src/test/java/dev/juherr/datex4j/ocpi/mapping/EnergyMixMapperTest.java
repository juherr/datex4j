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
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySource;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySourceCategory;
import java.math.BigDecimal;
import java.util.List;
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

    @Test
    void toDatexMapsEnergySourceRatios() {
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(true);
        EnergySource solar = new EnergySource();
        solar.setSource(EnergySourceCategory.SOLAR);
        solar.setPercentage(BigDecimal.valueOf(60));
        EnergySource wind = new EnergySource();
        wind.setSource(EnergySourceCategory.WIND);
        wind.setPercentage(BigDecimal.valueOf(40));
        ocpi.setEnergySources(List.of(solar, wind));

        ElectricEnergyMix datex = mapper.toDatex(ocpi);

        assertThat(datex.getElectricEnergySourceRatio()).hasSize(2);
        assertThat(datex.getElectricEnergySourceRatio().get(0).getEnergySource().getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.SOLAR);
        assertThat(datex.getElectricEnergySourceRatio()
                        .get(0)
                        .getSourceRatioValue()
                        .getPercentage())
                .isEqualTo(60f);
        assertThat(datex.getElectricEnergySourceRatio().get(1).getEnergySource().getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.WIND);
        assertThat(datex.getElectricEnergySourceRatio()
                        .get(1)
                        .getSourceRatioValue()
                        .getPercentage())
                .isEqualTo(40f);
    }

    @Test
    void roundTripsEnergySourceRatios() {
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(true);
        EnergySource solar = new EnergySource();
        solar.setSource(EnergySourceCategory.SOLAR);
        solar.setPercentage(BigDecimal.valueOf(60));
        EnergySource wind = new EnergySource();
        wind.setSource(EnergySourceCategory.WIND);
        wind.setPercentage(BigDecimal.valueOf(40));
        ocpi.setEnergySources(List.of(solar, wind));

        EnergyMix roundTrip = mapper.toOcpi(mapper.toDatex(ocpi));

        assertThat(roundTrip.getEnergySources()).hasSize(2);
        assertThat(roundTrip.getEnergySources().get(0).getSource()).isEqualTo(EnergySourceCategory.SOLAR);
        assertThat(roundTrip.getEnergySources().get(0).getPercentage()).isEqualByComparingTo("60");
        assertThat(roundTrip.getEnergySources().get(1).getSource()).isEqualTo(EnergySourceCategory.WIND);
        assertThat(roundTrip.getEnergySources().get(1).getPercentage()).isEqualByComparingTo("40");
    }

    @Test
    void roundTripsNonIntegerPercentagesWithoutFloatNoise() {
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(true);
        EnergySource solar = new EnergySource();
        solar.setSource(EnergySourceCategory.SOLAR);
        solar.setPercentage(new BigDecimal("33.3"));
        EnergySource wind = new EnergySource();
        wind.setSource(EnergySourceCategory.WIND);
        wind.setPercentage(new BigDecimal("66.7"));
        ocpi.setEnergySources(List.of(solar, wind));

        EnergyMix roundTrip = mapper.toOcpi(mapper.toDatex(ocpi));

        assertThat(roundTrip.getEnergySources().get(0).getPercentage()).isEqualByComparingTo("33.3");
        assertThat(roundTrip.getEnergySources().get(1).getPercentage()).isEqualByComparingTo("66.7");
    }
}
