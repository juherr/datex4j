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
package dev.juherr.datex4j.uvar;

import dev.juherr.datex4j.builders.PublicationBuilder;
import dev.juherr.datex4j.model.v3_7.controlledzone.ControlledZoneTable;
import dev.juherr.datex4j.model.v3_7.controlledzone.ControlledZoneTablePublication;

/**
 * Fluent builder for a DATEX II {@code ControlledZoneTablePublication}, describing access-controlled
 * zones in the Urban Vehicle Access Regulations (UVAR) user domain.
 *
 * <p>The mandatory publication header is handled by {@link PublicationBuilder}; this builder adds
 * controlled zone tables. Targets the current DATEX II version.
 */
public final class ControlledZoneTablePublicationBuilder
        extends PublicationBuilder<ControlledZoneTablePublication, ControlledZoneTablePublicationBuilder> {

    private final ControlledZoneTablePublication publication = new ControlledZoneTablePublication();

    private ControlledZoneTablePublicationBuilder() {}

    /**
     * Starts building a controlled zone table publication.
     *
     * @return a new builder
     */
    public static ControlledZoneTablePublicationBuilder controlledZoneTablePublication() {
        return new ControlledZoneTablePublicationBuilder();
    }

    /**
     * Adds a controlled zone table to the publication.
     *
     * @param table the controlled zone table to add
     * @return this builder
     */
    public ControlledZoneTablePublicationBuilder addControlledZoneTable(ControlledZoneTable table) {
        publication.getControlledZoneTable().add(table);
        return this;
    }

    @Override
    protected ControlledZoneTablePublication publication() {
        return publication;
    }

    @Override
    protected ControlledZoneTablePublicationBuilder self() {
        return this;
    }
}
