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
package dev.juherr.datex4j.xml;

import dev.juherr.datex4j.core.DatexVersion;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Entry point for creating {@link DatexMarshaller} instances.
 *
 * <p>Use {@link #createMarshaller()} for the default configuration (pretty-printed, non-validating,
 * UTF-8) or {@link #builder()} to customize behaviour:
 *
 * <pre>{@code
 * DatexMarshaller marshaller = DatexXml.builder()
 *     .prettyPrint(false)
 *     .validating(true)
 *     .build();
 * }</pre>
 */
public final class DatexXml {

    private DatexXml() {}

    /**
     * Creates a marshaller with the default configuration: pretty-printed, non-validating, UTF-8, for
     * the {@linkplain DatexVersion#current() current} DATEX II version.
     *
     * @return a new marshaller
     */
    public static DatexMarshaller createMarshaller() {
        return builder().build();
    }

    /**
     * Returns a builder for configuring a marshaller.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link DatexMarshaller} instances. */
    public static final class Builder {

        private DatexVersion version = DatexVersion.current();
        private boolean prettyPrint = true;
        private boolean validating = false;
        private Charset charset = StandardCharsets.UTF_8;

        private Builder() {}

        /**
         * Sets the DATEX II version whose schemas are used for validation. Defaults to {@link
         * DatexVersion#current()}.
         *
         * @param version the DATEX II version
         * @return this builder
         */
        public Builder version(DatexVersion version) {
            this.version = requireNonNull(version, "version");
            return this;
        }

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
         * Enables or disables schema validation on both read and write. Disabled by default.
         *
         * @param validating whether to validate against the DATEX II schemas
         * @return this builder
         */
        public Builder validating(boolean validating) {
            this.validating = validating;
            return this;
        }

        /**
         * Sets the character set used when writing. Defaults to UTF-8.
         *
         * @param charset the character set
         * @return this builder
         */
        public Builder charset(Charset charset) {
            this.charset = requireNonNull(charset, "charset");
            return this;
        }

        /**
         * Builds the configured marshaller.
         *
         * @return a new marshaller
         * @throws DatexXmlException if the JAXB context or schemas cannot be initialized
         */
        public DatexMarshaller build() {
            return new JaxbDatexMarshaller(version, prettyPrint, validating, charset);
        }

        private static <T> T requireNonNull(T value, String name) {
            if (value == null) {
                throw new NullPointerException(name);
            }
            return value;
        }
    }
}
