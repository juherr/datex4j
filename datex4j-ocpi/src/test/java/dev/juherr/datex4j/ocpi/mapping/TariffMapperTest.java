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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyPricingPolicy;
import dev.juherr.datex4j.ocpi.model.v2_3.DisplayText;
import dev.juherr.datex4j.ocpi.model.v2_3.Tariff;
import java.util.List;
import org.junit.jupiter.api.Test;

class TariffMapperTest {

    private final TariffMapper mapper = new TariffMapper();

    @Test
    void toDatexMapsAltTextToAdditionalInformation() {
        Tariff tariff = new Tariff();
        DisplayText text = new DisplayText();
        text.setLanguage("fr");
        text.setText("Tarif standard");
        tariff.setTariffAltText(List.of(text));

        EnergyPricingPolicy policy = mapper.toDatex(tariff);

        assertThat(policy.getAdditionalInformation()
                        .getValues()
                        .getValue()
                        .get(0)
                        .getValue())
                .isEqualTo("Tarif standard");
    }

    @Test
    void toDatexMapsAltTextLanguage() {
        Tariff tariff = new Tariff();
        DisplayText text = new DisplayText();
        text.setLanguage("fr");
        text.setText("Tarif standard");
        tariff.setTariffAltText(List.of(text));

        EnergyPricingPolicy policy = mapper.toDatex(tariff);

        assertThat(policy.getAdditionalInformation()
                        .getValues()
                        .getValue()
                        .get(0)
                        .getLang())
                .isEqualTo("fr");
    }

    @Test
    void roundTripsAltText() {
        Tariff tariff = new Tariff();
        DisplayText text = new DisplayText();
        text.setLanguage("fr");
        text.setText("Tarif standard");
        tariff.setTariffAltText(List.of(text));

        Tariff roundTrip = mapper.toOcpi(mapper.toDatex(tariff));

        assertThat(roundTrip.getTariffAltText().get(0).getText()).isEqualTo("Tarif standard");
    }

    @Test
    void roundTripsAltTextLanguage() {
        Tariff tariff = new Tariff();
        DisplayText text = new DisplayText();
        text.setLanguage("fr");
        text.setText("Tarif standard");
        tariff.setTariffAltText(List.of(text));

        Tariff roundTrip = mapper.toOcpi(mapper.toDatex(tariff));

        assertThat(roundTrip.getTariffAltText().get(0).getLanguage()).isEqualTo("fr");
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
