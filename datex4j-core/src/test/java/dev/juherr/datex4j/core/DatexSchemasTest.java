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

class DatexSchemasTest {

    @Test
    void resourceDirectoryIsVersionScoped() {
        assertThat(DatexSchemas.resourceDirectory(DatexVersion.V3_7)).isEqualTo("META-INF/datex4j/schema/v3.7");
    }

    @Test
    void resourceResolvesSchemaFile() {
        assertThat(DatexSchemas.resource(DatexVersion.V3_7, "DATEXII_3_Common.xsd"))
                .isEqualTo("META-INF/datex4j/schema/v3.7/DATEXII_3_Common.xsd");
    }

    @Test
    void rootSchemaPointsAtD2Payload() {
        assertThat(DatexSchemas.rootSchema(DatexVersion.V3_7))
                .isEqualTo("META-INF/datex4j/schema/v3.7/DATEXII_3_D2Payload.xsd");
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> DatexSchemas.resourceDirectory(null));
        assertThatNullPointerException().isThrownBy(() -> DatexSchemas.resource(DatexVersion.V3_7, null));
    }
}
