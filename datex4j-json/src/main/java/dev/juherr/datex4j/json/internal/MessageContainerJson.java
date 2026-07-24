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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

/**
 * Reads and writes the DATEX II conformant JSON root: {@code {"payload":[...],
 * "exchangeInformation":...}}.
 *
 * <p>The {@code payload} array is a substitution group of {@code PayloadPublication}. Unlike inner
 * substitution properties, the payload item hoists the versioning attributes ({@code idG}, {@code
 * versionG}, {@code modelBaseVersionG}) up as siblings of the single prefixed publication member,
 * for example:
 *
 * <pre>{@code {"modelBaseVersionG":"3","egiEnergyInfrastructureTablePublication":{...}}}</pre>
 *
 * <p>All nested encoding (multilingual strings, enums, inner substitution, {@code G}-suffixes) is
 * delegated to the shared {@link DatexJsonModule} registered on the supplied {@link ObjectMapper}.
 * This class is version-neutral: it locates the {@code MessageContainer} and {@code
 * ExchangeInformation} classes for the requested model version by reflection.
 */
public final class MessageContainerJson {

    private static final List<String> HOISTED_ATTRIBUTES = List.of("idG", "versionG", "modelBaseVersionG");

    private MessageContainerJson() {}

    /**
     * Serializes a DATEX II {@code MessageContainer} to the conformant JSON root as a byte array.
     *
     * @param container the message container (any bundled model version)
     * @param mapper the configured mapper carrying the {@link DatexJsonModule}
     * @param prettyPrint whether to indent the output
     * @return the conformant JSON document, UTF-8 encoded
     * @throws IOException if serialization fails
     */
    public static byte[] write(Object container, ObjectMapper mapper, boolean prettyPrint) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode payloadArray = root.putArray("payload");
        for (Object publication : payload(container)) {
            payloadArray.add(writePayloadItem(publication, mapper));
        }

        Object exchangeInformation = invoke(container, "getExchangeInformation");
        if (exchangeInformation != null) {
            root.set("exchangeInformation", mapper.valueToTree(exchangeInformation));
        }

        return (prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer()).writeValueAsBytes(root);
    }

    private static ObjectNode writePayloadItem(Object publication, ObjectMapper mapper) {
        ObjectNode publicationTree = (ObjectNode) mapper.valueToTree(publication);
        ObjectNode item = mapper.createObjectNode();
        for (String attribute : HOISTED_ATTRIBUTES) {
            JsonNode hoisted = publicationTree.remove(attribute);
            if (hoisted != null) {
                item.set(attribute, hoisted);
            }
        }
        item.set(SubstitutionJson.memberKey(publication), publicationTree);
        return item;
    }

    /**
     * Deserializes a conformant JSON root into a DATEX II {@code MessageContainer} for the requested
     * model version.
     *
     * @param json the conformant JSON document
     * @param mapper the configured mapper carrying the {@link DatexJsonModule}
     * @param versionSegment the model version package segment, for example {@code "v3_6"}
     * @return the populated message container
     * @throws IOException if parsing fails
     */
    public static Object read(byte[] json, ObjectMapper mapper, String versionSegment) throws IOException {
        JsonNode root = mapper.readTree(json);
        try {
            Class<?> containerClass = modelClass(versionSegment, "messagecontainer.MessageContainer");
            Object container = containerClass.getDeclaredConstructor().newInstance();

            JsonNode payloadNode = root.get("payload");
            if (payloadNode != null) {
                @SuppressWarnings("unchecked")
                List<Object> payloadList =
                        (List<Object>) containerClass.getMethod("getPayload").invoke(container);
                for (JsonNode item : payloadNode) {
                    Object publication = readPayloadItem(item, mapper, versionSegment);
                    if (publication != null) {
                        payloadList.add(publication);
                    }
                }
            }

            JsonNode exchangeNode = root.get("exchangeInformation");
            if (exchangeNode != null) {
                Class<?> exchangeClass = modelClass(versionSegment, "exchangeinformation.ExchangeInformation");
                Object exchange = mapper.treeToValue(exchangeNode, exchangeClass);
                containerClass
                        .getMethod("setExchangeInformation", exchangeClass)
                        .invoke(container, exchange);
            }

            return container;
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to read DATEX II message container", unwrap(e));
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new IOException(
                    "Failed to read DATEX II message container: malformed or unknown payload member (" + e.getMessage()
                            + ")",
                    e);
        }
    }

    private static Object readPayloadItem(JsonNode item, ObjectMapper mapper, String versionSegment)
            throws IOException {
        String memberKey = memberKeyOf(item);
        if (memberKey == null) {
            return null;
        }
        Class<?> concrete = SubstitutionJson.resolveClass(memberKey, versionSegment);
        ObjectNode publicationTree = ((ObjectNode) item.get(memberKey)).deepCopy();
        for (String attribute : HOISTED_ATTRIBUTES) {
            if (item.has(attribute)) {
                publicationTree.set(attribute, item.get(attribute));
            }
        }
        return mapper.treeToValue(publicationTree, concrete);
    }

    private static String memberKeyOf(JsonNode item) {
        for (Iterator<String> names = item.fieldNames(); names.hasNext(); ) {
            String name = names.next();
            if (!HOISTED_ATTRIBUTES.contains(name)) {
                return name;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> payload(Object container) {
        return (List<Object>) invoke(container, "getPayload");
    }

    private static Object invoke(Object target, String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Not a DATEX II message container: " + target.getClass(), unwrap(e));
        }
    }

    private static Class<?> modelClass(String versionSegment, String suffix) throws ClassNotFoundException {
        return Class.forName("dev.juherr.datex4j.model." + versionSegment + "." + suffix);
    }

    private static Throwable unwrap(ReflectiveOperationException e) {
        return e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
    }
}
