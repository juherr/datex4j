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

import dev.juherr.datex4j.validation.ValidationMessage.Severity;
import java.util.List;

/**
 * The outcome of validating a DATEX II document: all problems found, not just the first.
 *
 * <p>A document is {@linkplain #isValid() valid} when it produced no {@code ERROR} or {@code FATAL}
 * message; warnings alone do not make it invalid.
 */
public final class ValidationResult {

    private final List<ValidationMessage> messages;

    ValidationResult(List<ValidationMessage> messages) {
        this.messages = List.copyOf(messages);
    }

    /**
     * Tells whether the document is valid, i.e. produced no error or fatal message.
     *
     * @return {@code true} if there are no errors or fatal errors
     */
    public boolean isValid() {
        return messages.stream().noneMatch(m -> m.severity() == Severity.ERROR || m.severity() == Severity.FATAL);
    }

    /**
     * Returns every message produced during validation, in document order.
     *
     * @return all validation messages (possibly empty)
     */
    public List<ValidationMessage> messages() {
        return messages;
    }

    /**
     * Returns only the error and fatal messages.
     *
     * @return the error and fatal messages (possibly empty)
     */
    public List<ValidationMessage> errors() {
        return messages.stream()
                .filter(m -> m.severity() == Severity.ERROR || m.severity() == Severity.FATAL)
                .toList();
    }

    /**
     * Returns only the warning messages.
     *
     * @return the warning messages (possibly empty)
     */
    public List<ValidationMessage> warnings() {
        return messages.stream().filter(m -> m.severity() == Severity.WARNING).toList();
    }
}
