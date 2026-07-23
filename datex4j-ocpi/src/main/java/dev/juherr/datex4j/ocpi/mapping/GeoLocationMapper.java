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
package dev.juherr.datex4j.ocpi.mapping;

import dev.juherr.datex4j.location.Locations;
import dev.juherr.datex4j.model.v3_7.locationreferencing.LocationReference;
import dev.juherr.datex4j.model.v3_7.locationreferencing.PointByCoordinates;
import dev.juherr.datex4j.model.v3_7.locationreferencing.PointCoordinates;
import dev.juherr.datex4j.model.v3_7.locationreferencing.PointLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;

/**
 * Maps OCPI {@link GeoLocation} (decimal-degree strings) to a DATEX II {@link PointLocation} and
 * back. OCPI stores coordinates as strings; DATEX II uses {@code float}, so a round-trip is precise
 * to roughly 6-7 significant digits (see {@link Locations}).
 *
 * <p><b>Unmapped fields.</b> OCPI has no bearing; DATEX II bearing and any non-point location
 * reference are dropped when converting to OCPI.
 */
public final class GeoLocationMapper {

    /** Builds a point location, or {@code null} if {@code geo} is null. */
    public PointLocation toDatex(GeoLocation geo) {
        if (geo == null) {
            return null;
        }
        PointCoordinates coordinates = Locations.pointCoordinates(
                Double.parseDouble(geo.getLatitude()), Double.parseDouble(geo.getLongitude()));
        PointByCoordinates point = new PointByCoordinates();
        point.setPointCoordinates(coordinates);
        PointLocation location = new PointLocation();
        location.setPointByCoordinates(point);
        return location;
    }

    /** Reads coordinates back, or {@code null} if not a point location with coordinates. */
    public GeoLocation toOcpi(LocationReference reference) {
        if (!(reference instanceof PointLocation location) || location.getPointByCoordinates() == null) {
            return null;
        }
        PointCoordinates coordinates = location.getPointByCoordinates().getPointCoordinates();
        if (coordinates == null) {
            return null;
        }
        GeoLocation geo = new GeoLocation();
        geo.setLatitude(Float.toString(coordinates.getLatitude()));
        geo.setLongitude(Float.toString(coordinates.getLongitude()));
        return geo;
    }
}
