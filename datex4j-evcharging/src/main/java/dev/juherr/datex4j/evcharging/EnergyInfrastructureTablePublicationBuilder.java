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

import dev.juherr.datex4j.builders.PublicationBuilder;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTable;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;

/**
 * Fluent builder for a DATEX II {@code EnergyInfrastructureTablePublication}, the static description
 * of charging/refuelling infrastructure in the EV Charging user domain.
 *
 * <p>The mandatory publication header is handled by {@link PublicationBuilder}; this builder adds
 * energy infrastructure tables. Targets the current DATEX II version. For AFIR-regulation reporting,
 * the parallel {@code afirenergyinfrastructure} model types are also available in {@code
 * datex4j-model}.
 */
public final class EnergyInfrastructureTablePublicationBuilder
        extends PublicationBuilder<EnergyInfrastructureTablePublication, EnergyInfrastructureTablePublicationBuilder> {

    private final EnergyInfrastructureTablePublication publication = new EnergyInfrastructureTablePublication();

    private EnergyInfrastructureTablePublicationBuilder() {}

    /**
     * Starts building an energy infrastructure table publication.
     *
     * @return a new builder
     */
    public static EnergyInfrastructureTablePublicationBuilder energyInfrastructureTablePublication() {
        return new EnergyInfrastructureTablePublicationBuilder();
    }

    /**
     * Adds an energy infrastructure table to the publication.
     *
     * @param table the energy infrastructure table to add
     * @return this builder
     */
    public EnergyInfrastructureTablePublicationBuilder addEnergyInfrastructureTable(EnergyInfrastructureTable table) {
        publication.getEnergyInfrastructureTable().add(table);
        return this;
    }

    @Override
    protected EnergyInfrastructureTablePublication publication() {
        return publication;
    }

    @Override
    protected EnergyInfrastructureTablePublicationBuilder self() {
        return this;
    }
}
