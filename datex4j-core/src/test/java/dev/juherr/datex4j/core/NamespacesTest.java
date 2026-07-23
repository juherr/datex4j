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
package dev.juherr.datex4j.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NamespacesTest {

    @Test
    void baseHasNoTrailingSlash() {
        assertEquals("http://datex2.eu/schema/3", Namespaces.BASE);
    }

    @Test
    void moduleBuildsNamespaceUnderBase() {
        assertEquals("http://datex2.eu/schema/3/situation", Namespaces.module("situation"));
    }

    @Test
    void wellKnownConstantsMatchOfficialNamespaces() {
        assertEquals("http://datex2.eu/schema/3/common", Namespaces.COMMON);
        assertEquals("http://datex2.eu/schema/3/d2Payload", Namespaces.D2_PAYLOAD);
        assertEquals("http://datex2.eu/schema/3/afirEnergyInfrastructure", Namespaces.AFIR_ENERGY_INFRASTRUCTURE);
    }

    @Test
    void moduleRejectsNull() {
        assertThrows(NullPointerException.class, () -> Namespaces.module(null));
    }
}
