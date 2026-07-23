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
package dev.juherr.datex4j.validation;

/**
 * A single problem reported while validating a DATEX II document.
 *
 * @param severity how serious the problem is
 * @param message the human-readable description reported by the schema validator
 * @param lineNumber the 1-based line where the problem was detected, or {@code -1} if unknown
 * @param columnNumber the 1-based column where the problem was detected, or {@code -1} if unknown
 */
public record ValidationMessage(Severity severity, String message, int lineNumber, int columnNumber) {

    /** Severity of a {@link ValidationMessage}. */
    public enum Severity {
        /** A schema warning; does not by itself make the document invalid. */
        WARNING,
        /** A schema constraint violation; makes the document invalid. */
        ERROR,
        /** A fatal (typically well-formedness) error; makes the document invalid. */
        FATAL
    }
}
