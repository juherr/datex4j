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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DatexValidationTest {

    private final DatexMarshaller validating =
            DatexXml.builder().validating(true).build();

    @Test
    void writesAndReadsAValidDocumentWhenValidationIsEnabled() {
        byte[] xml = validating.write(Fixtures.situationPublication());

        assertThatCode(() -> validating.read(xml, SituationPublication.class)).doesNotThrowAnyException();
    }

    @Test
    void rejectsADocumentThatViolatesTheSchema() {
        // country is restricted to two characters; make its value invalid (prefix-agnostic).
        String valid = validating.writeToString(Fixtures.situationPublication());
        String invalid = valid.replace(">gb<", ">invalid<");
        byte[] bytes = invalid.getBytes(StandardCharsets.UTF_8);

        assertThatExceptionOfType(DatexXmlException.class)
                .isThrownBy(() -> validating.read(bytes, SituationPublication.class));
    }
}
