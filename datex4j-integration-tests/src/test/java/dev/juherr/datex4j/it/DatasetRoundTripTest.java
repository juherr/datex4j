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
package dev.juherr.datex4j.it;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.it.support.Dataset;
import dev.juherr.datex4j.it.support.DatasetCatalog;
import dev.juherr.datex4j.it.support.JsonRoundTrip;
import dev.juherr.datex4j.it.support.XmlRoundTrip;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DatasetRoundTripTest {

    private final XmlRoundTrip xmlRoundTrip = new XmlRoundTrip();
    private final JsonRoundTrip jsonRoundTrip = new JsonRoundTrip();

    static List<Dataset> datasets() {
        return DatasetCatalog.all();
    }

    @Test
    void catalogIsNotEmpty() {
        assertThat(DatasetCatalog.all()).isNotEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("datasets")
    void datasetRoundTrips(Dataset dataset) throws Exception {
        switch (dataset.format()) {
            case XML -> xmlRoundTrip.verify(dataset);
            // Real Fintraffic content that survives the (lossy) conformant round-trip: the
            // operator identity, the real site name, and a real connector type. The street
            // address/city is intentionally not asserted here: it lives under a FacilityLocation,
            // which the codec drops (documented in the dataset README).
            case JSON -> jsonRoundTrip.verify(dataset, "FI*911", "Porsche Destination Charging", "iec62196T2");
        }
    }
}
