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

import dev.juherr.datex4j.ocpi.mapping.OcpiDatexMapping;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;

/** Maps an OCPI 2.3 location to DATEX II 3.7 and back. */
public final class OcpiMappingExample {

    private OcpiMappingExample() {}

    /**
     * Runs the example and prints the round-tripped OCPI identity.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        Location restored = roundTrip();
        System.out.println(restored.getId() + ": " + restored.getName());
    }

    static Location roundTrip() {
        OcpiDatexMapping mapping = new OcpiDatexMapping();
        Location original = sampleLocation();
        return mapping.toOcpi(mapping.toDatex(original));
    }

    private static Location sampleLocation() {
        Location location = new Location();
        location.setId("FR*DX4J*PARIS");
        location.setName("datex4j charging hub");

        GeoLocation coordinates = new GeoLocation();
        coordinates.setLatitude("48.8566");
        coordinates.setLongitude("2.3522");
        location.setCoordinates(coordinates);
        return location;
    }
}
