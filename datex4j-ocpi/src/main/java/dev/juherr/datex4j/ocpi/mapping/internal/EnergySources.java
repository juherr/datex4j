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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._ElectricEnergySourceTypeEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySourceCategory;
import java.util.EnumMap;
import java.util.Map;

/**
 * Translates OCPI {@link EnergySourceCategory} values to the DATEX II energy-source-type enum and
 * back. Unknown OCPI categories map to {@link ElectricEnergySourceTypeEnum#OTHER} with the original
 * name kept in the DATEX II {@code _extendedValue} attribute so no information is lost. DATEX II
 * values with no OCPI equivalent (such as {@code BIOGAS} and {@code OTHER}) map back to {@code
 * null}.
 */
public final class EnergySources {

    private static final Map<EnergySourceCategory, ElectricEnergySourceTypeEnum> TO_DATEX =
            new EnumMap<>(EnergySourceCategory.class);

    static {
        TO_DATEX.put(EnergySourceCategory.NUCLEAR, ElectricEnergySourceTypeEnum.NUCLEAR);
        TO_DATEX.put(EnergySourceCategory.COAL, ElectricEnergySourceTypeEnum.COAL);
        TO_DATEX.put(EnergySourceCategory.GAS, ElectricEnergySourceTypeEnum.GAS);
        TO_DATEX.put(EnergySourceCategory.SOLAR, ElectricEnergySourceTypeEnum.SOLAR);
        TO_DATEX.put(EnergySourceCategory.WIND, ElectricEnergySourceTypeEnum.WIND);
        TO_DATEX.put(EnergySourceCategory.WATER, ElectricEnergySourceTypeEnum.WATER);
        TO_DATEX.put(EnergySourceCategory.GENERAL_GREEN, ElectricEnergySourceTypeEnum.GENERAL_GREEN);
        TO_DATEX.put(EnergySourceCategory.GENERAL_FOSSIL, ElectricEnergySourceTypeEnum.GENERAL_FOSSIL);
    }

    private static final Map<ElectricEnergySourceTypeEnum, EnergySourceCategory> TO_OCPI =
            new EnumMap<>(ElectricEnergySourceTypeEnum.class);

    static {
        TO_DATEX.forEach((ocpi, datex) -> TO_OCPI.put(datex, ocpi));
    }

    private EnergySources() {}

    /**
     * Maps an OCPI energy-source category to a DATEX II energy-source-type wrapper, or {@code null}
     * if {@code category} is null. Unknown categories map to {@link
     * ElectricEnergySourceTypeEnum#OTHER} with the original name preserved in {@code
     * _extendedValue}.
     */
    public static _ElectricEnergySourceTypeEnum toDatex(EnergySourceCategory category) {
        if (category == null) {
            return null;
        }
        _ElectricEnergySourceTypeEnum wrapper = new _ElectricEnergySourceTypeEnum();
        ElectricEnergySourceTypeEnum mapped = TO_DATEX.get(category);
        if (mapped != null) {
            wrapper.setValue(mapped);
        } else {
            wrapper.setValue(ElectricEnergySourceTypeEnum.OTHER);
            wrapper.set_ExtendedValue(category.name());
        }
        return wrapper;
    }

    /**
     * Maps a DATEX II energy-source-type wrapper back to an OCPI energy-source category, or {@code
     * null} if {@code type} or its value is null, or if the value (such as {@code BIOGAS} or {@code
     * OTHER}) has no OCPI equivalent.
     */
    public static EnergySourceCategory toOcpi(_ElectricEnergySourceTypeEnum type) {
        if (type == null || type.getValue() == null) {
            return null;
        }
        return TO_OCPI.get(type.getValue());
    }
}
