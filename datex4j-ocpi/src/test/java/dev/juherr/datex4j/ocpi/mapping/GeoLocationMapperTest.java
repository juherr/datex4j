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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.juherr.datex4j.model.v3_7.locationreferencing.PointLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import org.junit.jupiter.api.Test;

class GeoLocationMapperTest {

    private final GeoLocationMapper mapper = new GeoLocationMapper();

    @Test
    void toDatexBuildsPointLocationFromDecimalStrings() {
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("51.5074");
        geo.setLongitude("-0.1278");

        PointLocation location = mapper.toDatex(geo);

        assertThat(location.getPointByCoordinates().getPointCoordinates().getLatitude())
                .isCloseTo(51.5074f, within(1e-4f));
        assertThat(location.getPointByCoordinates().getPointCoordinates().getLongitude())
                .isCloseTo(-0.1278f, within(1e-4f));
    }

    @Test
    void toOcpiReadsBackCoordinates() {
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("51.5074");
        geo.setLongitude("-0.1278");

        GeoLocation roundTrip = mapper.toOcpi(mapper.toDatex(geo));

        assertThat(Double.parseDouble(roundTrip.getLatitude())).isCloseTo(51.5074, within(1e-4));
        assertThat(Double.parseDouble(roundTrip.getLongitude())).isCloseTo(-0.1278, within(1e-4));
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
