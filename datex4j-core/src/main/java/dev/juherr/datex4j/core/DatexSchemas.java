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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Locations of the official DATEX II XML Schemas bundled on the classpath by {@code datex4j-model}.
 *
 * <p>The schemas are packaged as classpath resources so that tools such as the XML marshaller can
 * validate documents without touching the file system.
 *
 * <p>DATEX II v3 is modular: {@link #ROOT_SCHEMA} imports every payload module schema, and from
 * Exchange 2020 onwards {@link #MESSAGE_CONTAINER_SCHEMA} additionally declares the {@code
 * messageContainer} envelope. DATEX II v2 is monolithic instead: a single {@code
 * DATEXIISchema_2_2_x.xsd} declares the whole model rooted at {@code d2LogicalModel}, so its root
 * schema file name is version-specific. {@link #rootSchemas(DatexVersion)} returns the full set of
 * root schema resources needed to build a validating {@code javax.xml.validation.Schema} that
 * recognises the documents of a version.
 */
public final class DatexSchemas {

    /** Classpath directory (without a trailing slash) under which schemas are bundled: {@value}. */
    public static final String RESOURCE_ROOT = "META-INF/datex4j/schema";

    /** File name of the DATEX II root schema that imports all payload module schemas: {@value}. */
    public static final String ROOT_SCHEMA = "DATEXII_3_D2Payload.xsd";

    /**
     * File name of the DATEX II Exchange 2020 schema declaring the {@code messageContainer} and
     * {@code exchangeInformation} envelope elements: {@value}. Bundled only for versions that ship
     * it (v3.6 and v3.7).
     */
    public static final String MESSAGE_CONTAINER_SCHEMA = "DATEXII_3_MessageContainer.xsd";

    /** Versions that bundle {@link #MESSAGE_CONTAINER_SCHEMA} (Exchange 2020 onwards). */
    private static final Set<DatexVersion> WITH_MESSAGE_CONTAINER = EnumSet.of(DatexVersion.V3_6, DatexVersion.V3_7);

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
     * Returns the file name of the root schema for a version.
     *
     * <p>For v3 this is the modular {@link #ROOT_SCHEMA}; for v2 it is the version-specific
     * monolithic schema, for example {@code DATEXIISchema_2_2_3.xsd} for v2.3.
     *
     * @param version the DATEX II version
     * @return the root schema file name
     * @throws NullPointerException if {@code version} is {@code null}
     */
    public static String rootSchemaFileName(DatexVersion version) {
        requireVersion(version);
        if (isV2(version)) {
            // v2 minors share a single monolithic schema whose name encodes the minor: v2.3 ->
            // DATEXIISchema_2_2_3.xsd.
            String minor = version.id().substring(version.id().indexOf('.') + 1);
            return "DATEXIISchema_2_2_" + minor + ".xsd";
        }
        return ROOT_SCHEMA;
    }

    /**
     * Returns the classpath resource path of the root schema for a version.
     *
     * @param version the DATEX II version
     * @return the classpath resource path of the version's root schema
     * @throws NullPointerException if {@code version} is {@code null}
     */
    public static String rootSchema(DatexVersion version) {
        return resource(version, rootSchemaFileName(version));
    }

    private static boolean isV2(DatexVersion version) {
        return version.id().startsWith("2.");
    }

    /**
     * Returns the classpath resource paths of every root schema that must be compiled together to
     * validate documents for a version.
     *
     * <p>This is always the {@linkplain #ROOT_SCHEMA payload root}, plus the {@linkplain
     * #MESSAGE_CONTAINER_SCHEMA message container schema} for versions that bundle it (v3.6 and
     * v3.7). Compiling both lets a single {@code Schema} recognise both payload-rooted documents and
     * Exchange 2020 {@code messageContainer}-rooted documents.
     *
     * @param version the DATEX II version
     * @return the ordered list of root schema resource paths
     * @throws NullPointerException if {@code version} is {@code null}
     */
    public static List<String> rootSchemas(DatexVersion version) {
        requireVersion(version);
        if (WITH_MESSAGE_CONTAINER.contains(version)) {
            return List.of(rootSchema(version), resource(version, MESSAGE_CONTAINER_SCHEMA));
        }
        return List.of(rootSchema(version));
    }

    private static void requireVersion(DatexVersion version) {
        if (version == null) {
            throw new NullPointerException("version");
        }
    }
}
