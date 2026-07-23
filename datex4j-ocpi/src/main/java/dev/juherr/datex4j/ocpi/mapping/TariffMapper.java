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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyPricingPolicy;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.DisplayText;
import dev.juherr.datex4j.ocpi.model.v2_3.Tariff;
import java.util.List;

/**
 * Maps the safe overlap between OCPI {@link Tariff} and DATEX II {@link EnergyPricingPolicy}.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code elements}, {@code currency}, {@code type}, {@code
 * minPrice}/{@code maxPrice}, {@code preauthorizeAmount}, {@code energyMix} beyond alt text, and
 * DATEX II {@code pricingPolicy}, {@code minimumDeliveryFee}/{@code maximumDeliveryFee}, {@code
 * discount}, {@code combinationWithParkingFee} are not mapped in this iteration. The two models
 * diverge heavily; only the alt text / additional information overlap is mapped.
 */
public final class TariffMapper {

    private static final String DEFAULT_LANG = "en";

    public EnergyPricingPolicy toDatex(Tariff tariff) {
        if (tariff == null) {
            return null;
        }
        EnergyPricingPolicy policy = new EnergyPricingPolicy();
        String altText = firstAltText(tariff.getTariffAltText());
        if (altText != null) {
            policy.setAdditionalInformation(MultilingualStrings.of(DEFAULT_LANG, altText));
        }
        return policy;
    }

    public Tariff toOcpi(EnergyPricingPolicy policy) {
        if (policy == null) {
            return null;
        }
        Tariff tariff = new Tariff();
        String info = MultilingualStrings.firstValue(policy.getAdditionalInformation());
        if (info != null) {
            DisplayText text = new DisplayText();
            text.setLanguage(DEFAULT_LANG);
            text.setText(info);
            tariff.setTariffAltText(List.of(text));
        }
        return tariff;
    }

    private static String firstAltText(List<DisplayText> texts) {
        if (texts == null || texts.isEmpty()) {
            return null;
        }
        return texts.get(0).getText();
    }
}
