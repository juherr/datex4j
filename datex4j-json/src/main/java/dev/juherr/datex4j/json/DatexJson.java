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
package dev.juherr.datex4j.json;

import dev.juherr.datex4j.core.DatexVersion;

/**
 * Entry point for creating {@link DatexJsonMapper} instances.
 *
 * <p>{@code datex4j-json} reads and writes the <strong>conformant DATEX II JSON</strong>
 * representation: namespace-prefixed substitution members (for example {@code
 * "locxFacilityLocation"}), {@code G}-suffixed versioning attributes ({@code idG}, {@code
 * versionG}, {@code modelBaseVersionG}), flattened multilingual strings, and {@code {value,
 * _extendedValue}}-shaped enums, rooted at the DATEX II {@code MessageContainer} (see {@link
 * DatexJsonMapper#readContainer}).
 *
 * <p>The mapping is <strong>best-effort and lossy</strong>: it targets everyday integration use,
 * not bit-for-bit archival fidelity. Some XML constructs with no natural JSON model slot are
 * dropped on the round trip; for example, a {@code locationReference} carrying both a {@code
 * FacilityLocation} (street address/postcode) and a point location keeps only the point location,
 * because {@code FacilityLocation} is not a {@code LocationReference} subtype. Within a single
 * mapper configuration, reads and writes are deterministic and idempotent (the same input always
 * produces the same output, and re-encoding a decoded value reproduces the same bytes).
 *
 * <p>DATEX II JSON is <strong>not yet officially standardised</strong> by the DATEX II
 * specification. This codec targets the de-facto mapping used by DATEX II wizard tooling and
 * National Access Points (NAPs), anchored against a real-world Fintraffic AFIR fixture; it may
 * need to adapt once an official JSON binding is published.
 *
 * <p>Both bundled DATEX II model versions are supported via {@link
 * Builder#version(DatexVersion)}: pass {@link DatexVersion#V3_6} or {@link DatexVersion#V3_7} (the
 * default is {@link DatexVersion#current()}).
 *
 * <p>Use {@link #createMapper()} for the default configuration (pretty-printed) or {@link
 * #builder()} to customize it.
 */
public final class DatexJson {

    private DatexJson() {}

    /**
     * Creates a mapper with the default configuration (pretty-printed).
     *
     * @return a new mapper
     */
    public static DatexJsonMapper createMapper() {
        return builder().build();
    }

    /**
     * Returns a builder for configuring a mapper.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link DatexJsonMapper} instances. */
    public static final class Builder {

        private boolean prettyPrint = true;
        private DatexVersion version = DatexVersion.current();

        private Builder() {}

        /**
         * Enables or disables indented, human-readable output. Enabled by default.
         *
         * @param prettyPrint whether to indent output
         * @return this builder
         */
        public Builder prettyPrint(boolean prettyPrint) {
            this.prettyPrint = prettyPrint;
            return this;
        }

        /**
         * Selects the DATEX II model version the mapper reads and writes. Defaults to {@link
         * DatexVersion#current()}.
         *
         * @param version the DATEX II model version
         * @return this builder
         */
        public Builder version(DatexVersion version) {
            this.version = version;
            return this;
        }

        /**
         * Builds the configured mapper.
         *
         * @return a new mapper
         */
        public DatexJsonMapper build() {
            return new DatexJsonMapper(prettyPrint, version);
        }
    }
}
