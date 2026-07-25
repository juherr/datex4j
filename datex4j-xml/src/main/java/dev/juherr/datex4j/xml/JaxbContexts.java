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

import dev.juherr.datex4j.model.spi.DatexModelProvider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Classloader-safe cache of immutable JAXB contexts. */
final class JaxbContexts {

    private static final ClassValue<ConcurrentMap<String, JAXBContext>> CACHE = new ClassValue<>() {
        @Override
        protected ConcurrentMap<String, JAXBContext> computeValue(Class<?> type) {
            return new ConcurrentHashMap<>();
        }
    };

    private JaxbContexts() {}

    static JAXBContext get(DatexModelProvider model) {
        return CACHE.get(model.getClass())
                .computeIfAbsent(
                        model.contextPath(),
                        contextPath -> create(contextPath, model.getClass().getClassLoader()));
    }

    private static JAXBContext create(String contextPath, ClassLoader loader) {
        try {
            return JAXBContext.newInstance(contextPath, loader);
        } catch (JAXBException e) {
            throw new DatexXmlException("Failed to initialize the DATEX II JAXB context", e);
        }
    }
}
