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
package dev.juherr.datex4j.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.juherr.datex4j.model.v3_7.locationreferencing.PointCoordinates;
import org.junit.jupiter.api.Test;

class LocationsTest {

    @Test
    void buildsPointCoordinatesFromDoubles() {
        PointCoordinates coordinates = Locations.pointCoordinates(51.5074, -0.1278);

        // Values are narrowed to the DATEX II float representation.
        assertEquals((float) 51.5074, coordinates.getLatitude());
        assertEquals((float) -0.1278, coordinates.getLongitude());
    }

    @Test
    void rejectsOutOfRangeLatitude() {
        assertThrows(IllegalArgumentException.class, () -> Locations.pointCoordinates(90.5, 0.0));
    }

    @Test
    void rejectsOutOfRangeLongitude() {
        assertThrows(IllegalArgumentException.class, () -> Locations.pointCoordinates(0.0, 200.0));
    }

    @Test
    void rejectsNonFiniteValues() {
        assertThrows(IllegalArgumentException.class, () -> Locations.pointCoordinates(Double.NaN, 0.0));
    }
}
