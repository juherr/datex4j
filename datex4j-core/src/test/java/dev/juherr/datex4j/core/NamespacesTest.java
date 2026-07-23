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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class NamespacesTest {

    @Test
    void baseHasNoTrailingSlash() {
        assertThat(Namespaces.BASE).isEqualTo("http://datex2.eu/schema/3");
    }

    @Test
    void moduleBuildsNamespaceUnderBase() {
        assertThat(Namespaces.module("situation")).isEqualTo("http://datex2.eu/schema/3/situation");
    }

    @Test
    void wellKnownConstantsMatchOfficialNamespaces() {
        assertThat(Namespaces.COMMON).isEqualTo("http://datex2.eu/schema/3/common");
        assertThat(Namespaces.D2_PAYLOAD).isEqualTo("http://datex2.eu/schema/3/d2Payload");
        assertThat(Namespaces.AFIR_ENERGY_INFRASTRUCTURE)
                .isEqualTo("http://datex2.eu/schema/3/afirEnergyInfrastructure");
    }

    @Test
    void moduleRejectsNull() {
        assertThatNullPointerException().isThrownBy(() -> Namespaces.module(null));
    }
}
