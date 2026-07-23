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

import dev.juherr.datex4j.core.DatexVersion;
import javax.xml.validation.Schema;

/**
 * Builds compiled {@link Schema} instances for the DATEX II XML Schema set bundled on the classpath.
 *
 * <p>The same schemas power the validating marshaller in this module and the richer validation API
 * in {@code datex4j-validation}. Compiling a schema is comparatively expensive, so callers should
 * reuse the returned instance (it is thread-safe).
 */
public final class DatexSchemaFactory {

    private DatexSchemaFactory() {}

    /**
     * Compiles the DATEX II schema set for the given version.
     *
     * @param version the DATEX II version
     * @return the compiled, thread-safe schema
     * @throws DatexXmlException if the schemas cannot be found or compiled
     */
    public static Schema newSchema(DatexVersion version) {
        return ClasspathSchemas.load(version);
    }
}
