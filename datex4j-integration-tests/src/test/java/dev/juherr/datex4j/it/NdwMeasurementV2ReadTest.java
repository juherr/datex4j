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
import dev.juherr.datex4j.model.v2_0.D2LogicalModel;
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationMessage;
import dev.juherr.datex4j.validation.ValidationResult;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Opt-in coverage for the Dutch NDW <strong>DATEX II v2</strong> measurement feeds
 * (<a href="http://opendata.ndw.nu">opendata.ndw.nu</a>, CC0):
 * {@code measurement_current.xml.gz}, {@code trafficspeed.xml.gz} and {@code traveltime.xml.gz}.
 *
 * <p>These feeds are <strong>never committed</strong>: each is tens to hundreds of MB uncompressed
 * (e.g. {@code measurement_current} is ~12 MB gzipped, ~390 MB uncompressed) and, unlike the small
 * committed v2 fixtures, they are <strong>SOAP-wrapped</strong> — the DATEX II {@code d2LogicalModel}
 * is nested inside a {@code <SOAP:Envelope>}/{@code <SOAP:Body>}
 * ({@code http://schemas.xmlsoap.org/soap/envelope/}). datex4j reads a {@code d2LogicalModel} root,
 * <strong>not</strong> a SOAP envelope, so this test first {@linkplain #stripSoapEnvelope strips the
 * SOAP wrapper} (slicing out the inner {@code d2LogicalModel} element) and only then hands the bytes
 * to the codec.
 *
 * <p>The test is skipped unless {@code -Ddatex4j.it.ndw.v2measurement=<path>} points at a downloaded
 * copy (a {@code .gz} path is gunzipped transparently). It reads the payload into a v2.0 {@link
 * D2LogicalModel}, then {@linkplain TestReporter reports} the payload publication type and the v2.0
 * XSD validation outcome. Run it with a generous heap:
 *
 * <pre>{@code
 * curl -sL -o /tmp/ndw-measurement.xml.gz http://opendata.ndw.nu/measurement_current.xml.gz
 * MAVEN_OPTS=-Xmx6g ./mvnw -pl datex4j-integration-tests test \
 *   -Dtest=NdwMeasurementV2ReadTest \
 *   -Ddatex4j.it.ndw.v2measurement=/tmp/ndw-measurement.xml.gz
 * }</pre>
 */
@EnabledIfSystemProperty(
        named = "datex4j.it.ndw.v2measurement",
        matches = ".+",
        disabledReason = "set -Ddatex4j.it.ndw.v2measurement=<path to a downloaded NDW v2 feed (.xml or .xml.gz)>")
class NdwMeasurementV2ReadTest {

    private static final byte[] START = "<d2LogicalModel".getBytes(StandardCharsets.UTF_8);
    private static final byte[] END = "</d2LogicalModel>".getBytes(StandardCharsets.UTF_8);

    @Test
    void readsAndReportsValidation(TestReporter reporter) throws Exception {
        Path path = Path.of(System.getProperty("datex4j.it.ndw.v2measurement"));
        byte[] soap = readAllMaybeGzip(path);
        byte[] d2 = stripSoapEnvelope(soap);

        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_0).build();
        D2LogicalModel root = marshaller.read(d2, D2LogicalModel.class);

        assertThat(root).as("parsed d2LogicalModel from %s", path).isNotNull();
        assertThat(root.getPayloadPublication())
                .as("payloadPublication from %s", path)
                .isNotNull();

        ValidationResult validation =
                DatexValidator.forVersion(DatexVersion.V2_0).validate(d2);

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("source", path.toString());
        entry.put("soap-bytes", Integer.toString(soap.length));
        entry.put("d2-bytes", Integer.toString(d2.length));
        entry.put("payload-type", root.getPayloadPublication().getClass().getSimpleName());
        entry.put("valid", Boolean.toString(validation.isValid()));
        entry.put("error-count", Integer.toString(validation.errors().size()));
        entry.put("first-errors", firstErrors(validation));
        reporter.publishEntry(entry);
    }

    /**
     * Slices the inner {@code d2LogicalModel} element out of a SOAP envelope, returning a standalone
     * DATEX II document (with an XML declaration). datex4j has no SOAP layer; callers of NDW's v2
     * feeds must unwrap the envelope exactly like this before reading.
     */
    private static byte[] stripSoapEnvelope(byte[] soap) {
        int start = indexOf(soap, START, 0);
        assertThat(start)
                .as("no <d2LogicalModel start element found in SOAP body")
                .isGreaterThanOrEqualTo(0);
        int endMarker = lastIndexOf(soap, END);
        assertThat(endMarker)
                .as("no </d2LogicalModel> end element found in SOAP body")
                .isGreaterThanOrEqualTo(0);
        int end = endMarker + END.length;

        String declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        byte[] prefix = declaration.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[prefix.length + (end - start)];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        System.arraycopy(soap, start, out, prefix.length, end - start);
        return out;
    }

    private static byte[] readAllMaybeGzip(Path path) throws Exception {
        if (path.getFileName().toString().endsWith(".gz")) {
            try (InputStream in = new GZIPInputStream(Files.newInputStream(path))) {
                return in.readAllBytes();
            }
        }
        return Files.readAllBytes(path);
    }

    private static int indexOf(byte[] haystack, byte[] needle, int from) {
        outer:
        for (int i = Math.max(from, 0); i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static int lastIndexOf(byte[] haystack, byte[] needle) {
        outer:
        for (int i = haystack.length - needle.length; i >= 0; i--) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static String firstErrors(ValidationResult validation) {
        return validation.errors().stream()
                .limit(3)
                .map(ValidationMessage::message)
                .reduce((a, b) -> a + " | " + b)
                .orElse("(none)");
    }
}
