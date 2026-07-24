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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juherr.datex4j.json.DatexJson;
import dev.juherr.datex4j.json.DatexJsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Runs the conformant DATEX II JSON checks for one dataset served as JSON (the shape real National
 * Access Points such as Fintraffic publish): well-formed JSON, parse into the expected {@code
 * MessageContainer} root, and a re-encode round-trip that must be idempotent and preserve the
 * fixture's real content.
 *
 * <p>The mandatory XSD/XML checks in {@link XmlRoundTrip} do not apply here: the source is JSON,
 * there is no XSD for the JSON encoding, and the publisher serves no XML.
 */
public final class JsonRoundTrip {

    private final ObjectMapper wellFormed = new ObjectMapper();

    /**
     * Verifies the JSON dataset. Tokens that must survive the round trip prove the fixture carries
     * real, non-empty content rather than an empty envelope.
     *
     * @param dataset the JSON dataset to verify
     * @param contentTokens verbatim substrings from the real feed that must appear in the re-encoded
     *     JSON (for example an operator id and a place name)
     * @throws Exception if a resource cannot be read
     */
    public void verify(Dataset dataset, String... contentTokens) throws Exception {
        byte[] original = readResource(dataset.resourcePath());

        // 1. well-formed JSON
        assertWellFormed(original, dataset);

        // 2. parse into the expected MessageContainer root
        DatexJsonMapper mapper = DatexJson.builder().version(dataset.version()).build();
        Object container = mapper.readContainer(original, dataset.rootType());
        assertThat(container).as("parsed container for %s", dataset).isNotNull();
        assertThat(container).as("container type for %s", dataset).isInstanceOf(dataset.rootType());

        // 3. re-encode, re-parse, re-encode: the codec must be idempotent
        byte[] encoded = mapper.write(container);
        Object reparsed = mapper.readContainer(encoded, dataset.rootType());
        byte[] reencoded = mapper.write(reparsed);
        String encodedText = new String(encoded, StandardCharsets.UTF_8);
        assertThat(new String(reencoded, StandardCharsets.UTF_8))
                .as("JSON round-trip is not idempotent for %s", dataset)
                .isEqualTo(encodedText);

        // 4. real content survives the round trip (parsed from the model, not passed through)
        assertThat(encodedText)
                .as("real content missing after round-trip for %s", dataset)
                .contains(contentTokens);
    }

    private void assertWellFormed(byte[] json, Dataset dataset) {
        try {
            wellFormed.readTree(json);
        } catch (IOException e) {
            throw new AssertionError("Not well-formed JSON: " + dataset, e);
        }
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream in = JsonRoundTrip.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Dataset resource not found on classpath: " + path);
            }
            return in.readAllBytes();
        }
    }
}
