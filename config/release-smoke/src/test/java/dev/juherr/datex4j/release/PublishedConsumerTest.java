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
package dev.juherr.datex4j.release;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.spi.DatexModelProvider;
import dev.juherr.datex4j.xml.DatexXml;
import java.util.List;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;

class PublishedConsumerTest {

    @Test
    void resolvesAndRunsWithOneSelectedModel() {
        List<DatexVersion> versions = ServiceLoader.load(DatexModelProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .map(DatexModelProvider::version)
                .toList();

        assertEquals(List.of(DatexVersion.V3_7), versions);
        assertDoesNotThrow(() -> DatexXml.builder().version(DatexVersion.V3_7).build());
    }
}
