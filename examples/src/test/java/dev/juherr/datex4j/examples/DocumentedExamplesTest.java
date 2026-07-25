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

import org.junit.jupiter.api.Test;

/** Keeps every example linked from the user guides executable and behaviorally verified. */
class DocumentedExamplesTest {

    @Test
    void jsonExampleRoundTripsACompletePublication() {
        var restored = JsonExample.roundTrip();

        assertThat(restored.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j-json-example");
    }

    @Test
    void validationExampleCollectsTheExpectedError() {
        var result = ValidationExample.validateInvalidCountry();

        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).isNotEmpty();
    }

    @Test
    void domainExampleUsesTheBuilderAndLocationHelper() {
        var publication = DomainBuilderExample.samplePublication();
        var coordinates = DomainBuilderExample.sampleCoordinates();

        assertThat(publication.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j-builder-example");
        assertThat(coordinates.getLatitude()).isEqualTo((float) 48.8566);
        assertThat(coordinates.getLongitude()).isEqualTo((float) 2.3522);
    }

    @Test
    void ocpiExampleRoundTripsThroughDatex() {
        var restored = OcpiMappingExample.roundTrip();

        assertThat(restored.getId()).isEqualTo("FR*DX4J*PARIS");
        assertThat(restored.getName()).isEqualTo("datex4j charging hub");
    }

    @Test
    void everyDocumentedMainMethodRuns() {
        assertThatCode(() -> JsonExample.main(new String[0])).doesNotThrowAnyException();
        assertThatCode(() -> ValidationExample.main(new String[0])).doesNotThrowAnyException();
        assertThatCode(() -> DomainBuilderExample.main(new String[0])).doesNotThrowAnyException();
        assertThatCode(() -> OcpiMappingExample.main(new String[0])).doesNotThrowAnyException();
    }
}
