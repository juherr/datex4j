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

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.validation.ValidationMessage.Severity;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexSchemaFactory;
import dev.juherr.datex4j.xml.DatexXml;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validates DATEX II documents against the official XML Schemas, collecting <em>every</em> problem
 * rather than failing on the first.
 *
 * <p>Where {@code datex4j-xml}'s validating marshaller throws on the first schema violation, this
 * validator returns a {@link ValidationResult} describing all errors and warnings with their
 * locations — better suited to reporting and tooling. A validator is bound to a DATEX II version, is
 * immutable and thread-safe, and reuses a single compiled schema.
 *
 * <pre>{@code
 * DatexValidator validator = DatexValidator.create();
 * ValidationResult result = validator.validate(xmlBytes);
 * if (!result.isValid()) {
 *   result.errors().forEach(System.out::println);
 * }
 * }</pre>
 */
public final class DatexValidator {

    private final Schema schema;
    private final DatexMarshaller marshaller;

    private DatexValidator(DatexVersion version) {
        this.schema = DatexSchemaFactory.newSchema(version);
        this.marshaller = DatexXml.builder().version(version).build();
    }

    /**
     * Creates a validator for the {@linkplain DatexVersion#current() current} DATEX II version.
     *
     * @return a new validator
     */
    public static DatexValidator create() {
        return new DatexValidator(DatexVersion.current());
    }

    /**
     * Creates a validator for a specific DATEX II version.
     *
     * @param version the DATEX II version to validate against
     * @return a new validator
     */
    public static DatexValidator forVersion(DatexVersion version) {
        return new DatexValidator(version);
    }

    /**
     * Validates a DATEX II XML document held in a byte array.
     *
     * @param xml the XML document
     * @return the validation result
     * @throws DatexValidationException if the document cannot be read
     */
    public ValidationResult validate(byte[] xml) {
        return validate(new StreamSource(new ByteArrayInputStream(xml)));
    }

    /**
     * Validates a DATEX II XML document read from a stream. The stream is not closed.
     *
     * @param in the source stream
     * @return the validation result
     * @throws DatexValidationException if the document cannot be read
     */
    public ValidationResult validate(InputStream in) {
        return validate(new StreamSource(in));
    }

    /**
     * Validates a DATEX II object by serializing it and validating the resulting XML.
     *
     * @param publication the object to validate; see {@link DatexMarshaller} for accepted types
     * @return the validation result
     * @throws DatexValidationException if the document cannot be read
     */
    public ValidationResult validate(Object publication) {
        return validate(marshaller.write(publication));
    }

    private ValidationResult validate(Source source) {
        Validator validator = schema.newValidator();
        try {
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (SAXException ignored) {
            // Property not supported by this implementation; safe to continue.
        }

        CollectingErrorHandler handler = new CollectingErrorHandler();
        validator.setErrorHandler(handler);
        try {
            validator.validate(source);
        } catch (SAXException e) {
            // A fatal error was already reported to the handler before the parser gave up.
            handler.recordFatalIfEmpty(e);
        } catch (IOException e) {
            throw new DatexValidationException("Failed to read the document to validate", e);
        }
        return new ValidationResult(handler.messages());
    }

    private static final class CollectingErrorHandler implements ErrorHandler {

        private final List<ValidationMessage> messages = new ArrayList<>();

        @Override
        public void warning(SAXParseException exception) {
            add(Severity.WARNING, exception);
        }

        @Override
        public void error(SAXParseException exception) {
            add(Severity.ERROR, exception);
        }

        @Override
        public void fatalError(SAXParseException exception) {
            add(Severity.FATAL, exception);
        }

        private void add(Severity severity, SAXParseException exception) {
            messages.add(new ValidationMessage(
                    severity, exception.getMessage(), exception.getLineNumber(), exception.getColumnNumber()));
        }

        void recordFatalIfEmpty(SAXException exception) {
            if (messages.isEmpty()) {
                int line = -1;
                int column = -1;
                if (exception instanceof SAXParseException parse) {
                    line = parse.getLineNumber();
                    column = parse.getColumnNumber();
                }
                messages.add(new ValidationMessage(Severity.FATAL, exception.getMessage(), line, column));
            }
        }

        List<ValidationMessage> messages() {
            return messages;
        }
    }
}
