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
package dev.juherr.datex4j.examples;

import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationResult;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.charset.StandardCharsets;

/** Collects every XSD problem in an invalid DATEX II document. */
public final class ValidationExample {

    private ValidationExample() {}

    /**
     * Runs the example and prints every validation error.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        ValidationResult result = validateInvalidCountry();
        result.errors().forEach(System.out::println);
    }

    static ValidationResult validateInvalidCountry() {
        String valid = DatexXml.createMarshaller().writeToString(DatexExample.sampleSituationPublication());
        String invalid = valid.replace(">gb<", ">invalid-country-code<");

        return DatexValidator.create().validate(invalid.getBytes(StandardCharsets.UTF_8));
    }
}
