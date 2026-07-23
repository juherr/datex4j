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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Jackson module teaching the DATEX II model's XML temporal types to serialize as ISO-8601 strings.
 *
 * <p>The generated model uses {@link XMLGregorianCalendar} (for {@code xs:dateTime}, {@code xs:date}
 * …) and {@link Duration} (for {@code xs:duration}). Jackson has no built-in support for them and
 * would otherwise expose their internal bean properties, so this module maps them to and from their
 * canonical lexical form.
 */
final class DatexTemporalModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    private static final DatatypeFactory DATATYPE_FACTORY = newDatatypeFactory();

    DatexTemporalModule() {
        addSerializer(XMLGregorianCalendar.class, new XmlGregorianCalendarSerializer());
        addDeserializer(XMLGregorianCalendar.class, new XmlGregorianCalendarDeserializer());
        addSerializer(Duration.class, new DurationSerializer());
        addDeserializer(Duration.class, new DurationDeserializer());
    }

    private static DatatypeFactory newDatatypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("No XML datatype factory available", e);
        }
    }

    private static final class XmlGregorianCalendarSerializer extends JsonSerializer<XMLGregorianCalendar> {
        @Override
        public void serialize(XMLGregorianCalendar value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            generator.writeString(value.toXMLFormat());
        }
    }

    private static final class XmlGregorianCalendarDeserializer extends JsonDeserializer<XMLGregorianCalendar> {
        @Override
        public XMLGregorianCalendar deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String text = parser.getValueAsString();
            return text == null ? null : DATATYPE_FACTORY.newXMLGregorianCalendar(text);
        }
    }

    private static final class DurationSerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            generator.writeString(value.toString());
        }
    }

    private static final class DurationDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String text = parser.getValueAsString();
            return text == null ? null : DATATYPE_FACTORY.newDuration(text);
        }
    }
}
