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

import dev.juherr.datex4j.domain.traffic.SituationPublicationBuilder;
import dev.juherr.datex4j.location.Locations;
import dev.juherr.datex4j.model.v3_7.locationreferencing.PointCoordinates;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;

/** Uses the DATEX II 3.7 traffic builder and cross-domain location helper. */
public final class DomainBuilderExample {

    private DomainBuilderExample() {}

    /**
     * Runs the example and prints the generated header and coordinates.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        SituationPublication publication = samplePublication();
        PointCoordinates coordinates = sampleCoordinates();

        System.out.println(
                "Published by: " + publication.getPublicationCreator().getNationalIdentifier());
        System.out.println("Coordinates: " + coordinates.getLatitude() + ", " + coordinates.getLongitude());
    }

    static SituationPublication samplePublication() {
        return SituationPublicationBuilder.situationPublication()
                .publishedBy("fr", "datex4j-builder-example")
                .build();
    }

    static PointCoordinates sampleCoordinates() {
        return Locations.pointCoordinates(48.8566, 2.3522);
    }
}
