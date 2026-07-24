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
package dev.juherr.datex4j.it.support;

import dev.juherr.datex4j.core.DatexVersion;

/**
 * A committed DATEX II dataset fixture, the on-the-wire {@link Format} it is expressed in, the
 * DATEX II model {@link DatexVersion} it targets, and the type its root unmarshals to.
 *
 * <p>XML datasets unmarshal to a publication type (for example {@code
 * EnergyInfrastructureTablePublication}) and run the XML round-trip checks. JSON datasets are
 * conformant DATEX II JSON rooted at a {@code MessageContainer} and run the JSON round-trip checks;
 * their {@code rootType} is the container class.
 */
public record Dataset(
        String id, String country, Format format, DatexVersion version, String resourcePath, Class<?> rootType) {

    /** The serialization the committed fixture is expressed in. */
    public enum Format {
        /** DATEX II XML, validated and round-tripped through {@code datex4j-xml}. */
        XML,
        /** Conformant DATEX II JSON (MessageContainer root), round-tripped through {@code datex4j-json}. */
        JSON
    }

    @Override
    public String toString() {
        return country + "/" + id;
    }
}
