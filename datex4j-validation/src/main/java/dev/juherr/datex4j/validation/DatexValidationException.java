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
 * Unchecked exception thrown when a DATEX II document cannot be read for validation.
 *
 * <p>Schema violations are <em>not</em> reported through this exception; they are collected into a
 * {@link ValidationResult}. This exception signals an I/O or setup failure that prevented validation
 * from running at all.
 */
public class DatexValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception with a message and a cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public DatexValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
