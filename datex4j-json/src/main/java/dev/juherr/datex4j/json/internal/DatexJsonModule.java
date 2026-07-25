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
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;
import dev.juherr.datex4j.core.DatexVersion;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jackson module implementing the DATEX II conformant JSON rules on top of the base Jakarta XML
 * Binding element-name mapping and the temporal module.
 *
 * <p>The module layers four conformant-JSON transformations, driven entirely by the shape of the
 * generated model (no per-type hardcoding):
 *
 * <ul>
 *   <li>the global identification attributes {@code id}/{@code version}/{@code modelBaseVersion}
 *       are suffixed with {@code G} (see {@link GAttributes});
 *   <li>properties whose declared (or element) type is an abstract DATEX II class are written and
 *       read as single-member {@code {prefix+ConcreteType:{...}}} substitution objects (see {@link
 *       SubstitutionJson});
 *   <li>the {@code MultilingualString} nesting is flattened to {@code {"values":[{lang,value}]}}
 *       (see {@link MultilingualStringJson});
 *   <li>every generated {@code _XxxEnum} wrapper is flattened to {@code {value,_extendedValue?}}
 *       (see {@link DatexEnumJson}).
 * </ul>
 *
 * <p>The module is version-aware: substitution member keys resolve to concrete classes in the
 * generated model package for the configured {@link DatexVersion}.
 */
public final class DatexJsonModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    private static final String MODEL_PACKAGE_PREFIX = "dev.juherr.datex4j.model.";

    private final transient String versionSegment;

    /**
     * Creates the module for a specific DATEX II model version.
     *
     * @param version the DATEX II model version whose generated classes are (de)serialized
     */
    public DatexJsonModule(DatexVersion version) {
        super("DatexJsonModule-" + version.packageSegment());
        this.versionSegment = version.packageSegment();
        registerMultilingualString(version);
        setSerializerModifier(new DatexSerializerModifier());
        setDeserializerModifier(new DatexDeserializerModifier(versionSegment));
    }

    @SuppressWarnings("unchecked")
    private void registerMultilingualString(DatexVersion version) {
        String commonPackage = MODEL_PACKAGE_PREFIX + version.packageSegment() + ".common.";
        Class<?> stringType = loadModelClass(commonPackage + "MultilingualString", version);
        Class<?> valueType = loadModelClass(commonPackage + "MultilingualStringValue", version);
        addSerializer((Class<Object>) stringType, new MultilingualStringJson.Serializer());
        addDeserializer((Class<Object>) stringType, new MultilingualStringJson.Deserializer<>(stringType, valueType));
    }

    private static Class<?> loadModelClass(String className, DatexVersion version) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = DatexJsonModule.class.getClassLoader();
        }
        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "DATEX II model "
                            + version
                            + " is not available; add dev.juherr.datex4j:datex4j-model-"
                            + version.packageSegment(),
                    e);
        }
    }

    private static boolean isAbstractModelClass(Class<?> raw) {
        return raw != null
                && Modifier.isAbstract(raw.getModifiers())
                && !raw.isInterface()
                && raw.getName().startsWith(MODEL_PACKAGE_PREFIX);
    }

    private static boolean isEnumWrapper(Class<?> raw) {
        if (raw == null) {
            return false;
        }
        String simpleName = raw.getSimpleName();
        if (!simpleName.startsWith("_") || !simpleName.endsWith("Enum")) {
            return false;
        }
        return hasMethod(raw, "getValue") && hasMethod(raw, "get_ExtendedValue");
    }

    private static boolean hasMethod(Class<?> raw, String name) {
        try {
            raw.getMethod(name);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static JavaType elementType(JavaType type) {
        return type.isContainerType() ? type.getContentType() : type;
    }

    /** Applies {@code G}-suffix renaming and substitution wrapping on the serialization side. */
    private static final class DatexSerializerModifier extends BeanSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(
                SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            List<BeanPropertyWriter> result = new ArrayList<>(beanProperties.size());
            for (BeanPropertyWriter writer : beanProperties) {
                BeanPropertyWriter modified = writer;

                String jsonName = GAttributes.jsonName(writer.getName());
                if (!jsonName.equals(writer.getName())) {
                    modified = modified.rename(NameTransformer.simpleTransformer(null, "G"));
                }

                if (isAbstractModelClass(elementType(modified.getType()).getRawClass())) {
                    modified.assignSerializer(new SubstitutionSerializer());
                }
                result.add(modified);
            }
            return result;
        }

        @Override
        public JsonSerializer<?> modifySerializer(
                SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            if (isEnumWrapper(beanDesc.getBeanClass())) {
                return new DatexEnumJson.Serializer();
            }
            return serializer;
        }
    }

    /** Applies {@code G}-suffix renaming, substitution unwrapping, and enum decoding when reading. */
    private static final class DatexDeserializerModifier extends BeanDeserializerModifier {
        private final String versionSegment;

        DatexDeserializerModifier(String versionSegment) {
            this.versionSegment = versionSegment;
        }

        @Override
        public BeanDeserializerBuilder updateBuilder(
                DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
            List<SettableBeanProperty> properties = new ArrayList<>();
            builder.getProperties().forEachRemaining(properties::add);
            for (SettableBeanProperty property : properties) {
                String jsonName = GAttributes.jsonName(property.getName());
                if (!jsonName.equals(property.getName())) {
                    builder.removeProperty(PropertyName.construct(property.getName()));
                    builder.addOrReplaceProperty(property.withName(PropertyName.construct(jsonName)), true);
                }
            }
            return builder;
        }

        @Override
        public JsonDeserializer<?> modifyDeserializer(
                DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            Class<?> raw = beanDesc.getBeanClass();
            if (isEnumWrapper(raw)) {
                return new EnumWrapperDeserializer(raw);
            }
            if (isAbstractModelClass(raw)) {
                return new SubstitutionDeserializer(raw, versionSegment);
            }
            return deserializer;
        }
    }

    /** Wraps a concrete substitution value (or each element of a collection) in its member key. */
    private static final class SubstitutionSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            if (value instanceof Collection<?> collection) {
                generator.writeStartArray();
                for (Object element : collection) {
                    writeMember(element, generator, serializers);
                }
                generator.writeEndArray();
            } else {
                writeMember(value, generator, serializers);
            }
        }

        private void writeMember(Object value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                generator.writeNull();
                return;
            }
            generator.writeStartObject();
            generator.writeFieldName(SubstitutionJson.memberKey(value));
            serializers.findValueSerializer(value.getClass(), null).serialize(value, generator, serializers);
            generator.writeEndObject();
        }
    }

    /** Reads a single-member {@code {memberKey:{...}}} object into the concrete substitution value. */
    private static final class SubstitutionDeserializer extends JsonDeserializer<Object> {
        private final Class<?> baseClass;
        private final String versionSegment;

        SubstitutionDeserializer(Class<?> baseClass, String versionSegment) {
            this.baseClass = baseClass;
            this.versionSegment = versionSegment;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.readValueAsTree();
            if (node == null || !node.isObject()) {
                return null;
            }
            for (Iterator<String> names = node.fieldNames(); names.hasNext(); ) {
                String memberKey = names.next();
                Class<?> concrete;
                try {
                    concrete = SubstitutionJson.resolveClass(memberKey, versionSegment);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                if (!baseClass.isAssignableFrom(concrete)) {
                    continue;
                }
                return context.readTreeAsValue(node.get(memberKey), concrete);
            }
            return null;
        }
    }

    /** Decodes the flat {@code {value,_extendedValue?}} enum shape into a bound {@code _XxxEnum} wrapper. */
    private static final class EnumWrapperDeserializer extends JsonDeserializer<Object> {
        private static final Map<Class<?>, EnumMethods> CACHE = new ConcurrentHashMap<>();

        private final Class<?> wrapperType;

        EnumWrapperDeserializer(Class<?> wrapperType) {
            this.wrapperType = wrapperType;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.readValueAsTree();
            EnumMethods methods = CACHE.computeIfAbsent(wrapperType, EnumMethods::new);
            try {
                Object wrapper = methods.constructor.newInstance();
                JsonNode valueNode = node.get("value");
                if (valueNode != null && !valueNode.isNull()) {
                    methods.setValue.invoke(wrapper, methods.fromValue.invoke(null, valueNode.asText()));
                }
                JsonNode extendedNode = node.get("_extendedValue");
                if (extendedNode != null && !extendedNode.isNull()) {
                    methods.setExtendedValue.invoke(wrapper, extendedNode.asText());
                }
                return wrapper;
            } catch (ReflectiveOperationException e) {
                throw new IOException("Failed to deserialize DATEX II enum wrapper " + wrapperType, e);
            }
        }
    }

    /** Cached reflection handles for a single {@code _XxxEnum} wrapper type. */
    private static final class EnumMethods {
        private final java.lang.reflect.Constructor<?> constructor;
        private final Method setValue;
        private final Method fromValue;
        private final Method setExtendedValue;

        EnumMethods(Class<?> wrapperType) {
            try {
                this.constructor = wrapperType.getDeclaredConstructor();
                Method getValue = wrapperType.getMethod("getValue");
                Class<?> enumType = getValue.getReturnType();
                this.setValue = wrapperType.getMethod("setValue", enumType);
                this.fromValue = enumType.getMethod("fromValue", String.class);
                this.setExtendedValue = wrapperType.getMethod("set_ExtendedValue", String.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Not a DATEX II enum wrapper: " + wrapperType, e);
            }
        }
    }
}
