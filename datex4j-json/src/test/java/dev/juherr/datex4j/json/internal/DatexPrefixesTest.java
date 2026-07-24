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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DatexPrefixesTest {

    @Test
    void mapsCoreNamespacesToDatexPrefixes() {
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/energyInfrastructure"))
                .isEqualTo("egi");
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/locationReferencing"))
                .isEqualTo("loc");
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/locationExtension"))
                .isEqualTo("locx");
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/facilities"))
                .isEqualTo("fac");
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/common")).isEqualTo("com");
        assertThat(DatexPrefixes.namespaceFor("egi")).isEqualTo("http://datex2.eu/schema/3/energyInfrastructure");
    }

    @Test
    void mapsMessageContainerAndExchangeInformationToTheirRealXsdPrefixes() {
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/messageContainer"))
                .isEqualTo("con");
        assertThat(DatexPrefixes.prefixFor("http://datex2.eu/schema/3/exchangeInformation"))
                .isEqualTo("ex");
        assertThat(DatexPrefixes.namespaceFor("con")).isEqualTo("http://datex2.eu/schema/3/messageContainer");
        assertThat(DatexPrefixes.namespaceFor("ex")).isEqualTo("http://datex2.eu/schema/3/exchangeInformation");
    }

    @Test
    void rejectsUnknownNamespacesAndPrefixes() {
        assertThatThrownBy(() -> DatexPrefixes.prefixFor("http://datex2.eu/schema/3/notARealModule"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DatexPrefixes.namespaceFor("nope")).isInstanceOf(IllegalArgumentException.class);
    }
}
