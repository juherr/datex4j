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
import dev.juherr.datex4j.ocpi.mapping.internal.Organisations;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySource;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergySourceCategory;
import dev.juherr.datex4j.ocpi.model.v2_3.EnvironmentalImpact;
import dev.juherr.datex4j.ocpi.model.v2_3.EnvironmentalImpactCategory;
import java.math.BigDecimal;

/**
 * Maps OCPI {@link EnergyMix} to a DATEX II {@link ElectricEnergyMix} and back, including the
 * energy-source ratio breakdown, the supplier (energy provider) and environmental impacts.
 *
 * <p><b>Deviation note.</b> The OCPI spec models {@code EnvironmentalImpactCategory} as an
 * extensible {@code anyOf} (a known string enum plus an open-ended string), so the generated
 * {@link EnvironmentalImpactCategory} is a wrapper class, not a Java {@code enum}. This mapper
 * compares its {@link EnvironmentalImpactCategory#getActualInstance()} against the raw
 * {@code "CARBON_DIOXIDE"} / {@code "NUCLEAR_WASTE"} strings instead of enum constants.
 *
 * <p><b>Unmapped fields.</b> DATEX II energy provider fields other than the name (website, logo,
 * etc.) and environmental impact categories other than carbon dioxide / nuclear waste are not
 * mapped in this iteration.
 */
public final class EnergyMixMapper {

    private static final String DEFAULT_LANG = "en";
    private static final String CARBON_DIOXIDE = "CARBON_DIOXIDE";
    private static final String NUCLEAR_WASTE = "NUCLEAR_WASTE";

    public ElectricEnergyMix toDatex(EnergyMix ocpi) {
        if (ocpi == null) {
            return null;
        }
        ElectricEnergyMix datex = new ElectricEnergyMix();
        datex.setIsGreenEnergy(ocpi.getIsGreenEnergy());
        datex.setEnergyProductName(MultilingualStrings.of(DEFAULT_LANG, ocpi.getEnergyProductName()));
        datex.setEnergyProvider(Organisations.named(ocpi.getSupplierName()));
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
        if (ocpi.getEnvironImpact() != null) {
            for (EnvironmentalImpact impact : ocpi.getEnvironImpact()) {
                applyEnvironmentalImpact(datex, impact);
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
        ocpi.setSupplierName(Organisations.nameOf(datex.getEnergyProvider()));
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
        if (datex.getCarbonDioxideImpact() != null) {
            ocpi.addEnvironImpactItem(toEnvironmentalImpact(CARBON_DIOXIDE, datex.getCarbonDioxideImpact()));
        }
        if (datex.getNuclearWasteImpact() != null) {
            ocpi.addEnvironImpactItem(toEnvironmentalImpact(NUCLEAR_WASTE, datex.getNuclearWasteImpact()));
        }
        return ocpi;
    }

    private static void applyEnvironmentalImpact(ElectricEnergyMix datex, EnvironmentalImpact impact) {
        if (impact == null) {
            return;
        }
        String category = toCategoryString(impact.getCategory());
        if (category == null) {
            return;
        }
        Float amount = toFloat(impact.getAmount());
        if (CARBON_DIOXIDE.equals(category)) {
            datex.setCarbonDioxideImpact(amount);
        } else if (NUCLEAR_WASTE.equals(category)) {
            datex.setNuclearWasteImpact(amount);
        }
    }

    private static String toCategoryString(EnvironmentalImpactCategory category) {
        if (category == null) {
            return null;
        }
        Object actual = category.getActualInstance();
        return actual instanceof String value ? value : null;
    }

    private static EnvironmentalImpact toEnvironmentalImpact(String category, Float amount) {
        EnvironmentalImpact impact = new EnvironmentalImpact();
        impact.setCategory(new EnvironmentalImpactCategory(category));
        impact.setAmount(new BigDecimal(Float.toString(amount)));
        return impact;
    }

    private static Float toFloat(BigDecimal amount) {
        return amount == null ? null : amount.floatValue();
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
