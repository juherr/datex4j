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
