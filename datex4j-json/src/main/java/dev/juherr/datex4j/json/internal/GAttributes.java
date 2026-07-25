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

import java.util.Set;

/**
 * DATEX II conformant JSON suffixes attributes from the global identification/model attribute
 * group with a trailing {@code G}, to disambiguate them from locally declared attributes that
 * happen to share the same name.
 *
 * <p>The group is pinned to {@code {id, version, modelBaseVersion}} and validated against the
 * real-world Fintraffic AFIR oracle fixture ({@code
 * datex4j-json/src/test/resources/datex-json/finland-afir-messagecontainer.v3_6.json}), which
 * shows {@code idG}, {@code versionG}, and {@code modelBaseVersionG} but leaves locally declared
 * attributes such as {@code lang}, {@code order}, and {@code extensionName} bare.
 */
final class GAttributes {

    private static final Set<String> GLOBAL_GROUP = Set.of("id", "version", "modelBaseVersion");

    private GAttributes() {}

    /**
     * Returns the conformant JSON attribute name for an XML attribute name, appending {@code G}
     * iff the attribute belongs to the global identification/model attribute group.
     *
     * @param xmlAttributeName the XML attribute name, for example {@code "id"} or {@code "lang"}
     * @return the JSON attribute name, for example {@code "idG"} or {@code "lang"}
     */
    public static String jsonName(String xmlAttributeName) {
        return GLOBAL_GROUP.contains(xmlAttributeName) ? xmlAttributeName + "G" : xmlAttributeName;
    }

    /**
     * Returns the XML attribute name for a conformant JSON attribute name, stripping a trailing
     * {@code G} iff the stripped name belongs to the global identification/model attribute group.
     *
     * <p>A JSON name that merely ends with {@code G} but is not one of {@code idG}, {@code
     * versionG}, or {@code modelBaseVersionG} is returned unchanged.
     *
     * @param jsonName the JSON attribute name, for example {@code "idG"} or {@code "lang"}
     * @return the XML attribute name, for example {@code "id"} or {@code "lang"}
     */
    public static String xmlName(String jsonName) {
        if (jsonName.endsWith("G")) {
            String candidate = jsonName.substring(0, jsonName.length() - 1);
            if (GLOBAL_GROUP.contains(candidate)) {
                return candidate;
            }
        }
        return jsonName;
    }
}
