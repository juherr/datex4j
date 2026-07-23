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

/**
 * Unchecked exception thrown when reading or writing DATEX II JSON fails.
 *
 * <p>It wraps the lower-level Jackson {@code IOException} so callers are not exposed to the JSON
 * implementation.
 */
public class DatexJsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception with a message and a cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public DatexJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
