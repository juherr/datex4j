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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Version-neutral Jackson (de)serializers flattening a generated DATEX II
 * {@code MultilingualString} into its conformant JSON encoding.
 *
 * <p>The generated JAXB model nests the language/value pairs under an intermediate {@code Values}
 * wrapper ({@code getValues().getValue()}), mirroring the XML schema's anonymous wrapper element.
 * The conformant DATEX II JSON encoding flattens that wrapper away, exposing a single {@code
 * values} array of {@code {lang, value}} objects directly on the generated string:
 *
 * <pre>{@code {"values":[{"lang":"fi","value":"Kärkitie 4"}]}}</pre>
 */
public final class MultilingualStringJson {

    private MultilingualStringJson() {}

    /** Serializes any generated multilingual string to the flat {@code {"values":[...]}} shape. */
    public static final class Serializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            try {
                Method getValues = value.getClass().getMethod("getValues");
                Object wrapper = getValues.invoke(value);
                List<?> values = wrapper == null
                        ? List.of()
                        : (List<?>) wrapper.getClass().getMethod("getValue").invoke(wrapper);

                generator.writeStartObject();
                generator.writeArrayFieldStart("values");
                for (Object item : values) {
                    generator.writeStartObject();
                    generator.writeStringField("lang", (String)
                            item.getClass().getMethod("getLang").invoke(item));
                    generator.writeStringField("value", (String)
                            item.getClass().getMethod("getValue").invoke(item));
                    generator.writeEndObject();
                }
                generator.writeEndArray();
                generator.writeEndObject();
            } catch (ReflectiveOperationException e) {
                throw new IOException(
                        "Failed to serialize DATEX II multilingual string "
                                + value.getClass().getName(),
                        e);
            }
        }
    }

    /** Deserializes the flat JSON shape into one version-specific generated string type. */
    public static final class Deserializer<T> extends JsonDeserializer<T> {
        private final Constructor<?> stringConstructor;
        private final Constructor<?> valuesConstructor;
        private final Constructor<?> valueConstructor;
        private final Method setValues;
        private final Method values;
        private final Method setLang;
        private final Method setValue;

        /** Creates a deserializer for the supplied generated multilingual string class. */
        public Deserializer(Class<?> stringType, Class<?> valueType) {
            try {
                Class<?> valuesType =
                        Class.forName(stringType.getName() + "$Values", true, stringType.getClassLoader());
                this.stringConstructor = stringType.getDeclaredConstructor();
                this.valuesConstructor = valuesType.getDeclaredConstructor();
                this.valueConstructor = valueType.getDeclaredConstructor();
                this.setValues = stringType.getMethod("setValues", valuesType);
                this.values = valuesType.getMethod("getValue");
                this.setLang = valueType.getMethod("setLang", String.class);
                this.setValue = valueType.getMethod("setValue", String.class);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(
                        "Unsupported DATEX II multilingual string type " + stringType.getName(), e);
            }
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode root = parser.getCodec().readTree(parser);
            try {
                Object wrapper = valuesConstructor.newInstance();
                @SuppressWarnings("unchecked")
                List<Object> generatedValues = (List<Object>) values.invoke(wrapper);
                JsonNode valuesNode = root.get("values");
                if (valuesNode != null) {
                    for (JsonNode item : valuesNode) {
                        Object generatedValue = valueConstructor.newInstance();
                        JsonNode lang = item.get("lang");
                        if (lang != null) {
                            setLang.invoke(generatedValue, lang.asText());
                        }
                        JsonNode text = item.get("value");
                        if (text != null) {
                            setValue.invoke(generatedValue, text.asText());
                        }
                        generatedValues.add(generatedValue);
                    }
                }
                Object result = stringConstructor.newInstance();
                setValues.invoke(result, wrapper);
                @SuppressWarnings("unchecked")
                T typedResult = (T) result;
                return typedResult;
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to deserialize DATEX II multilingual string", e);
            }
        }
    }
}
