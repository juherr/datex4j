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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.juherr.datex4j.validation.ValidationMessage.Severity;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

class ValidationResultTest {

    @Test
    void separatesWarningsFromErrorsAndFatalMessages() {
        var warning = new ValidationMessage(Severity.WARNING, "warning", 1, 2);
        var error = new ValidationMessage(Severity.ERROR, "error", 3, 4);
        var fatal = new ValidationMessage(Severity.FATAL, "fatal", 5, 6);

        ValidationResult result = new ValidationResult(new ArrayList<>(java.util.List.of(warning, error, fatal)));

        assertThat(result.isValid()).isFalse();
        assertThat(result.messages()).containsExactly(warning, error, fatal);
        assertThat(result.warnings()).containsExactly(warning);
        assertThat(result.errors()).containsExactly(error, fatal);
        assertThatThrownBy(() -> result.messages().add(warning)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void warningsAloneDoNotInvalidateADocument() {
        var warning = new ValidationMessage(Severity.WARNING, "warning", 1, 2);

        ValidationResult result = new ValidationResult(java.util.List.of(warning));

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void preservesWarningLocationFromTheSchemaValidator() {
        var handler = new DatexValidator.CollectingErrorHandler();

        handler.warning(new SAXParseException("schema warning", null, null, 12, 34));

        assertThat(handler.messages())
                .containsExactly(new ValidationMessage(Severity.WARNING, "schema warning", 12, 34));
    }
}
