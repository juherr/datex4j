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
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

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
    void rejectsDocumentsThatDeclareEntities() {
        String valid = DatexXml.builder().build().writeToString(validPublication());
        String invalid = valid.replaceFirst("\\?>", "?><!DOCTYPE situationPublication [<!ENTITY probe \"en\">]>")
                .replace("lang=\"en\"", "lang=\"&probe;\"");

        ValidationResult result = validator.validate(invalid.getBytes(StandardCharsets.UTF_8));

        assertThat(result.isValid()).isFalse();
        assertThat(result.errors())
                .anySatisfy(message -> assertThat(message.message()).containsIgnoringCase("DOCTYPE"));
    }

    @Test
    void reportsMalformedXmlAsAFatalValidationMessage() {
        ValidationResult result = validator.validate("<broken".getBytes(StandardCharsets.UTF_8));

        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).extracting(ValidationMessage::severity).contains(ValidationMessage.Severity.FATAL);
    }

    @Test
    void recordsFatalSaxErrorsWithoutLocation() {
        var handler = new DatexValidator.CollectingErrorHandler();

        handler.recordFatalIfEmpty(new SAXException("fatal parser error"));

        assertThat(handler.messages())
                .containsExactly(new ValidationMessage(ValidationMessage.Severity.FATAL, "fatal parser error", -1, -1));
    }

    @Test
    void wrapsIoFailuresFromInputStreams() {
        InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("simulated read failure");
            }
        };

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> validator.validate(failing))
                .isInstanceOf(DatexValidationException.class)
                .hasMessageContaining("Failed to read the document to validate")
                .hasRootCauseMessage("simulated read failure");
    }

    @Test
    void acceptsAValidMessageContainerDocument() {
        byte[] xml = resource("/messagecontainer/message-container-v3_7.xml");

        ValidationResult result = validator.validate(xml);

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void stillAcceptsAPayloadRootedDocument() {
        // The message container roots must not regress plain payload-rooted validation.
        ValidationResult result = validator.validate(validPublication());

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void acceptsAValidV23D2LogicalModel() {
        DatexValidator v23 = DatexValidator.forVersion(DatexVersion.V2_3);

        ValidationResult result = v23.validate(validV23D2LogicalModel());

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void acceptsAValidV21D2LogicalModel() {
        DatexValidator v21 = DatexValidator.forVersion(DatexVersion.V2_1);

        ValidationResult result = v21.validate(validV21D2LogicalModel());

        assertThat(result.isValid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    private static jakarta.xml.bind.JAXBElement<dev.juherr.datex4j.model.v2_3.D2LogicalModel> validV23D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_3.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_3.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_3.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_3.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        var publication = new dev.juherr.datex4j.model.v2_3.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_3.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_3.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        root.setPayloadPublication(publication);
        return factory.createD2LogicalModel(root);
    }

    private static jakarta.xml.bind.JAXBElement<dev.juherr.datex4j.model.v2_1.D2LogicalModel> validV21D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_1.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_1.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_1.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_1.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        var publication = new dev.juherr.datex4j.model.v2_1.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_1.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_1.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        root.setPayloadPublication(publication);
        return factory.createD2LogicalModel(root);
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

    private static byte[] resource(String path) {
        try (InputStream in = DatexValidatorTest.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing test resource: " + path);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static javax.xml.datatype.XMLGregorianCalendar dateTime() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z");
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }
}
