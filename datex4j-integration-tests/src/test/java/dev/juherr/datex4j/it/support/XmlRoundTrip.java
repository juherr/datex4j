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

import dev.juherr.datex4j.json.DatexJson;
import dev.juherr.datex4j.json.DatexJsonException;
import dev.juherr.datex4j.json.DatexJsonMapper;
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Runs the mandatory XML checks for one dataset: well-formed, XSD, parse, model, serialize,
 * re-validate, equivalence; then an optional JSON round-trip (skipped when {@code datex4j-json}
 * cannot map the publication).
 */
public final class XmlRoundTrip {

    private final DatexMarshaller marshaller = DatexXml.createMarshaller();
    private final DatexValidator validator = DatexValidator.create();
    private final DatexJsonMapper jsonMapper = DatexJson.createMapper();

    public void verify(Dataset dataset) throws Exception {
        byte[] original = readResource(dataset.resourcePath());

        // 1. well-formed
        assertWellFormed(original, dataset);

        // 2. XSD validation of the source
        var sourceResult = validator.validate(original);
        assertThat(sourceResult.isValid())
                .as("source XSD errors for %s: %s", dataset, sourceResult.errors())
                .isTrue();

        // 3. parse succeeds
        Object model = marshaller.read(original, dataset.rootType());
        assertThat(model).as("parsed model for %s", dataset).isNotNull();

        // 4. object model is the expected type
        assertThat(model).as("model type for %s", dataset).isInstanceOf(dataset.rootType());

        // 5. serialization succeeds
        byte[] serialized = marshaller.write(model);

        // 6. serialized XML re-validates
        var roundResult = validator.validate(serialized);
        assertThat(roundResult.isValid())
                .as("serialized XSD errors for %s: %s", dataset, roundResult.errors())
                .isTrue();

        // 7. parser(serialized) is equivalent to the source (canonical XML comparison)
        Diff diff = DiffBuilder.compare(original)
                .withTest(serialized)
                .ignoreComments()
                .ignoreWhitespace()
                .checkForSimilar()
                .build();
        assertThat(diff.hasDifferences())
                .as("round-trip differences for %s: %s", dataset, diff)
                .isFalse();

        // 8. optional JSON round-trip (skipped when datex4j-json cannot map this publication)
        verifyJsonRoundTrip(model, serialized, dataset);
    }

    private void verifyJsonRoundTrip(Object model, byte[] xmlSerialized, Dataset dataset) {
        byte[] json;
        Object fromJson;
        try {
            json = jsonMapper.write(model);
            fromJson = jsonMapper.read(json, dataset.rootType());
        } catch (DatexJsonException unsupported) {
            return; // JSON conversion is optional; skip when unsupported for this type
        }
        byte[] reserialized = marshaller.write(fromJson);
        Diff diff = DiffBuilder.compare(xmlSerialized)
                .withTest(reserialized)
                .ignoreComments()
                .ignoreWhitespace()
                .checkForSimilar()
                .build();
        assertThat(diff.hasDifferences())
                .as("JSON round-trip differences for %s: %s", dataset, diff)
                .isFalse();
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream in = XmlRoundTrip.class.getResourceAsStream(path)) {
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
