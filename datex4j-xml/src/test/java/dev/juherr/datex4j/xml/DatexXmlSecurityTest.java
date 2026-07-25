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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DatexXmlSecurityTest {

    private final DatexMarshaller marshaller = DatexXml.createMarshaller();

    @Test
    void rejectsDocumentsWithInternalEntities() {
        byte[] xml = withDoctype("<!ENTITY probe \"fr\">");

        assertThatThrownBy(() -> marshaller.read(xml, SituationPublication.class))
                .isInstanceOf(DatexXmlException.class)
                .hasMessageContaining("Failed to read DATEX II XML");
    }

    @Test
    void rejectsDocumentsWithExternalEntities() {
        byte[] xml = withDoctype("<!ENTITY probe SYSTEM \"file:///etc/hosts\">");

        assertThatThrownBy(() -> marshaller.read(xml, SituationPublication.class))
                .isInstanceOf(DatexXmlException.class)
                .hasMessageContaining("Failed to read DATEX II XML");
    }

    @Test
    void rejectsDocumentsWithRecursiveEntityExpansion() {
        byte[] xml = withDoctype("""
                        <!ENTITY a "DATEX">
                        <!ENTITY b "&a;&a;&a;&a;&a;&a;&a;&a;&a;&a;">
                        <!ENTITY probe "&b;&b;&b;&b;&b;&b;&b;&b;&b;&b;">
                        """);

        assertThatThrownBy(() -> marshaller.read(xml, SituationPublication.class))
                .isInstanceOf(DatexXmlException.class)
                .hasMessageContaining("Failed to read DATEX II XML");
    }

    private byte[] withDoctype(String declaration) {
        String original = marshaller.writeToString(Fixtures.situationPublication());
        String doctype = "<!DOCTYPE situationPublication [" + declaration + "]>";
        return original.replaceFirst("\\?>", "?>" + doctype)
                .replace("lang=\"en\"", "lang=\"&probe;\"")
                .getBytes(StandardCharsets.UTF_8);
    }
}
