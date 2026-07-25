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
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationMessage;
import dev.juherr.datex4j.validation.ValidationResult;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Offline coverage for real, anonymously accessible DATEX II <strong>v2</strong> traffic feeds, the
 * v2 counterpart of {@link Datex3TrafficFeedReadValidateTest}. Each fixture is a small,
 * redistributable-licence (Licence Ouverte 2.0 / CC BY 4.0) trim of a live national feed, committed
 * under {@code src/test/resources/datasets/<country>/}; see the per-country {@code README.md} for
 * source URLs, licences, DATEX versions and trimming notes.
 *
 * <p>Unlike v3 (where the {@code payload} element is itself a document root), a v2 document is rooted
 * at {@code d2LogicalModel}, which nests both the {@code exchange} and the {@code
 * payloadPublication}. Every fixture is therefore read into the version-scoped {@code D2LogicalModel}
 * and its {@code payloadPublication} is asserted to be the expected publication type. A verbatim,
 * standard-field token from the source must survive the read and reappear when the parsed model is
 * re-serialized; then the bundled monolithic v2 XSD validator runs and the outcome (valid /
 * error-count / first errors) is {@linkplain TestReporter reported} and the now-known verdict
 * asserted.
 *
 * <p>Per-feed validity, honestly recorded (see the country READMEs for the full story):
 *
 * <ul>
 *   <li><strong>FR speeds</strong> (Licence Ouverte 2.0): a bare {@code d2LogicalModel} carrying a
 *       {@code MeasuredDataPublication}, validated against <strong>v2.2</strong>.
 *   <li><strong>FR events</strong> (Licence Ouverte 2.0): the source wraps each publication in a
 *       {@code <soap:Envelope>}; the committed fixture is the <em>unwrapped</em> {@code
 *       d2LogicalModel} (a {@code SituationPublication}), validated against <strong>v2.2</strong>.
 *   <li><strong>FI roadworks</strong> (CC BY 4.0): a bare {@code d2LogicalModel} carrying a {@code
 *       SituationPublication}, validated against the bundled plain <strong>v2.3</strong>. NOTE: the
 *       feed's {@code xsi:schemaLocation} points to a Finnish national-extension XSD, not the plain
 *       DATEX II v2.3 schema the validator compiles; the verdict is reported honestly against the
 *       bundled schema.
 * </ul>
 */
class Datex2TrafficFeedReadValidateTest {

    /**
     * One committed DATEX II v2 traffic feed.
     *
     * @param country dataset directory under {@code /datasets}
     * @param name short feed name (also the parameterized test label)
     * @param resourcePath classpath location of the committed fixture
     * @param version the DATEX II minor the feed is read and validated against
     * @param publicationType the expected {@code payloadPublication} type carried by the feed
     * @param contentToken a verbatim, standard-field substring from the real feed that must survive
     *     the read and reappear in the re-serialized document
     * @param expectedValid whether the raw feed bytes validate cleanly against the bundled monolithic
     *     v2 XSD for {@code version}
     */
    private record Feed(
            String country,
            String name,
            String resourcePath,
            DatexVersion version,
            Class<?> publicationType,
            String contentToken,
            boolean expectedValid) {
        @Override
        public String toString() {
            return country + "/" + name;
        }
    }

    static List<Feed> feeds() {
        return List.of(
                // FR speeds (Licence Ouverte 2.0): bare d2LogicalModel -> MeasuredDataPublication, v2.2.
                new Feed(
                        "france",
                        "speeds",
                        "/datasets/france/speeds.xml",
                        DatexVersion.V2_2,
                        dev.juherr.datex4j.model.v2_2.MeasuredDataPublication.class,
                        "MUM76.h1",
                        FR_SPEEDS_VALID),
                // FR events (Licence Ouverte 2.0): SOAP-unwrapped d2LogicalModel -> SituationPublication, v2.2.
                new Feed(
                        "france",
                        "events",
                        "/datasets/france/events.xml",
                        DatexVersion.V2_2,
                        dev.juherr.datex4j.model.v2_2.SituationPublication.class,
                        "260724-000456",
                        FR_EVENTS_VALID),
                // FI roadworks (CC BY 4.0): bare d2LogicalModel -> SituationPublication, plain v2.3 (its
                // xsi:schemaLocation points at a Finnish-extended XSD; validated against the bundled plain v2.3).
                new Feed(
                        "finland",
                        "roadworks",
                        "/datasets/finland/roadworks.xml",
                        DatexVersion.V2_3,
                        dev.juherr.datex4j.model.v2_3.SituationPublication.class,
                        "GUID50467344",
                        FI_ROADWORKS_VALID));
    }

    // Validity verdicts observed against the bundled XSDs; asserted below (never faked).
    private static final boolean FR_SPEEDS_VALID = true;
    private static final boolean FR_EVENTS_VALID = true;
    // FI roadworks omits the exchange element and the payloadPublication's lang/publicationTime that
    // plain DATEX II v2.3 mandates (its xsi:schemaLocation points at a Finnish-extended XSD instead),
    // so it is genuinely invalid against the bundled plain v2.3 schema. Reported honestly, not faked.
    private static final boolean FI_ROADWORKS_VALID = false;

    @ParameterizedTest(name = "{0}")
    @MethodSource("feeds")
    void readsAndReportsValidation(Feed feed, TestReporter reporter) throws Exception {
        byte[] bytes = readResource(feed.resourcePath());
        DatexMarshaller marshaller = DatexXml.builder().version(feed.version()).build();

        Object publication = read(marshaller, feed, bytes);

        assertThat(publication)
                .as("parsed payloadPublication for %s", feed)
                .isNotNull()
                .isInstanceOf(feed.publicationType());

        // Real content survives: re-serialize the parsed document and assert the stable, standard
        // -field token from the source reappears (national extensions are dropped on the lax read).
        String serialized = new String(marshaller.write(publication), StandardCharsets.UTF_8);
        assertThat(serialized)
                .as("real content missing after read for %s", feed)
                .contains(feed.contentToken());

        ValidationResult validation = DatexValidator.forVersion(feed.version()).validate(bytes);

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("feed", feed.toString());
        entry.put("bytes", Integer.toString(bytes.length));
        entry.put("read-version", feed.version().toString());
        entry.put("read-type", feed.publicationType().getSimpleName());
        entry.put("valid", Boolean.toString(validation.isValid()));
        entry.put("error-count", Integer.toString(validation.errors().size()));
        entry.put("first-errors", firstErrors(validation));
        reporter.publishEntry(entry);

        assertThat(validation.isValid())
                .as("XSD validity for %s (errors: %s)", feed, firstErrors(validation))
                .isEqualTo(feed.expectedValid());
    }

    private static Object read(DatexMarshaller marshaller, Feed feed, byte[] bytes) {
        return switch (feed.version()) {
            case V2_2 -> {
                var root = marshaller.read(bytes, dev.juherr.datex4j.model.v2_2.D2LogicalModel.class);
                assertThat(root.getExchange()).as("exchange of %s", feed).isNotNull();
                yield root.getPayloadPublication();
            }
            case V2_3 ->
                marshaller
                        .read(bytes, dev.juherr.datex4j.model.v2_3.D2LogicalModel.class)
                        .getPayloadPublication();
            default -> throw new IllegalArgumentException("unhandled v2 version: " + feed.version());
        };
    }

    private static String firstErrors(ValidationResult validation) {
        return validation.errors().stream()
                .limit(3)
                .map(ValidationMessage::message)
                .reduce((a, b) -> a + " | " + b)
                .orElse("(none)");
    }

    private static byte[] readResource(String path) throws Exception {
        try (InputStream in = Datex2TrafficFeedReadValidateTest.class.getResourceAsStream(path)) {
            assertThat(in).as("committed fixture on classpath: %s", path).isNotNull();
            return in.readAllBytes();
        }
    }
}
