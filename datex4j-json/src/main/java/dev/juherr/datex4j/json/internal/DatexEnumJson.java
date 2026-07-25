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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Jackson serializer for the generated DATEX II {@code _XxxEnum} wrapper shape.
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
 * <p>The serializer operates purely through reflection over the {@code getValue}/{@code
 * get_ExtendedValue} methods, so a single instance covers every {@code _XxxEnum} wrapper type in
 * the generated model; callers register it per wrapper type. The corresponding deserialization
 * direction is handled by {@link DatexJsonModule}'s own contextual deserializer.
 */
final class DatexEnumJson {

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

    private static Throwable unwrap(ReflectiveOperationException e) {
        return e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
    }
}
