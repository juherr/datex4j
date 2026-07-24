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

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Opt-in read test for the real Fintraffic roadworks traffic feed: a DATEX II <strong>v3.5</strong>
 * {@code SituationPublication} (~7.5 MB, hundreds of situations). It proves {@code datex4j-xml}
 * reads a real v3.5 bare payload into the v3.7 model even though the bundled model targets
 * v3.6/v3.7 — the DATEX II v3 payloads are wire-compatible across these minor versions.
 *
 * <p>The feed is deliberately <strong>not committed</strong> (too large, and the suite must stay
 * offline and reproducible; it also requires a {@code Digitraffic-User} header and gzip to fetch).
 * This test therefore runs <strong>only</strong> when the system property {@value #FEED_PROPERTY}
 * points to a locally downloaded copy, and is skipped otherwise.
 *
 * <p>Download it (CC BY 4.0) with:
 *
 * <pre>{@code
 * curl -s --compressed -H 'Digitraffic-User: datex4j-test' \
 *   https://tie.digitraffic.fi/api/traffic-message/v2/roadworks/datex2-3.5.xml -o /tmp/fi-roadworks.xml
 * ./mvnw -pl datex4j-integration-tests test \
 *   -Dtest=FintrafficTrafficReadTest -Ddatex4j.it.fintraffic.roadworks=/tmp/fi-roadworks.xml
 * }</pre>
 */
class FintrafficTrafficReadTest {

    private static final String FEED_PROPERTY = "datex4j.it.fintraffic.roadworks";

    @Test
    @EnabledIfSystemProperty(
            named = FEED_PROPERTY,
            matches = ".+",
            disabledReason = "Set -D" + FEED_PROPERTY + "=<path> to a downloaded v3.5 roadworks feed to run this test")
    void readsFintrafficV35SituationPublication(TestReporter reporter) throws Exception {
        Path file = Path.of(System.getProperty(FEED_PROPERTY));
        byte[] bytes = Files.readAllBytes(file);

        SituationPublication publication = DatexXml.createMarshaller().read(bytes, SituationPublication.class);

        assertThat(publication).as("parsed SituationPublication from %s", file).isNotNull();
        assertThat(publication).as("model type for %s", file).isInstanceOf(SituationPublication.class);

        int situations = publication.getSituation().size();

        reporter.publishEntry(Map.of(
                "file", file.toString(),
                "bytes", Long.toString(bytes.length),
                "situation", Integer.toString(situations)));

        assertThat(situations)
                .as("the v3.5 feed must contain at least one situation to be a meaningful read test")
                .isPositive();
    }
}
