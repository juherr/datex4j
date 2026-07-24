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
package dev.juherr.datex4j.model.spi;

import dev.juherr.datex4j.core.DatexVersion;
import jakarta.xml.bind.JAXBElement;

/**
 * Bridge to the generated DATEX II model of a single {@link DatexVersion}.
 *
 * <p>Each supported version is generated into its own package tree ({@code
 * dev.juherr.datex4j.model.v3_6.*}, {@code ...v3_7.*}), so the {@code PayloadPublication} base type
 * and the {@code payload} {@code ObjectFactory} differ per version. A provider encapsulates those
 * differences: the JAXB context path, how to recognize a publication, and how to wrap it in the
 * {@code payload} root element.
 *
 * <p>Every {@code datex4j-model-vX_Y} module ships exactly one implementation, registered through
 * {@link java.util.ServiceLoader} so that facades such as {@code datex4j-xml} discover the versions
 * present on the classpath without a hard-coded reference to any generated class.
 */
public interface DatexModelProvider {

    /** Returns the DATEX II version backed by this provider. */
    DatexVersion version();

    /** Returns the colon-separated JAXB context path for this version's model packages. */
    String contextPath();

    /** Tells whether the value is a {@code PayloadPublication} of this version's model. */
    boolean isPayloadPublication(Object value);

    /** Wraps a {@code PayloadPublication} of this version in its {@code payload} root element. */
    JAXBElement<?> wrapAsPayload(Object value);

    /**
     * Tells whether the value is a {@code MessageContainer} of this version's model. Only DATEX II
     * v3.6 and v3.7 ship the Exchange 2020 {@code MessageContainer} family; earlier versions return
     * {@code false}.
     */
    default boolean isMessageContainer(Object value) {
        return false;
    }

    /**
     * Wraps a {@code MessageContainer} of this version in its {@code messageContainer} root element.
     * Only supported for DATEX II v3.6 and v3.7.
     */
    default JAXBElement<?> wrapAsMessageContainer(Object value) {
        throw new IllegalArgumentException("MessageContainer is not part of the DATEX II " + version() + " model");
    }
}
