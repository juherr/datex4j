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
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Jackson (de)serializers for the generated DATEX II {@code _XxxEnum} wrapper shape.
 *
 * <p>Every DATEX II enumeration is modelled by JAXB as two generated types: a plain Java {@code
 * enum} (say {@code AddressLineTypeEnum}, with instance method {@code value()} returning the XML
 * enumeration literal and a static {@code fromValue(String)} factory), and a wrapper complex type
 * (say {@code _AddressLineTypeEnum}, with {@code getValue()}/{@code setValue(...)} for the plain
 * enum and {@code get_ExtendedValue()}/{@code set_ExtendedValue(String)} for the DATEX II {@code
 * _extended} escape hatch). The conformant DATEX II JSON encoding flattens the wrapper to:
 *
 * <pre>{@code {"value":"street"}}</pre>
 *
 * <p>or, when the extended escape hatch is used:
 *
 * <pre>{@code {"value":"_extended","_extendedValue":"myCustom"}}</pre>
 *
 * <p>Both (de)serializers operate purely through reflection over the {@code getValue}/{@code
 * setValue}/{@code get_ExtendedValue}/{@code set_ExtendedValue} methods, so a single pair of
 * instances covers every {@code _XxxEnum} wrapper type in the generated model; callers register
 * them per wrapper type (directly, or via a {@code BeanSerializerModifier}/{@code
 * BeanDeserializerModifier} that detects the wrapper shape).
 */
public final class DatexEnumJson {

    private DatexEnumJson() {}

    /** Serializes any {@code _XxxEnum} wrapper instance to the flat {@code {value,_extendedValue?}} shape. */
    public static final class Serializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object wrapper, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            if (wrapper == null) {
                generator.writeNull();
                return;
            }
            try {
                Class<?> wrapperType = wrapper.getClass();
                Object enumValue = wrapperType.getMethod("getValue").invoke(wrapper);
                String extendedValue =
                        (String) wrapperType.getMethod("get_ExtendedValue").invoke(wrapper);

                generator.writeStartObject();
                if (enumValue != null) {
                    String xmlValue =
                            (String) enumValue.getClass().getMethod("value").invoke(enumValue);
                    generator.writeStringField("value", xmlValue);
                }
                if (extendedValue != null) {
                    generator.writeStringField("_extendedValue", extendedValue);
                }
                generator.writeEndObject();
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to serialize DATEX II enum wrapper " + wrapper.getClass(), unwrap(e));
            }
        }
    }

    /**
     * Deserializes the flat {@code {value,_extendedValue?}} shape back into any {@code _XxxEnum}
     * wrapper type, discovered contextually from the target property/type being deserialized.
     */
    public static final class Deserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
        private final Class<?> wrapperType;

        public Deserializer() {
            this(null);
        }

        private Deserializer(Class<?> wrapperType) {
            this.wrapperType = wrapperType;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
            JavaType type = property != null ? property.getType() : context.getContextualType();
            return new Deserializer(type != null ? type.getRawClass() : null);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (wrapperType == null) {
                throw new IOException("DatexEnumJson.Deserializer needs a contextual target type; "
                        + "register it via a module that resolves the _XxxEnum wrapper class first");
            }
            JsonNode root = parser.getCodec().readTree(parser);
            try {
                Object wrapper = wrapperType.getDeclaredConstructor().newInstance();
                Method getValue = wrapperType.getMethod("getValue");
                Class<?> enumType = getValue.getReturnType();
                Method setValue = wrapperType.getMethod("setValue", enumType);
                Method fromValue = enumType.getMethod("fromValue", String.class);

                JsonNode valueNode = root.get("value");
                if (valueNode != null && !valueNode.isNull()) {
                    setValue.invoke(wrapper, fromValue.invoke(null, valueNode.asText()));
                }

                JsonNode extendedValueNode = root.get("_extendedValue");
                if (extendedValueNode != null && !extendedValueNode.isNull()) {
                    Method setExtendedValue = wrapperType.getMethod("set_ExtendedValue", String.class);
                    setExtendedValue.invoke(wrapper, extendedValueNode.asText());
                }

                return wrapper;
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to deserialize DATEX II enum wrapper " + wrapperType, unwrap(e));
            }
        }
    }

    private static Throwable unwrap(ReflectiveOperationException e) {
        return e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
    }
}
