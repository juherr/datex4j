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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.json.internal.DatexJsonModule;
import dev.juherr.datex4j.json.internal.MessageContainerJson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Reads and writes DATEX II model objects as conformant DATEX II JSON, hiding Jackson. See {@link
 * DatexJson} for an overview of the conformant format, its best-effort/lossy guarantees, and its
 * standardisation status.
 *
 * <p>The mapper honours the model's Jakarta XML Binding annotations so property names match the
 * DATEX II model, and maps the XML temporal types via {@link DatexTemporalModule}. Instances are
 * immutable and thread-safe and may be shared across an application.
 *
 * <p>Any DATEX II object of any bundled version can be written; {@link #read} returns the
 * requested type. A {@code MessageContainer} value is the one exception: {@link #write} and {@link
 * #writeToString} emit the conformant {@code payload}/{@code exchangeInformation} envelope for it
 * (see {@link #readContainer}), while every other type is serialized directly with no wrapper.
 */
public final class DatexJsonMapper {

    private final ObjectMapper mapper;
    private final boolean prettyPrint;
    private final DatexVersion version;

    DatexJsonMapper(boolean prettyPrint, DatexVersion version) {
        this.prettyPrint = prettyPrint;
        this.version = version;
        this.mapper = new ObjectMapper()
                .registerModule(new JakartaXmlBindAnnotationModule())
                .registerModule(new DatexTemporalModule())
                .registerModule(new DatexJsonModule(version))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Serializes a DATEX II object to a JSON byte array.
     *
     * @param value the object to serialize
     * @return the JSON document, UTF-8 encoded
     * @throws DatexJsonException if serialization fails
     */
    public byte[] write(Object value) {
        try {
            if (isMessageContainer(value)) {
                return MessageContainerJson.write(value, mapper, prettyPrint);
            }
            return writer().writeValueAsBytes(value);
        } catch (IOException e) {
            throw new DatexJsonException("Failed to write DATEX II JSON", e);
        }
    }

    /**
     * Serializes a DATEX II object to a JSON string.
     *
     * @param value the object to serialize
     * @return the JSON document
     * @throws DatexJsonException if serialization fails
     */
    public String writeToString(Object value) {
        try {
            if (isMessageContainer(value)) {
                return new String(MessageContainerJson.write(value, mapper, prettyPrint), StandardCharsets.UTF_8);
            }
            return writer().writeValueAsString(value);
        } catch (IOException e) {
            throw new DatexJsonException("Failed to write DATEX II JSON", e);
        }
    }

    /**
     * Serializes a DATEX II object to an output stream. The stream is not closed.
     *
     * @param value the object to serialize
     * @param out the destination stream
     * @throws DatexJsonException if serialization fails
     */
    public void write(Object value, OutputStream out) {
        try {
            if (isMessageContainer(value)) {
                out.write(MessageContainerJson.write(value, mapper, prettyPrint));
                return;
            }
            writer().writeValue(out, value);
        } catch (IOException e) {
            throw new DatexJsonException("Failed to write DATEX II JSON", e);
        }
    }

    /**
     * Deserializes a DATEX II object from a JSON byte array.
     *
     * @param json the JSON document
     * @param type the expected type
     * @param <T> the expected type
     * @return the deserialized object
     * @throws DatexJsonException if parsing fails
     */
    public <T> T read(byte[] json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new DatexJsonException("Failed to read DATEX II JSON", e);
        }
    }

    /**
     * Deserializes a DATEX II object from a JSON input stream. The stream is not closed.
     *
     * @param in the source stream
     * @param type the expected type
     * @param <T> the expected type
     * @return the deserialized object
     * @throws DatexJsonException if parsing fails
     */
    public <T> T read(InputStream in, Class<T> type) {
        try {
            return mapper.readValue(in, type);
        } catch (IOException e) {
            throw new DatexJsonException("Failed to read DATEX II JSON", e);
        }
    }

    /**
     * Deserializes a conformant DATEX II JSON message container, restoring namespace-prefixed
     * fields, flattened multilingual strings, enum {@code {value, _extendedValue}} encodings,
     * {@code G}-suffixed attributes, and substitution prefix+type markers back into the model.
     *
     * @param json the conformant JSON document
     * @param type the expected container type
     * @param <T> the expected type
     * @return the deserialized message container
     * @throws DatexJsonException if parsing fails
     */
    public <T> T readContainer(byte[] json, Class<T> type) {
        try {
            return type.cast(MessageContainerJson.read(json, mapper, version.packageSegment()));
        } catch (IOException e) {
            throw new DatexJsonException("Failed to read DATEX II JSON", e);
        }
    }

    private boolean isMessageContainer(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> type = value.getClass();
        return "MessageContainer".equals(type.getSimpleName())
                && type.getName().startsWith("dev.juherr.datex4j.model.");
    }

    private ObjectWriter writer() {
        return prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
    }
}
