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
 *
 * <p><strong>Coordinate precision.</strong> DATEX II defines {@code latitude}/{@code longitude} as
 * 32-bit {@code xs:float}, so the model — and hence the XML on the wire — cannot represent more than
 * roughly 6–7 significant digits (about 1–3&nbsp;m of horizontal resolution, worst near ±180°). The
 * helpers below accept {@code double} for a natural call site but narrow to {@code float} when
 * populating the model; that narrowing, not the helper, is the source of any precision loss. datex4j
 * deliberately does not depend on a GIS library: for geometry, geodesic distance or CRS
 * transformations, use a dedicated library (for example
 * <a href="https://locationtech.github.io/jts/">JTS</a> for geometry, or GeographicLib / Apache SIS
 * for geodesy and referencing) in your application and convert at the DATEX boundary.
 */
public final class Locations {

    private Locations() {}

    /**
     * Builds WGS-84 {@code PointCoordinates} from a latitude and longitude in decimal degrees.
     *
     * <p>Values are narrowed to 32-bit {@code float} because that is the DATEX II representation; see
     * the {@linkplain Locations class documentation} on precision.
     *
     * @param latitude the latitude in decimal degrees, within {@code [-90, 90]}
     * @param longitude the longitude in decimal degrees, within {@code [-180, 180]}
     * @return the point coordinates
     * @throws IllegalArgumentException if a value is not finite or is outside its valid range
     */
    public static PointCoordinates pointCoordinates(double latitude, double longitude) {
        checkRange("latitude", latitude, 90.0);
        checkRange("longitude", longitude, 180.0);
        PointCoordinates coordinates = new PointCoordinates();
        coordinates.setLatitude((float) latitude);
        coordinates.setLongitude((float) longitude);
        return coordinates;
    }

    private static void checkRange(String name, double value, double bound) {
        if (!Double.isFinite(value) || value < -bound || value > bound) {
            throw new IllegalArgumentException(
                    name + " must be a finite value within [-" + bound + ", " + bound + "]: " + value);
        }
    }
}
