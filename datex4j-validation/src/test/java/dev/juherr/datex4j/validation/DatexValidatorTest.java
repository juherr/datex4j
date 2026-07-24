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

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.charset.StandardCharsets;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.Test;

class DatexValidatorTest {

    private final DatexValidator validator = DatexValidator.create();

    @Test
    void acceptsAValidPublication() {
        ValidationResult result = validator.validate(validPublication());

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void collectsErrorsForAnInvalidDocument() {
        // country is restricted to two characters; make its value invalid (prefix-agnostic).
        String valid = DatexXml.builder().build().writeToString(validPublication());
        String invalid = valid.replace(">gb<", ">invalid<");

        ValidationResult result = validator.validate(invalid.getBytes(StandardCharsets.UTF_8));

        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).isNotEmpty();
        assertThat(result.errors().get(0).lineNumber()).isPositive();
    }

    @Test
    void acceptsAValidV35Publication() {
        DatexValidator v35 = DatexValidator.forVersion(DatexVersion.V3_5);

        ValidationResult result = v35.validate(validV35Publication());

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    private static dev.juherr.datex4j.model.v3_5.situation.SituationPublication validV35Publication() {
        var publication = new dev.juherr.datex4j.model.v3_5.situation.SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v3_5.common.InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        return publication;
    }

    private static SituationPublication validPublication() {
        SituationPublication publication = new SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(dateTime());
        InternationalIdentifier creator = new InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        return publication;
    }

    private static javax.xml.datatype.XMLGregorianCalendar dateTime() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z");
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }
}
