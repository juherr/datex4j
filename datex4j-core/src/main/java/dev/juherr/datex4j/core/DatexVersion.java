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
 * The DATEX II model versions supported by datex4j.
 *
 * <p>Several versions can be bundled at once, each with its own generated model. Callers select a
 * version through the public API rather than hard-coding a string; the {@linkplain #current()
 * current} version is used by default.
 */
public enum DatexVersion {

    /** DATEX II version 3.0. */
    V3_0("3.0"),

    /** DATEX II version 3.1. */
    V3_1("3.1"),

    /** DATEX II version 3.2. */
    V3_2("3.2"),

    /** DATEX II version 3.3. */
    V3_3("3.3"),

    /** DATEX II version 3.4. */
    V3_4("3.4"),

    /** DATEX II version 3.5. */
    V3_5("3.5"),

    /** DATEX II version 3.6. */
    V3_6("3.6"),

    /** DATEX II version 3.7. */
    V3_7("3.7");

    private final String id;

    DatexVersion(String id) {
        this.id = id;
    }

    /**
     * Returns the canonical version identifier as published by DATEX II, for example {@code "3.7"}.
     *
     * @return the DATEX II version identifier
     */
    public String id() {
        return id;
    }

    /**
     * Returns the version directory segment used to locate bundled resources, for example {@code
     * "v3.7"}.
     *
     * @return the resource directory segment for this version
     */
    public String resourceSegment() {
        return "v" + id;
    }

    /**
     * Returns the Java package segment used by the generated model for this version, for example
     * {@code "v3_7"}. Each version's model lives under {@code dev.juherr.datex4j.model.<segment>}.
     *
     * @return the model package segment for this version
     */
    public String packageSegment() {
        return "v" + id.replace('.', '_');
    }

    /**
     * Returns the version that datex4j uses by default. This is the newest DATEX II version bundled
     * by {@code datex4j-model}.
     *
     * @return the default DATEX II version
     */
    public static DatexVersion current() {
        return V3_7;
    }

    @Override
    public String toString() {
        return id;
    }
}
