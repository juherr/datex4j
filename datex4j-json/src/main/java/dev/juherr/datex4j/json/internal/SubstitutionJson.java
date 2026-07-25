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

import jakarta.xml.bind.annotation.XmlSchema;
import java.util.Locale;

/**
 * DATEX II conformant JSON substitution-member wrapping, for properties whose declared type is an
 * abstract DATEX II class (a substitution group).
 *
 * <p>In the conformant encoding such a property is written as a single-entry object whose key is
 * the standard short prefix of the concrete type's namespace followed by the concrete type's
 * simple name, for example {@code "locationReference":{"locxFacilityLocation":{...}}} or {@code
 * "operator":{"facOrganisationSpecification":{...}}}, per the real-world Fintraffic AFIR oracle
 * fixture ({@code
 * datex4j-json/src/test/resources/datex-json/finland-afir-messagecontainer.v3_6.json}).
 */
final class SubstitutionJson {

    private SubstitutionJson() {}

    /**
     * Returns the conformant JSON substitution-member key for a concrete DATEX II model instance.
     *
     * @param concreteValue the concrete DATEX II model instance, for example a {@code
     *     FacilityLocation}
     * @return the member key, for example {@code "locxFacilityLocation"}
     * @throws IllegalArgumentException if the value's package has no {@link XmlSchema} namespace,
     *     or the namespace has no known DATEX II JSON prefix
     */
    public static String memberKey(Object concreteValue) {
        Class<?> concreteType = concreteValue.getClass();
        return DatexPrefixes.prefixFor(namespaceOf(concreteType)) + concreteType.getSimpleName();
    }

    /**
     * Resolves the concrete DATEX II model {@link Class} for a conformant JSON substitution-member
     * key.
     *
     * @param memberKey the member key, for example {@code "locxFacilityLocation"}
     * @param versionSegment the generated model version segment, for example {@code "v3_6"}
     * @return the concrete DATEX II model class, for example {@code FacilityLocation.class}
     * @throws IllegalArgumentException if the leading prefix is not a known DATEX II JSON prefix,
     *     or the resolved class does not exist
     */
    public static Class<?> resolveClass(String memberKey, String versionSegment) {
        int prefixEnd = 0;
        while (prefixEnd < memberKey.length() && Character.isLowerCase(memberKey.charAt(prefixEnd))) {
            prefixEnd++;
        }
        String prefix = memberKey.substring(0, prefixEnd);
        String simpleName = memberKey.substring(prefixEnd);

        String namespace = DatexPrefixes.namespaceFor(prefix);
        String packageName = "dev.juherr.datex4j.model." + versionSegment + "." + modulePackageOf(namespace);
        String className = packageName + "." + simpleName;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "No DATEX II model class found for substitution member key: " + memberKey, e);
        }
    }

    private static String namespaceOf(Class<?> concreteType) {
        XmlSchema xmlSchema = concreteType.getPackage().getAnnotation(XmlSchema.class);
        if (xmlSchema == null) {
            throw new IllegalArgumentException(
                    "No @XmlSchema namespace found for package: " + concreteType.getPackageName());
        }
        return xmlSchema.namespace();
    }

    private static String modulePackageOf(String namespace) {
        int lastSlash = namespace.lastIndexOf('/');
        return namespace.substring(lastSlash + 1).toLowerCase(Locale.ROOT);
    }
}
