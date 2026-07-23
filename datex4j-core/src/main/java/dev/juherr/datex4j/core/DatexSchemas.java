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

/**
 * Locations of the official DATEX II XML Schemas bundled on the classpath by {@code datex4j-model}.
 *
 * <p>The schemas are packaged as classpath resources so that tools such as the XML marshaller can
 * validate documents without touching the file system. {@link #ROOT_SCHEMA} imports every module
 * schema and is therefore the entry point for building a validating {@code javax.xml.validation
 * .Schema}.
 */
public final class DatexSchemas {

    /** Classpath directory (without a trailing slash) under which schemas are bundled: {@value}. */
    public static final String RESOURCE_ROOT = "META-INF/datex4j/schema";

    /** File name of the DATEX II root schema that imports all module schemas: {@value}. */
    public static final String ROOT_SCHEMA = "DATEXII_3_D2Payload.xsd";

    private DatexSchemas() {}

    /**
     * Returns the classpath resource directory holding the schemas for a version, for example {@code
     * META-INF/datex4j/schema/v3.7}.
     *
     * @param version the DATEX II version
     * @return the classpath resource directory, without a trailing slash
     * @throws NullPointerException if {@code version} is {@code null}
     */
    public static String resourceDirectory(DatexVersion version) {
        requireVersion(version);
        return RESOURCE_ROOT + "/" + version.resourceSegment();
    }

    /**
     * Returns the classpath resource path of a specific schema file for a version, for example {@code
     * META-INF/datex4j/schema/v3.7/DATEXII_3_Common.xsd}.
     *
     * @param version the DATEX II version
     * @param fileName the schema file name, for example {@code DATEXII_3_Common.xsd}
     * @return the classpath resource path
     * @throws NullPointerException if any argument is {@code null}
     */
    public static String resource(DatexVersion version, String fileName) {
        requireVersion(version);
        if (fileName == null) {
            throw new NullPointerException("fileName");
        }
        return resourceDirectory(version) + "/" + fileName;
    }

    /**
     * Returns the classpath resource path of the root schema for a version.
     *
     * @param version the DATEX II version
     * @return the classpath resource path of {@link #ROOT_SCHEMA}
     * @throws NullPointerException if {@code version} is {@code null}
     */
    public static String rootSchema(DatexVersion version) {
        return resource(version, ROOT_SCHEMA);
    }

    private static void requireVersion(DatexVersion version) {
        if (version == null) {
            throw new NullPointerException("version");
        }
    }
}
