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

class DatexSchemasTest {

    @Test
    void resourceDirectoryIsVersionScoped() {
        assertEquals("META-INF/datex4j/schema/v3.7", DatexSchemas.resourceDirectory(DatexVersion.V3_7));
    }

    @Test
    void resourceResolvesSchemaFile() {
        assertEquals(
                "META-INF/datex4j/schema/v3.7/DATEXII_3_Common.xsd",
                DatexSchemas.resource(DatexVersion.V3_7, "DATEXII_3_Common.xsd"));
    }

    @Test
    void rootSchemaPointsAtD2Payload() {
        assertEquals(
                "META-INF/datex4j/schema/v3.7/DATEXII_3_D2Payload.xsd", DatexSchemas.rootSchema(DatexVersion.V3_7));
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> DatexSchemas.resourceDirectory(null));
        assertThrows(NullPointerException.class, () -> DatexSchemas.resource(DatexVersion.V3_7, null));
    }
}
