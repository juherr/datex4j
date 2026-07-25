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
package dev.juherr.datex4j.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.json.DatexJson;
import dev.juherr.datex4j.model.spi.DatexModelProvider;
import dev.juherr.datex4j.xml.DatexXml;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class SingleModelClasspathTest {

    @Test
    void exposesOnlyTheModelExplicitlySelectedByTheConsumer() {
        Iterable<DatexModelProvider> providers = ServiceLoader.load(DatexModelProvider.class);

        assertThat(StreamSupport.stream(providers.spliterator(), false).map(DatexModelProvider::version))
                .containsExactly(DatexVersion.V3_7);
    }

    @Test
    void createsXmlAndJsonFacadesForTheInstalledModel() {
        assertThatCode(() -> DatexXml.builder().version(DatexVersion.V3_7).build())
                .doesNotThrowAnyException();
        assertThatCode(() -> DatexJson.builder().version(DatexVersion.V3_7).build())
                .doesNotThrowAnyException();
    }

    @Test
    void reportsAUsefulErrorForAModelThatIsNotInstalled() {
        assertThatThrownBy(() -> DatexXml.builder().version(DatexVersion.V3_6).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("datex4j-model-v3_6");
        assertThatThrownBy(() -> DatexJson.builder().version(DatexVersion.V3_6).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("datex4j-model-v3_6");
    }
}
