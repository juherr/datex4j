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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reads, writes and (optionally) validates DATEX II XML, hiding all JAXB details.
 *
 * <p>Instances are obtained from {@link DatexXml} and are immutable and thread-safe, so a single
 * marshaller can be shared across an application:
 *
 * <pre>{@code
 * DatexMarshaller marshaller = DatexXml.createMarshaller();
 *
 * byte[] xml = marshaller.write(publication);
 * SituationPublication value = marshaller.read(xml, SituationPublication.class);
 * }</pre>
 *
 * <p>The value passed to {@code write} may be any DATEX II publication (a subtype of {@code
 * PayloadPublication}), which is automatically wrapped in the DATEX II {@code payload} root
 * element, a type annotated with {@code @XmlRootElement}, or a {@code
 * jakarta.xml.bind.JAXBElement}.
 */
public interface DatexMarshaller {

    /**
     * Serializes a DATEX II value to a byte array.
     *
     * @param value the value to serialize; see the {@linkplain DatexMarshaller class documentation}
     *     for accepted types
     * @return the serialized XML, encoded with this marshaller's charset
     * @throws DatexXmlException if serialization or (when enabled) validation fails
     * @throws IllegalArgumentException if {@code value} cannot be marshalled
     */
    byte[] write(Object value);

    /**
     * Serializes a DATEX II value to an output stream. The stream is not closed.
     *
     * @param value the value to serialize
     * @param out the destination stream
     * @throws DatexXmlException if serialization or (when enabled) validation fails
     * @throws IllegalArgumentException if {@code value} cannot be marshalled
     */
    void write(Object value, OutputStream out);

    /**
     * Serializes a DATEX II value to a string.
     *
     * @param value the value to serialize
     * @return the serialized XML
     * @throws DatexXmlException if serialization or (when enabled) validation fails
     * @throws IllegalArgumentException if {@code value} cannot be marshalled
     */
    String writeToString(Object value);

    /**
     * Deserializes DATEX II XML from a byte array.
     *
     * @param xml the XML document
     * @param type the expected value type
     * @param <T> the expected value type
     * @return the deserialized value
     * @throws DatexXmlException if parsing or (when enabled) validation fails
     * @throws ClassCastException if the document does not contain the expected type
     */
    <T> T read(byte[] xml, Class<T> type);

    /**
     * Deserializes DATEX II XML from an input stream. The stream is not closed.
     *
     * @param in the source stream
     * @param type the expected value type
     * @param <T> the expected value type
     * @return the deserialized value
     * @throws DatexXmlException if parsing or (when enabled) validation fails
     * @throws ClassCastException if the document does not contain the expected type
     */
    <T> T read(InputStream in, Class<T> type);
}
