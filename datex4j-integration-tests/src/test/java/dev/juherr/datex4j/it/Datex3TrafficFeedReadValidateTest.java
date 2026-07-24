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
import dev.juherr.datex4j.model.v3_7.controlledzone.ControlledZoneTablePublication;
import dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
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
 * Offline coverage for real, anonymously accessible DATEX II v3.x <em>traffic</em> feeds (as opposed
 * to the AFIR recharging feeds covered elsewhere). Each fixture is a small, redistributable-licence
 * (CC0 / Licence Ouverte 2.0) trim of a live national feed, committed under {@code
 * src/test/resources/datasets/<country>/}; see the per-country {@code README.md} for source URLs,
 * licences, DATEX versions and trimming notes.
 *
 * <p>For every feed the test proves the {@code datex4j-xml} codec <strong>reads</strong> the feed
 * into the expected publication type and that real content survives (a stable identifier from the
 * source reappears when the parsed model is re-serialized), then runs the bundled XSD validator and
 * <strong>reports</strong> the outcome (valid / error-count / first errors) through {@link
 * TestReporter}. It deliberately asserts only that the parse succeeds — real feeds routinely carry
 * producer data-quality errors, national extensions (dropped on the lax read) or cross-minor drift,
 * so XSD validity is reported honestly rather than asserted.
 *
 * <p>Two honest, verified caveats are baked into the fixtures below:
 *
 * <ul>
 *   <li><strong>Feed minor version.</strong> The NDW feeds are Exchange-2020 {@code
 *       mc:messageContainer} documents read into the v3.7 model; FR DiaLog is a bare {@code
 *       d2:payload} authored against an older DATEX II minor (it uses {@code
 *       xsi:type="ValidityCondition"}, a {@code TrafficRegulation} {@code Condition} subtype that
 *       exists only in v3.2/v3.3 and was removed in v3.4+), so it is read and validated against
 *       <strong>v3.3</strong>. It does not read into v3.7 at all.
 *   <li><strong>messageContainer validation.</strong> The bundled validator's root schema is {@code
 *       DATEXII_3_D2Payload.xsd}, which declares the {@code d2:payload} root but not the Exchange
 *       {@code mc:messageContainer} root; container feeds therefore always report at least a {@code
 *       cvc-elt.1.a} "element mc:messageContainer not declared" error even when their payload is
 *       otherwise sound. This is reported, not asserted.
 * </ul>
 */
class Datex3TrafficFeedReadValidateTest {

    /** How the fixture's DATEX II payload is rooted on the wire. */
    private enum Root {
        /** Exchange-2020 {@code mc:messageContainer} read into a v3.7 {@link MessageContainer}. */
        MESSAGE_CONTAINER,
        /** Bare {@code d2:payload} read directly into the publication type. */
        BARE_PAYLOAD
    }

    /**
     * One committed DATEX II v3.x traffic feed.
     *
     * @param country dataset directory under {@code /datasets}
     * @param name short feed name (also the parameterized test label)
     * @param resourcePath classpath location of the committed fixture
     * @param root how the payload is rooted on the wire
     * @param version the DATEX II minor the feed is read and validated against
     * @param publicationType the expected publication type carried by the feed
     * @param contentToken a verbatim, standard-field substring from the real feed that must survive
     *     the read and reappear in the re-serialized publication
     */
    private record Feed(
            String country,
            String name,
            String resourcePath,
            Root root,
            DatexVersion version,
            Class<?> publicationType,
            String contentToken) {
        @Override
        public String toString() {
            return country + "/" + name;
        }
    }

    static List<Feed> feeds() {
        return List.of(
                // NDW situations (CC0): mc:messageContainer -> SituationPublication, v3.7.
                new Feed(
                        "netherlands",
                        "situations",
                        "/datasets/netherlands/situations.xml",
                        Root.MESSAGE_CONTAINER,
                        DatexVersion.V3_7,
                        SituationPublication.class,
                        "RWS01_SM1162215_D2_WWA"),
                // NDW SRTI safety-related messages (CC0): mc:messageContainer -> SituationPublication, v3.7.
                new Feed(
                        "netherlands",
                        "srti",
                        "/datasets/netherlands/srti.xml",
                        Root.MESSAGE_CONTAINER,
                        DatexVersion.V3_7,
                        SituationPublication.class,
                        "NDW08_9d8afcc1-fd11-44f5-9653-0efae246856a_SIT"),
                // NDW emission zones / UVAR (CC0): mc:messageContainer -> ControlledZoneTablePublication,
                // v3.7. NOTE: the feed's zone records use <cz:urbanVehicleAccessRegulation>, an element
                // absent from every bundled v3.x schema, so the zones are dropped on the lax read and only
                // the table envelope survives. The asserted token is the (surviving) tableVersionTime.
                new Feed(
                        "netherlands",
                        "emission-zones",
                        "/datasets/netherlands/emission-zones.xml",
                        Root.MESSAGE_CONTAINER,
                        DatexVersion.V3_7,
                        ControlledZoneTablePublication.class,
                        "2026-07-23T06:00:00.771432340Z"),
                // FR DiaLog (Licence Ouverte 2.0): bare d2:payload -> TrafficRegulationPublication, v3.3
                // (uses xsi:type="ValidityCondition", removed after v3.3).
                new Feed(
                        "france",
                        "dialog-regulations",
                        "/datasets/france/dialog-regulations.xml",
                        Root.BARE_PAYLOAD,
                        DatexVersion.V3_3,
                        dev.juherr.datex4j.model.v3_3.trafficregulation.TrafficRegulationPublication.class,
                        "018a45df-58c3-740c-b712-37d3d2ca25f8"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("feeds")
    void readsAndReportsValidation(Feed feed, TestReporter reporter) throws Exception {
        byte[] bytes = readResource(feed.resourcePath());
        DatexMarshaller marshaller = DatexXml.builder().version(feed.version()).build();

        Object publication = read(marshaller, feed, bytes);

        assertThat(publication)
                .as("parsed publication for %s", feed)
                .isNotNull()
                .isInstanceOf(feed.publicationType());

        // Real content survives: re-serialize the parsed publication and assert the stable, standard
        // -field token from the source reappears (national extensions are dropped on the lax read).
        String serialized = new String(marshaller.write(publication), StandardCharsets.UTF_8);
        assertThat(serialized)
                .as("real content missing after read for %s", feed)
                .contains(feed.contentToken());

        // Validation is reported, not asserted: real feeds carry producer errors / extensions, and
        // messageContainer roots are not declared by the bundled payload-rooted validation schema.
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
    }

    private static Object read(DatexMarshaller marshaller, Feed feed, byte[] bytes) {
        if (feed.root() == Root.BARE_PAYLOAD) {
            return marshaller.read(bytes, feed.publicationType());
        }
        MessageContainer container = marshaller.read(bytes, MessageContainer.class);
        assertThat(container.getPayload()).as("payload of %s", feed).isNotEmpty();
        return container.getPayload().getFirst();
    }

    private static String firstErrors(ValidationResult validation) {
        return validation.errors().stream()
                .limit(3)
                .map(ValidationMessage::message)
                .reduce((a, b) -> a + " | " + b)
                .orElse("(none)");
    }

    private static byte[] readResource(String path) throws Exception {
        try (InputStream in = Datex3TrafficFeedReadValidateTest.class.getResourceAsStream(path)) {
            assertThat(in).as("committed fixture on classpath: %s", path).isNotNull();
            return in.readAllBytes();
        }
    }
}
