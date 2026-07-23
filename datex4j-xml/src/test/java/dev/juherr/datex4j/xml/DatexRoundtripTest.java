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
package dev.juherr.datex4j.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import org.junit.jupiter.api.Test;

class DatexRoundtripTest {

    private final DatexMarshaller marshaller = DatexXml.createMarshaller();

    @Test
    void writesThenReadsBackAnEquivalentPublication() {
        SituationPublication original = Fixtures.situationPublication();

        byte[] xml = marshaller.write(original);
        SituationPublication restored = marshaller.read(xml, SituationPublication.class);

        assertThat(restored.getLang()).isEqualTo(original.getLang());
        assertThat(restored.getModelBaseVersion()).isEqualTo(original.getModelBaseVersion());
        assertThat(restored.getPublicationTime()).isEqualTo(original.getPublicationTime());
        assertThat(restored.getPublicationCreator().getCountry())
                .isEqualTo(original.getPublicationCreator().getCountry());
        assertThat(restored.getPublicationCreator().getNationalIdentifier())
                .isEqualTo(original.getPublicationCreator().getNationalIdentifier());
    }

    @Test
    void writeToStringProducesReadableXml() {
        assertThat(marshaller.writeToString(Fixtures.situationPublication())).startsWith("<?xml");
    }

    @Test
    void rejectsValuesThatAreNotDatexPublications() {
        assertThatIllegalArgumentException().isThrownBy(() -> marshaller.write("not a publication"));
    }

    @Test
    void rejectsNullValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> marshaller.write(null));
    }
}
