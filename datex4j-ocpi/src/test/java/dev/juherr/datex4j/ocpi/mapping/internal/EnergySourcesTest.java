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
package dev.juherr.datex4j.ocpi.mapping.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySourceCategory;
import org.junit.jupiter.api.Test;

class EnergySourcesTest {

    @Test
    void toDatexMapsKnownCategories() {
        assertThat(EnergySources.toDatex(EnergySourceCategory.NUCLEAR).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.NUCLEAR);
        assertThat(EnergySources.toDatex(EnergySourceCategory.COAL).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.COAL);
        assertThat(EnergySources.toDatex(EnergySourceCategory.GAS).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.GAS);
        assertThat(EnergySources.toDatex(EnergySourceCategory.SOLAR).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.SOLAR);
        assertThat(EnergySources.toDatex(EnergySourceCategory.WIND).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.WIND);
        assertThat(EnergySources.toDatex(EnergySourceCategory.WATER).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.WATER);
        assertThat(EnergySources.toDatex(EnergySourceCategory.GENERAL_GREEN).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.GENERAL_GREEN);
        assertThat(EnergySources.toDatex(EnergySourceCategory.GENERAL_FOSSIL).getValue())
                .isEqualTo(ElectricEnergySourceTypeEnum.GENERAL_FOSSIL);
    }

    @Test
    void toDatexReturnsNullForNullInput() {
        assertThat(EnergySources.toDatex(null)).isNull();
    }

    @Test
    void toOcpiMapsKnownValuesBack() {
        for (EnergySourceCategory category : EnergySourceCategory.values()) {
            _ElectricEnergySourceTypeEnum wrapper = EnergySources.toDatex(category);
            assertThat(EnergySources.toOcpi(wrapper)).isEqualTo(category);
        }
    }

    @Test
    void toOcpiReturnsNullForValuesWithoutOcpiEquivalent() {
        _ElectricEnergySourceTypeEnum biogas = new _ElectricEnergySourceTypeEnum();
        biogas.setValue(ElectricEnergySourceTypeEnum.BIOGAS);
        assertThat(EnergySources.toOcpi(biogas)).isNull();

        _ElectricEnergySourceTypeEnum other = new _ElectricEnergySourceTypeEnum();
        other.setValue(ElectricEnergySourceTypeEnum.OTHER);
        assertThat(EnergySources.toOcpi(other)).isNull();
    }

    @Test
    void toOcpiReturnsNullForNullInputOrNullValue() {
        assertThat(EnergySources.toOcpi(null)).isNull();
        assertThat(EnergySources.toOcpi(new _ElectricEnergySourceTypeEnum())).isNull();
    }
}
