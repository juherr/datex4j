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

import dev.juherr.datex4j.model.v3_7.common.PercentageValue;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergySourceRatio;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.ocpi.mapping.internal.EnergySources;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySource;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySourceCategory;
import java.math.BigDecimal;

/**
 * Maps OCPI {@link EnergyMix} to a DATEX II {@link ElectricEnergyMix} and back, including the
 * energy-source ratio breakdown.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code environImpact}, {@code supplierName} and DATEX II
 * provider / rates are not mapped in this iteration.
 */
public final class EnergyMixMapper {

    private static final String DEFAULT_LANG = "en";

    public ElectricEnergyMix toDatex(EnergyMix ocpi) {
        if (ocpi == null) {
            return null;
        }
        ElectricEnergyMix datex = new ElectricEnergyMix();
        datex.setIsGreenEnergy(ocpi.getIsGreenEnergy());
        datex.setEnergyProductName(MultilingualStrings.of(DEFAULT_LANG, ocpi.getEnergyProductName()));
        if (ocpi.getEnergySources() != null) {
            for (EnergySource source : ocpi.getEnergySources()) {
                if (source == null) {
                    continue;
                }
                _ElectricEnergySourceTypeEnum energySource = EnergySources.toDatex(source.getSource());
                if (energySource == null) {
                    continue;
                }
                ElectricEnergySourceRatio ratio = new ElectricEnergySourceRatio();
                ratio.setEnergySource(energySource);
                ratio.setSourceRatioValue(toPercentageValue(source.getPercentage()));
                datex.getElectricEnergySourceRatio().add(ratio);
            }
        }
        return datex;
    }

    public EnergyMix toOcpi(ElectricEnergyMix datex) {
        if (datex == null) {
            return null;
        }
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(datex.isIsGreenEnergy());
        ocpi.setEnergyProductName(MultilingualStrings.firstValue(datex.getEnergyProductName()));
        for (ElectricEnergySourceRatio ratio : datex.getElectricEnergySourceRatio()) {
            if (ratio == null) {
                continue;
            }
            EnergySourceCategory category = EnergySources.toOcpi(ratio.getEnergySource());
            if (category == null) {
                continue;
            }
            EnergySource source = new EnergySource();
            source.setSource(category);
            source.setPercentage(toBigDecimal(ratio.getSourceRatioValue()));
            ocpi.addEnergySourcesItem(source);
        }
        return ocpi;
    }

    private static PercentageValue toPercentageValue(BigDecimal percentage) {
        if (percentage == null) {
            return null;
        }
        PercentageValue value = new PercentageValue();
        value.setPercentage(percentage.floatValue());
        return value;
    }

    private static BigDecimal toBigDecimal(PercentageValue percentage) {
        if (percentage == null) {
            return null;
        }
        return new BigDecimal(Float.toString(percentage.getPercentage()));
    }
}
