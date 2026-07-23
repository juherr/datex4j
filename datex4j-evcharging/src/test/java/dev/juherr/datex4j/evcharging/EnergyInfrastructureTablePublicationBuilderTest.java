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
package dev.juherr.datex4j.evcharging;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import org.junit.jupiter.api.Test;

class EnergyInfrastructureTablePublicationBuilderTest {

    @Test
    void buildsAndRoundtripsThroughDatexXml() {
        EnergyInfrastructureTablePublication publication =
                EnergyInfrastructureTablePublicationBuilder.energyInfrastructureTablePublication()
                        .publishedBy("gb", "datex4j-evcharging")
                        .build();

        DatexMarshaller marshaller = DatexXml.createMarshaller();
        byte[] xml = marshaller.write(publication);
        EnergyInfrastructureTablePublication restored =
                marshaller.read(xml, EnergyInfrastructureTablePublication.class);

        assertThat(restored.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j-evcharging");
    }
}
