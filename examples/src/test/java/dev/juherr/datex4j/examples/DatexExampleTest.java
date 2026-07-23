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
package dev.juherr.datex4j.examples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Ensures the example stays correct and runnable as the library evolves. */
class DatexExampleTest {

    @Test
    void exampleWritesReadsAndValidates() {
        DatexMarshaller marshaller = DatexXml.builder().validating(true).build();
        SituationPublication publication = DatexExample.sampleSituationPublication();

        String xml = marshaller.writeToString(publication);
        assertThat(xml).contains("http://datex2.eu/schema/3/d2Payload");

        SituationPublication restored =
                marshaller.read(xml.getBytes(StandardCharsets.UTF_8), SituationPublication.class);
        assertThat(restored.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j-examples");
    }

    @Test
    void mainRunsWithoutError() {
        assertThatCode(() -> DatexExample.main(new String[0])).doesNotThrowAnyException();
    }
}
