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

import dev.juherr.datex4j.model.v3_7.locationreferencing.PointCoordinates;

/**
 * Small helpers for DATEX II location referencing, a concern shared by every user domain.
 *
 * <p>This module is intentionally cross-cutting rather than domain-specific: location referencing is
 * reused by traffic, parking, EV charging and the other domains. Targets the current DATEX II
 * version.
 */
public final class Locations {

    private Locations() {}

    /**
     * Builds WGS-84 {@code PointCoordinates} from a latitude and longitude.
     *
     * @param latitude the latitude in decimal degrees
     * @param longitude the longitude in decimal degrees
     * @return the point coordinates
     */
    public static PointCoordinates pointCoordinates(float latitude, float longitude) {
        PointCoordinates coordinates = new PointCoordinates();
        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);
        return coordinates;
    }
}
