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
package dev.juherr.datex4j.json.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.juherr.datex4j.model.v3_6.common.MultilingualString;
import dev.juherr.datex4j.model.v3_6.common.MultilingualStringValue;
import java.io.IOException;
import java.util.List;

/**
 * DATEX II v3.6 counterpart of {@link MultilingualStringJson}, flattening the v3.6 {@link
 * MultilingualString} model into its conformant {@code {"values":[{lang,value}]}} JSON encoding.
 *
 * <p>The v3.6 and v3.7 {@code MultilingualString} classes are distinct generated types with
 * identical shape; the logic mirrors {@link MultilingualStringJson} exactly.
 */
public final class MultilingualString36Json {

    private MultilingualString36Json() {}

    /** Serializes a v3.6 {@link MultilingualString} to the flat {@code {"values":[...]}} JSON shape. */
    public static final class Serializer extends JsonSerializer<MultilingualString> {
        @Override
        public void serialize(MultilingualString value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            generator.writeStartObject();
            generator.writeArrayFieldStart("values");
            List<MultilingualStringValue> values =
                    value.getValues() == null ? List.of() : value.getValues().getValue();
            for (MultilingualStringValue msv : values) {
                generator.writeStartObject();
                generator.writeStringField("lang", msv.getLang());
                generator.writeStringField("value", msv.getValue());
                generator.writeEndObject();
            }
            generator.writeEndArray();
            generator.writeEndObject();
        }
    }

    /** Deserializes the flat {@code {"values":[...]}} JSON shape back into a v3.6 {@link MultilingualString}. */
    public static final class Deserializer extends JsonDeserializer<MultilingualString> {
        @Override
        public MultilingualString deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode root = parser.getCodec().readTree(parser);
            MultilingualString.Values values = new MultilingualString.Values();
            JsonNode valuesNode = root.get("values");
            if (valuesNode != null) {
                for (JsonNode item : valuesNode) {
                    MultilingualStringValue msv = new MultilingualStringValue();
                    JsonNode lang = item.get("lang");
                    if (lang != null) {
                        msv.setLang(lang.asText());
                    }
                    JsonNode text = item.get("value");
                    if (text != null) {
                        msv.setValue(text.asText());
                    }
                    values.getValue().add(msv);
                }
            }
            MultilingualString result = new MultilingualString();
            result.setValues(values);
            return result;
        }
    }
}
