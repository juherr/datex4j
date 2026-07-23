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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;

/**
 * Maps OCPI {@link EnergyMix} to a DATEX II {@link ElectricEnergyMix} and back (flat fields only).
 *
 * <p><b>Unmapped fields.</b> OCPI {@code energySources}, {@code environImpact}, {@code supplierName}
 * and DATEX II source ratios / provider / rates are not mapped in this iteration.
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
        return datex;
    }

    public EnergyMix toOcpi(ElectricEnergyMix datex) {
        if (datex == null) {
            return null;
        }
        EnergyMix ocpi = new EnergyMix();
        ocpi.setIsGreenEnergy(datex.isIsGreenEnergy());
        ocpi.setEnergyProductName(MultilingualStrings.firstValue(datex.getEnergyProductName()));
        return ocpi;
    }
}
