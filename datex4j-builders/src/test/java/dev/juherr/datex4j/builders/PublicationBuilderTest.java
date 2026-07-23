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
package dev.juherr.datex4j.builders;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class PublicationBuilderTest {

    /** Minimal concrete builder used to exercise the shared header logic. */
    private static final class TestBuilder extends PublicationBuilder<SituationPublication, TestBuilder> {
        private final SituationPublication publication = new SituationPublication();

        @Override
        protected SituationPublication publication() {
            return publication;
        }

        @Override
        protected TestBuilder self() {
            return this;
        }
    }

    @Test
    void appliesSensibleHeaderDefaults() {
        SituationPublication publication = new TestBuilder().build();

        assertThat(publication.getLang()).isEqualTo("en");
        assertThat(publication.getModelBaseVersion()).isEqualTo("3");
        assertThat(publication.getPublicationTime()).isNotNull();
    }

    @Test
    void appliesConfiguredHeader() {
        OffsetDateTime time = OffsetDateTime.parse("2026-07-23T10:15:30Z");

        SituationPublication publication = new TestBuilder()
                .lang("fr")
                .modelBaseVersion("3")
                .publicationTime(time)
                .publishedBy("fr", "datex4j")
                .build();

        assertThat(publication.getLang()).isEqualTo("fr");
        assertThat(publication.getPublicationCreator().getCountry()).isEqualTo("fr");
        assertThat(publication.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j");
        assertThat(publication.getPublicationTime().getYear()).isEqualTo(2026);
    }
}
