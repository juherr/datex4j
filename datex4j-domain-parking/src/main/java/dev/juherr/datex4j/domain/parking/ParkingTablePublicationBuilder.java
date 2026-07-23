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
package dev.juherr.datex4j.domain.parking;

import dev.juherr.datex4j.builders.PublicationBuilder;
import dev.juherr.datex4j.model.v3_7.parking.ParkingTable;
import dev.juherr.datex4j.model.v3_7.parking.ParkingTablePublication;

/**
 * Fluent builder for a DATEX II {@code ParkingTablePublication}, the static description of parking
 * sites in the Parking user domain.
 *
 * <p>The mandatory publication header is handled by {@link PublicationBuilder}; this builder adds
 * parking tables. Targets the current DATEX II version.
 */
public final class ParkingTablePublicationBuilder
        extends PublicationBuilder<ParkingTablePublication, ParkingTablePublicationBuilder> {

    private final ParkingTablePublication publication = new ParkingTablePublication();

    private ParkingTablePublicationBuilder() {}

    /**
     * Starts building a parking table publication.
     *
     * @return a new builder
     */
    public static ParkingTablePublicationBuilder parkingTablePublication() {
        return new ParkingTablePublicationBuilder();
    }

    /**
     * Adds a parking table to the publication.
     *
     * @param parkingTable the parking table to add
     * @return this builder
     */
    public ParkingTablePublicationBuilder addParkingTable(ParkingTable parkingTable) {
        publication.getParkingTable().add(parkingTable);
        return this;
    }

    @Override
    protected ParkingTablePublication publication() {
        return publication;
    }

    @Override
    protected ParkingTablePublicationBuilder self() {
        return this;
    }
}
