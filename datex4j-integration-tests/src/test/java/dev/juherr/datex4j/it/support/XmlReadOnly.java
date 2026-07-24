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

import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Runs the <em>read-only</em> checks for one real-world XML dataset that parses into the model but
 * does not pass strict XSD validation: well-formed XML, parse into the expected {@code rootType},
 * and a proof that real content survived by re-serializing the parsed model and asserting the
 * output carries a stable real identifier from the source.
 *
 * <p>Unlike {@link XmlRoundTrip} this performs <strong>no</strong> XSD validation and
 * <strong>no</strong> round-trip diff: such feeds (for example NDW's truck-parking status, whose
 * {@code targetClass="par:ParkingTable"} drifts from the bundled XSD's fixed prefix) are
 * XSD-invalid on the wire yet meaningfully readable.
 */
public final class XmlReadOnly {

    private final DatexMarshaller marshaller = DatexXml.createMarshaller();

    /**
     * Verifies the read-only XML dataset.
     *
     * @param dataset the dataset to verify
     * @param contentTokens verbatim substrings from the real feed that must appear in the model
     *     re-serialized to XML (for example a stable parking-table identifier), proving real content
     *     survived parsing rather than an empty envelope
     * @throws Exception if a resource cannot be read
     */
    public void verify(Dataset dataset, String... contentTokens) throws Exception {
        byte[] original = readResource(dataset.resourcePath());

        // 1. well-formed XML
        assertWellFormed(original, dataset);

        // 2. parse succeeds into the expected type (no XSD validation: the feed is XSD-invalid)
        Object model = marshaller.read(original, dataset.rootType());
        assertThat(model).as("parsed model for %s", dataset).isNotNull();
        assertThat(model).as("model type for %s", dataset).isInstanceOf(dataset.rootType());

        // 3. real content survives: re-serialize the parsed model and assert stable identifiers.
        //    Deliberately no round-trip diff against the source (prefix drift makes it non-canonical).
        byte[] serialized = marshaller.write(model);
        String serializedText = new String(serialized, StandardCharsets.UTF_8);
        assertThat(serializedText)
                .as("real content missing after read for %s", dataset)
                .contains(contentTokens);
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream in = XmlReadOnly.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Dataset resource not found on classpath: " + path);
            }
            return in.readAllBytes();
        }
    }

    private static void assertWellFormed(byte[] xml, Dataset dataset) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        } catch (Exception e) {
            throw new AssertionError("Not well-formed XML: " + dataset, e);
        }
    }
}
