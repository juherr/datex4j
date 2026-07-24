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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import dev.juherr.datex4j.core.DatexVersion;
import org.junit.jupiter.api.Test;

/** Verifies that both bundled DATEX II versions can be marshalled with a version-specific facade. */
class DatexMultiVersionTest {

    @Test
    void v35MarshallerRoundtripsAv35Publication() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V3_5).validating(true).build();

        var original = Fixtures.situationPublicationV35();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v3_5.situation.SituationPublication.class);

        assertThat(restored.getPublicationCreator().getNationalIdentifier())
                .isEqualTo(original.getPublicationCreator().getNationalIdentifier());
    }

    @Test
    void v36MarshallerRoundtripsAv36Publication() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V3_6).validating(true).build();

        var original = Fixtures.situationPublicationV36();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v3_6.situation.SituationPublication.class);

        assertThat(restored.getPublicationCreator().getNationalIdentifier())
                .isEqualTo(original.getPublicationCreator().getNationalIdentifier());
    }

    @Test
    void v37MarshallerRoundtripsAv37Publication() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V3_7).validating(true).build();

        var original = Fixtures.situationPublication();
        byte[] xml = marshaller.write(original);

        assertThatCode(() -> marshaller.read(xml, dev.juherr.datex4j.model.v3_7.situation.SituationPublication.class))
                .doesNotThrowAnyException();
    }

    @Test
    void aVersionMarshallerRejectsAnotherVersionsPublication() {
        DatexMarshaller v36 = DatexXml.builder().version(DatexVersion.V3_6).build();

        // A v3.7 publication is not a v3.6 PayloadPublication, so it cannot be wrapped.
        assertThatIllegalArgumentException().isThrownBy(() -> v36.write(Fixtures.situationPublication()));
    }
}
