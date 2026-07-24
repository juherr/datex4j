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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.json.DatexJson;
import dev.juherr.datex4j.model.v3_6.common.PayloadPublication;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.EnergyInfrastructureTable;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Opt-in whole-feed read test: parses the <em>complete</em> Fintraffic AFIR feed (~13 MB, thousands
 * of sites) into a {@link MessageContainer} and proves nothing is silently dropped by comparing the
 * number of {@code energyInfrastructureSite} entries the codec parsed against the number present in
 * the raw JSON.
 *
 * <p>The full feed is deliberately <strong>not committed</strong> (too large, and the suite must
 * stay offline and reproducible). This test therefore runs <strong>only</strong> when the system
 * property {@value #FULL_FEED_PROPERTY} points to a locally downloaded copy, and is skipped
 * otherwise. See the Finland dataset README for how to download the feed and run this test.
 */
class FinlandFullFeedReadTest {

    private static final String FULL_FEED_PROPERTY = "datex4j.it.finland.full";

    @Test
    @EnabledIfSystemProperty(
            named = FULL_FEED_PROPERTY,
            matches = ".+",
            disabledReason = "Set -D" + FULL_FEED_PROPERTY + "=<path> to a downloaded full feed to run this test")
    void readsEntireFinlandFeed(TestReporter reporter) throws Exception {
        Path file = Path.of(System.getProperty(FULL_FEED_PROPERTY));
        byte[] bytes = Files.readAllBytes(file);

        long rawSites = countArrayElements(new ObjectMapper().readTree(bytes), "energyInfrastructureSite");

        MessageContainer container =
                DatexJson.builder().version(DatexVersion.V3_6).build().readContainer(bytes, MessageContainer.class);

        assertThat(container).as("parsed container from %s", file).isNotNull();
        assertThat(container.getPayload())
                .as("payload of the full Finland feed")
                .isNotEmpty();

        long tables = 0;
        long parsedSites = 0;
        long stations = 0;
        for (PayloadPublication publication : container.getPayload()) {
            if (publication instanceof EnergyInfrastructureTablePublication pub) {
                for (EnergyInfrastructureTable table : pub.getEnergyInfrastructureTable()) {
                    tables++;
                    parsedSites += table.getEnergyInfrastructureSite().size();
                    for (var site : table.getEnergyInfrastructureSite()) {
                        stations += site.getEnergyInfrastructureStation().size();
                    }
                }
            }
        }

        reporter.publishEntry(Map.of(
                "file", file.toString(),
                "bytes", Long.toString(bytes.length),
                "publications", Integer.toString(container.getPayload().size()),
                "energyInfrastructureTable", Long.toString(tables),
                "energyInfrastructureSite (raw)", Long.toString(rawSites),
                "energyInfrastructureSite (parsed)", Long.toString(parsedSites),
                "energyInfrastructureStation", Long.toString(stations)));

        assertThat(parsedSites)
                .as("the full feed must contain at least one site to be a meaningful read test")
                .isPositive();
        assertThat(parsedSites)
                .as("codec dropped sites while reading %s: parsed %d of %d raw entries", file, parsedSites, rawSites)
                .isEqualTo(rawSites);
    }

    /** Recursively sums the sizes of every array reached through a field named {@code fieldName}. */
    private static long countArrayElements(JsonNode node, String fieldName) {
        long count = 0;
        if (node.isObject()) {
            for (Map.Entry<String, JsonNode> field : node.properties()) {
                if (field.getKey().equals(fieldName) && field.getValue().isArray()) {
                    count += field.getValue().size();
                }
                count += countArrayElements(field.getValue(), fieldName);
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                count += countArrayElements(child, fieldName);
            }
        }
        return count;
    }
}
