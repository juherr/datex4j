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
package dev.juherr.datex4j.ocpi.mapping.internal;

import dev.juherr.datex4j.model.v3_7.facilities.Organisation;
import dev.juherr.datex4j.model.v3_7.facilities.OrganisationSpecification;

/** Builds and reads name-only DATEX II {@link Organisation} values from plain OCPI text. */
public final class Organisations {

    private Organisations() {}

    /**
     * Builds a name-only DATEX II organisation from {@code name}, or {@code null} if {@code name}
     * is null.
     */
    public static Organisation named(String name) {
        if (name == null) {
            return null;
        }
        OrganisationSpecification organisation = new OrganisationSpecification();
        organisation.setName(MultilingualStrings.of("en", name));
        return organisation;
    }

    /**
     * Returns the name of {@code organisation}, or {@code null} if {@code organisation} is not an
     * {@link OrganisationSpecification} or has no name.
     */
    public static String nameOf(Organisation organisation) {
        if (!(organisation instanceof OrganisationSpecification spec)) {
            return null;
        }
        return MultilingualStrings.firstValue(spec.getName());
    }
}
