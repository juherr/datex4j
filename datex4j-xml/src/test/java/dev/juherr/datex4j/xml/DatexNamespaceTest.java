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
package dev.juherr.datex4j.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatexNamespaceTest {

    @Test
    void outputDeclaresTheDatexPayloadNamespace() {
        String xml = DatexXml.createMarshaller().writeToString(Fixtures.situationPublication());

        assertThat(xml).contains("http://datex2.eu/schema/3/d2Payload");
    }

    @Test
    void prettyPrintingIsOnByDefaultAndCanBeDisabled() {
        String pretty = DatexXml.createMarshaller().writeToString(Fixtures.situationPublication());
        String compact = DatexXml.builder().prettyPrint(false).build().writeToString(Fixtures.situationPublication());

        assertThat(pretty).as("pretty output should span multiple lines").contains("\n");
        assertThat(compact.substring(compact.indexOf("?>") + 2))
                .as("compact output should not contain newlines after the XML declaration")
                .doesNotContain("\n");
    }
}
