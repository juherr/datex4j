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
package dev.juherr.datex4j.ocpi.mapping.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import org.junit.jupiter.api.Test;

class MultilingualStringsTest {

    @Test
    void ofBuildsSingleValueWithLang() {
        MultilingualString s = MultilingualStrings.of("en", "Main Street Hub");
        assertThat(s.getValues().getValue()).hasSize(1);
        assertThat(s.getValues().getValue().get(0).getLang()).isEqualTo("en");
        assertThat(s.getValues().getValue().get(0).getValue()).isEqualTo("Main Street Hub");
    }

    @Test
    void firstValueReadsBackTheText() {
        MultilingualString s = MultilingualStrings.of("en", "Main Street Hub");
        assertThat(MultilingualStrings.firstValue(s)).isEqualTo("Main Street Hub");
    }

    @Test
    void ofReturnsNullForNullText() {
        assertThat(MultilingualStrings.of("en", null)).isNull();
    }

    @Test
    void firstValueReturnsNullForNull() {
        assertThat(MultilingualStrings.firstValue(null)).isNull();
    }
}
