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

import java.util.Map;
import java.util.stream.Collectors;

/**
 * DATEX II namespace &harr; standard short prefix, per the official XSD {@code xmlns:}
 * declarations.
 *
 * <p>Every entry is confirmed against the vendored XSDs under {@code
 * datex4j-model/src/main/resources/META-INF/datex4j/schema/v3.7/}, for example:
 *
 * <pre>{@code
 * grep -ohE 'xmlns:[a-z0-9]+="http://datex2.eu/schema/3/[^"]+"' \
 *   datex4j-model/src/main/resources/META-INF/datex4j/schema/v3.7/*.xsd | sort -u
 * }</pre>
 */
public final class DatexPrefixes {

    private static final String BASE = "http://datex2.eu/schema/3/";

    private static final Map<String, String> NS_TO_PREFIX = Map.ofEntries(
            Map.entry(BASE + "common", "com"),
            Map.entry(BASE + "commonExtension", "comx"),
            Map.entry(BASE + "locationReferencing", "loc"),
            Map.entry(BASE + "locationExtension", "locx"),
            Map.entry(BASE + "facilities", "fac"),
            Map.entry(BASE + "energyInfrastructure", "egi"),
            Map.entry(BASE + "messageContainer", "con"),
            Map.entry(BASE + "exchangeInformation", "ex"),
            Map.entry(BASE + "afirEnergyInfrastructure", "aegi"),
            Map.entry(BASE + "afirFacilities", "afac"),
            Map.entry(BASE + "cisInformation", "cis"),
            Map.entry(BASE + "controlledZone", "cz"),
            Map.entry(BASE + "d2Payload", "d2"),
            Map.entry(BASE + "faultAndStatus", "fst"),
            Map.entry(BASE + "informationManagement", "inf"),
            Map.entry(BASE + "parking", "prk"),
            Map.entry(BASE + "reroutingManagementEnhanced", "rer"),
            Map.entry(BASE + "roadTrafficData", "roa"),
            Map.entry(BASE + "situation", "sit"),
            Map.entry(BASE + "trafficManagementPlan", "tmp"),
            Map.entry(BASE + "trafficRegulation", "tro"),
            Map.entry(BASE + "urbanExtensions", "ubx"),
            Map.entry(BASE + "vms", "vms"));

    private static final Map<String, String> PREFIX_TO_NS =
            NS_TO_PREFIX.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private DatexPrefixes() {}

    /**
     * Returns the standard short prefix for a DATEX II namespace URI.
     *
     * @param namespaceUri the DATEX II namespace URI, for example {@code
     *     "http://datex2.eu/schema/3/common"}
     * @return the standard short prefix, for example {@code "com"}
     * @throws IllegalArgumentException if the namespace is not a known DATEX II module
     */
    public static String prefixFor(String namespaceUri) {
        String prefix = NS_TO_PREFIX.get(namespaceUri);
        if (prefix == null) {
            throw new IllegalArgumentException("No DATEX II JSON prefix for namespace: " + namespaceUri);
        }
        return prefix;
    }

    /**
     * Returns the DATEX II namespace URI for a standard short prefix.
     *
     * @param prefix the standard short prefix, for example {@code "com"}
     * @return the DATEX II namespace URI, for example {@code
     *     "http://datex2.eu/schema/3/common"}
     * @throws IllegalArgumentException if the prefix is not a known DATEX II module prefix
     */
    public static String namespaceFor(String prefix) {
        String namespaceUri = PREFIX_TO_NS.get(prefix);
        if (namespaceUri == null) {
            throw new IllegalArgumentException("No DATEX II namespace for prefix: " + prefix);
        }
        return namespaceUri;
    }
}
