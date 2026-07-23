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
package dev.juherr.datex4j.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class DatexVersionTest {

    @Test
    void currentIsV37() {
        assertSame(DatexVersion.V3_7, DatexVersion.current());
    }

    @Test
    void exposesCanonicalId() {
        assertEquals("3.7", DatexVersion.V3_7.id());
        assertEquals("3.7", DatexVersion.V3_7.toString());
    }

    @Test
    void resourceSegmentIsPrefixedWithV() {
        assertEquals("v3.7", DatexVersion.V3_7.resourceSegment());
    }
}
