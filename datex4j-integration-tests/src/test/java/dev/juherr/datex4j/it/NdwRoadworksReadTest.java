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

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.v3_7.common.PayloadPublication;
import dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationResult;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Opt-in read test for the real NDW roadworks/events planning feed: a DATEX II <strong>v3</strong>
 * Exchange-2020 {@code mc:messageContainer} carrying a large {@code SituationPublication} (~171 MB
 * uncompressed, ~14 000 situations). It proves {@code datex4j-xml} reads the whole feed into the
 * v3.7 {@link MessageContainer} model and that the vendored v3.7 schemas run against it, reporting
 * the outcome.
 *
 * <p>The feed is deliberately <strong>not committed</strong> (far too large, and the suite must stay
 * offline and reproducible; it also needs gunzip to fetch). This test therefore runs
 * <strong>only</strong> when {@value #FEED_PROPERTY} points to a locally downloaded, decompressed
 * copy, and is skipped otherwise. Because the file is large, run it with a generous heap.
 *
 * <p>Download it (CC0) and run with:
 *
 * <pre>{@code
 * curl -s http://opendata.ndw.nu/planningsfeed_wegwerkzaamheden_en_evenementen.xml.gz \
 *   | gunzip > /tmp/ndw-roadworks.xml
 * MAVEN_OPTS=-Xmx6g ./mvnw -pl datex4j-integration-tests test \
 *   -Dtest=NdwRoadworksReadTest -Ddatex4j.it.ndw.roadworks=/tmp/ndw-roadworks.xml
 * }</pre>
 *
 * <p>Observed against the live feed (2026-07-24): reads <strong>13 793 situations</strong> in one
 * {@code SituationPublication}; v3.7 validation reports it invalid with ~10 766 errors, dominated by
 * the {@code mc:messageContainer} root not being declared by the payload-rooted validation schema
 * and by NDW's {@code sit:roadworksExtension} national-extension elements. These are container-root
 * and national-extension issues, not a vendoring defect, so the test reports the count rather than
 * asserting validity.
 */
class NdwRoadworksReadTest {

    private static final String FEED_PROPERTY = "datex4j.it.ndw.roadworks";

    @Test
    @EnabledIfSystemProperty(
            named = FEED_PROPERTY,
            matches = ".+",
            disabledReason = "Set -D" + FEED_PROPERTY + "=<path> to a downloaded, gunzipped roadworks feed")
    void readsNdwRoadworksMessageContainer(TestReporter reporter) throws Exception {
        Path file = Path.of(System.getProperty(FEED_PROPERTY));
        byte[] bytes = Files.readAllBytes(file);

        MessageContainer container =
                DatexXml.builder().version(DatexVersion.V3_7).build().read(bytes, MessageContainer.class);

        assertThat(container).as("parsed container from %s", file).isNotNull();
        assertThat(container.getPayload()).as("payload of %s", file).isNotEmpty();

        long situations = 0;
        for (PayloadPublication publication : container.getPayload()) {
            if (publication instanceof SituationPublication sp) {
                situations += sp.getSituation().size();
            }
        }

        ValidationResult validation =
                DatexValidator.forVersion(DatexVersion.V3_7).validate(bytes);

        reporter.publishEntry(Map.of(
                "file", file.toString(),
                "bytes", Long.toString(bytes.length),
                "publications", Integer.toString(container.getPayload().size()),
                "situations", Long.toString(situations),
                "v3.7-valid", Boolean.toString(validation.isValid()),
                "v3.7-errors", Integer.toString(validation.errors().size())));

        assertThat(situations)
                .as("the roadworks feed must contain at least one situation to be a meaningful read test")
                .isPositive();
    }
}
