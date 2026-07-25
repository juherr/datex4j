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

import dev.juherr.datex4j.core.DatexVersion;
import org.junit.jupiter.api.Test;

class DatexRuntimeCacheTest {

    @Test
    void reusesCompiledSchemasForTheSameVersionAndClassLoader() {
        assertThat(ClasspathSchemas.load(DatexVersion.V3_7)).isSameAs(ClasspathSchemas.load(DatexVersion.V3_7));
        assertThat(DatexSchemaFactory.newSchema(DatexVersion.V3_7)).isSameAs(ClasspathSchemas.load(DatexVersion.V3_7));
    }

    @Test
    void reusesJaxbContextsForTheSameModelAndClassLoader() {
        var model = VersionModel.of(DatexVersion.V3_7);

        assertThat(JaxbContexts.get(model)).isSameAs(JaxbContexts.get(model));
    }
}
