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
package dev.juherr.datex4j.xml;

import dev.juherr.datex4j.core.DatexVersion;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.xml.validation.Schema;

/**
 * Default {@link DatexMarshaller} implementation backed by JAXB.
 *
 * <p>A single {@link JAXBContext} is built per instance and shared across marshalling operations;
 * fresh {@code Marshaller}/{@code Unmarshaller} objects are created per call because those are not
 * thread-safe. Instances of this class are therefore immutable and thread-safe.
 */
final class JaxbDatexMarshaller implements DatexMarshaller {

    private final VersionModel model;
    private final JAXBContext context;
    private final Schema schema;
    private final boolean prettyPrint;
    private final Charset charset;

    JaxbDatexMarshaller(DatexVersion version, boolean prettyPrint, boolean validating, Charset charset) {
        this.prettyPrint = prettyPrint;
        this.charset = charset;
        this.model = VersionModel.of(version);
        try {
            this.context =
                    JAXBContext.newInstance(model.contextPath(), getClass().getClassLoader());
        } catch (JAXBException e) {
            throw new DatexXmlException("Failed to initialize the DATEX II JAXB context", e);
        }
        this.schema = validating ? ClasspathSchemas.load(version) : null;
    }

    @Override
    public byte[] write(Object value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(value, out);
        return out.toByteArray();
    }

    @Override
    public void write(Object value, OutputStream out) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            if (schema != null) {
                marshaller.setSchema(schema);
            }
            marshaller.marshal(toMarshallable(value), out);
        } catch (JAXBException e) {
            throw new DatexXmlException("Failed to write DATEX II XML", e);
        }
    }

    @Override
    public String writeToString(Object value) {
        return new String(write(value), charset);
    }

    @Override
    public <T> T read(byte[] xml, Class<T> type) {
        if (xml == null) {
            throw new IllegalArgumentException("xml must not be null");
        }
        return read(new ByteArrayInputStream(xml), type);
    }

    @Override
    public <T> T read(InputStream in, Class<T> type) {
        if (in == null) {
            throw new IllegalArgumentException("in must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            Object result = unmarshaller.unmarshal(in);
            if (result instanceof JAXBElement<?> element) {
                result = element.getValue();
            }
            return type.cast(result);
        } catch (JAXBException e) {
            throw new DatexXmlException("Failed to read DATEX II XML", e);
        }
    }

    /**
     * Adapts an arbitrary value into something JAXB can marshal: JAXB elements and
     * {@code @XmlRootElement} types are marshalled as-is, a bare {@code PayloadPublication} (of this
     * marshaller's version) is wrapped in the DATEX II {@code payload} root element, and a {@code
     * MessageContainer} (v3.6/v3.7 only) is wrapped in the {@code messageContainer} root element.
     */
    private Object toMarshallable(Object value) {
        if (value instanceof JAXBElement) {
            return value;
        }
        if (value.getClass().isAnnotationPresent(XmlRootElement.class)) {
            return value;
        }
        if (model.isPayloadPublication(value)) {
            return model.wrapAsPayload(value);
        }
        if (model.isMessageContainer(value)) {
            return model.wrapAsMessageContainer(value);
        }
        throw new IllegalArgumentException("Cannot marshal "
                + value.getClass().getName()
                + "; pass a PayloadPublication, a MessageContainer, an @XmlRootElement type, or a JAXBElement");
    }
}
