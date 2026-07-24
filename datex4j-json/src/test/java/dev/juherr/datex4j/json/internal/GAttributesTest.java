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
package dev.juherr.datex4j.json.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GAttributesTest {

    @Test
    void suffixesGlobalIdentificationAndModelAttributesWithG() {
        assertThat(GAttributes.jsonName("id")).isEqualTo("idG");
        assertThat(GAttributes.jsonName("version")).isEqualTo("versionG");
        assertThat(GAttributes.jsonName("modelBaseVersion")).isEqualTo("modelBaseVersionG");
    }

    @Test
    void leavesLocallyDeclaredAttributesUnchanged() {
        assertThat(GAttributes.jsonName("lang")).isEqualTo("lang");
        assertThat(GAttributes.jsonName("order")).isEqualTo("order");
        assertThat(GAttributes.jsonName("extensionName")).isEqualTo("extensionName");
    }

    @Test
    void stripsGSuffixFromGlobalAttributeGroupJsonNames() {
        assertThat(GAttributes.xmlName("idG")).isEqualTo("id");
        assertThat(GAttributes.xmlName("versionG")).isEqualTo("version");
        assertThat(GAttributes.xmlName("modelBaseVersionG")).isEqualTo("modelBaseVersion");
    }

    @Test
    void leavesNonGroupJsonNamesUnchangedIncludingOnesEndingInG() {
        assertThat(GAttributes.xmlName("lang")).isEqualTo("lang");
        assertThat(GAttributes.xmlName("order")).isEqualTo("order");
        // "somethingG" is not idG/versionG/modelBaseVersionG, so it must NOT be stripped.
        assertThat(GAttributes.xmlName("somethingG")).isEqualTo("somethingG");
    }
}
