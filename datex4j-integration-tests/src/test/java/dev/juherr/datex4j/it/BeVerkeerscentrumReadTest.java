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
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationMessage;
import dev.juherr.datex4j.validation.ValidationResult;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Opt-in read test for the Flemish "Verkeerscentrum" DATEX II v3 exchange feed
 * (<a href="https://www.verkeerscentrum.be/uitwisseling/datex2v3full">datex2v3full</a>).
 *
 * <p>The feed's licence is <strong>not restated</strong> on the endpoint, so it is treated as
 * uncertain and <strong>never committed</strong>: this test runs only when {@value #FEED_PROPERTY}
 * points to a locally downloaded copy, and is skipped otherwise.
 *
 * <p><strong>Honest finding (verified 2026-07-24):</strong> despite the {@code datex2v3full} name,
 * the feed does <em>not</em> conform to the DATEX II payload model as bundled. Its root is a bare
 * {@code <d2:payload>} <em>without</em> an {@code xsi:type} (the payload type is abstract, so JAXB
 * cannot instantiate it), it places {@code <situation>} elements in the {@code d2Payload} namespace
 * rather than the {@code situation} namespace, and it mixes in a proprietary {@code
 * verkeerscentrum.be/tcc.backend} namespace. Consequently {@code datex4j-xml} <strong>cannot</strong>
 * read it into a {@code SituationPublication}, and v3.7 validation rejects it (the abstract payload
 * plus missing {@code lang}). This test asserts only that the file is well-formed XML and that the
 * codec/validator run, and <strong>reports</strong> the (failing) DATEX-conformance outcome so the
 * behaviour is pinned rather than hidden.
 *
 * <pre>{@code
 * curl -s https://www.verkeerscentrum.be/uitwisseling/datex2v3full -o /tmp/be-verkeer.xml
 * ./mvnw -pl datex4j-integration-tests test \
 *   -Dtest=BeVerkeerscentrumReadTest -Ddatex4j.it.be.verkeerscentrum=/tmp/be-verkeer.xml
 * }</pre>
 */
class BeVerkeerscentrumReadTest {

    private static final String FEED_PROPERTY = "datex4j.it.be.verkeerscentrum";

    @Test
    @EnabledIfSystemProperty(
            named = FEED_PROPERTY,
            matches = ".+",
            disabledReason = "Set -D" + FEED_PROPERTY + "=<path> to a downloaded Verkeerscentrum feed")
    void readsBeVerkeerscentrumFeed(TestReporter reporter) throws Exception {
        Path file = Path.of(System.getProperty(FEED_PROPERTY));
        byte[] bytes = Files.readAllBytes(file);

        assertThat(bytes).as("downloaded feed %s is non-empty", file).isNotEmpty();
        assertThatWellFormed(bytes, file);

        String datexRead;
        try {
            SituationPublication publication =
                    DatexXml.builder().version(DatexVersion.V3_7).build().read(bytes, SituationPublication.class);
            datexRead = "read-ok: situations=" + publication.getSituation().size();
        } catch (Exception e) {
            // Expected: the feed's bare, untyped <d2:payload> cannot be resolved to a concrete
            // publication. Report the reason rather than failing — the point is honest coverage.
            datexRead = "read-failed: " + rootReason(e);
        }

        ValidationResult validation =
                DatexValidator.forVersion(DatexVersion.V3_7).validate(bytes);

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("file", file.toString());
        entry.put("bytes", Long.toString(bytes.length));
        entry.put("datex-read", datexRead);
        entry.put("v3.7-valid", Boolean.toString(validation.isValid()));
        entry.put("v3.7-errors", Integer.toString(validation.errors().size()));
        entry.put(
                "first-errors",
                validation.errors().stream()
                        .limit(3)
                        .map(ValidationMessage::message)
                        .reduce((a, b) -> a + " | " + b)
                        .orElse("(none)"));
        reporter.publishEntry(entry);

        assertThat(validation.errors())
                .as("validation must have run and returned a result")
                .isNotNull();
    }

    private static void assertThatWellFormed(byte[] xml, Path file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        } catch (Exception e) {
            throw new AssertionError("Not well-formed XML: " + file, e);
        }
    }

    private static String rootReason(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getClass().getSimpleName() + (root.getMessage() == null ? "" : ": " + root.getMessage());
    }
}
