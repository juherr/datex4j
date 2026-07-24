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
package dev.juherr.datex4j.it.support;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import java.util.List;

/** The explicit registry of committed dataset fixtures exercised by the round-trip suite. */
public final class DatasetCatalog {

    private DatasetCatalog() {}

    public static List<Dataset> all() {
        return List.of(
                new Dataset(
                        "afir-recharging-minimal",
                        "synthetic",
                        Dataset.Format.XML,
                        DatexVersion.V3_7,
                        "/datasets/synthetic/afir-recharging/table.xml",
                        EnergyInfrastructureTablePublication.class),
                // Real-world Fintraffic AFIR feed (CC BY 4.0): conformant DATEX II JSON, v3.6,
                // rooted at a MessageContainer. Trimmed to one site; see the dataset README.
                new Dataset(
                        "afir-fintraffic",
                        "finland",
                        Dataset.Format.JSON,
                        DatexVersion.V3_6,
                        "/datasets/finland/afir-messagecontainer.v3_6.json",
                        MessageContainer.class));
        // Register committed official/country datasets here as they are added.
    }
}
