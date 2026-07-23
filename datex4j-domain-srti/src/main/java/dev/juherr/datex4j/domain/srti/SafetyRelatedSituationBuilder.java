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
package dev.juherr.datex4j.domain.srti;

import dev.juherr.datex4j.builders.PublicationBuilder;
import dev.juherr.datex4j.model.v3_7.situation.Situation;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;

/**
 * Fluent builder for a DATEX II {@code SituationPublication} carrying Safety Related Traffic
 * Information (SRTI).
 *
 * <p>SRTI is exchanged through DATEX II situations whose records describe safety-relevant events.
 * The mandatory publication header is handled by {@link PublicationBuilder}; this builder adds the
 * safety-related situations. Targets the current DATEX II version.
 */
public final class SafetyRelatedSituationBuilder
        extends PublicationBuilder<SituationPublication, SafetyRelatedSituationBuilder> {

    private final SituationPublication publication = new SituationPublication();

    private SafetyRelatedSituationBuilder() {}

    /**
     * Starts building a safety-related situation publication.
     *
     * @return a new builder
     */
    public static SafetyRelatedSituationBuilder safetyPublication() {
        return new SafetyRelatedSituationBuilder();
    }

    /**
     * Adds a safety-related situation to the publication.
     *
     * @param situation the situation to add
     * @return this builder
     */
    public SafetyRelatedSituationBuilder addSituation(Situation situation) {
        publication.getSituation().add(situation);
        return this;
    }

    @Override
    protected SituationPublication publication() {
        return publication;
    }

    @Override
    protected SafetyRelatedSituationBuilder self() {
        return this;
    }
}
