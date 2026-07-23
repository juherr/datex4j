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

import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;

class TemporalsTest {

    @Test
    void toXmlDateTimeRoundTripsThroughToIso() {
        XMLGregorianCalendar cal = Temporals.toXmlDateTime("2026-07-23T10:15:30Z");

        assertThat(cal).isNotNull();
        assertThat(Temporals.toIso(cal)).isEqualTo("2026-07-23T10:15:30Z");
    }

    @Test
    void toXmlDateTimeReturnsNullForNull() {
        assertThat(Temporals.toXmlDateTime(null)).isNull();
    }

    @Test
    void toXmlDateTimeReturnsNullForBlank() {
        assertThat(Temporals.toXmlDateTime("   ")).isNull();
    }

    @Test
    void toXmlDateTimeReturnsNullForUnparseableInput() {
        assertThat(Temporals.toXmlDateTime("not-a-date")).isNull();
    }

    @Test
    void toIsoReturnsNullForNull() {
        assertThat(Temporals.toIso(null)).isNull();
    }

    @Test
    void toXmlTimeRoundTripsThroughToHhmm() {
        XMLGregorianCalendar cal = Temporals.toXmlTime("08:05");

        assertThat(cal).isNotNull();
        assertThat(Temporals.toHhmm(cal)).isEqualTo("08:05");
    }

    @Test
    void toXmlTimeReturnsNullForNull() {
        assertThat(Temporals.toXmlTime(null)).isNull();
    }

    @Test
    void toXmlTimeReturnsNullForBlank() {
        assertThat(Temporals.toXmlTime("   ")).isNull();
    }

    @Test
    void toXmlTimeReturnsNullForUnparseableInput() {
        assertThat(Temporals.toXmlTime("not-a-time")).isNull();
    }

    @Test
    void toXmlTimeReturnsNullForOutOfRangeInput() {
        assertThat(Temporals.toXmlTime("24:00")).isNull();
        assertThat(Temporals.toXmlTime("08:60")).isNull();
        assertThat(Temporals.toXmlTime("-1:00")).isNull();
    }

    @Test
    void toHhmmReturnsNullForNull() {
        assertThat(Temporals.toHhmm(null)).isNull();
    }
}
