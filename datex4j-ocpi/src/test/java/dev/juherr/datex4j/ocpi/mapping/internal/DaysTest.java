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

import dev.juherr.datex4j.model.v3_7.common.DayEnum;
import dev.juherr.datex4j.model.v3_7.common._DayEnum;
import org.junit.jupiter.api.Test;

class DaysTest {

    @Test
    void toDatexMapsMondayThroughSunday() {
        assertThat(Days.toDatex(1).getValue()).isEqualTo(DayEnum.MONDAY);
        assertThat(Days.toDatex(2).getValue()).isEqualTo(DayEnum.TUESDAY);
        assertThat(Days.toDatex(3).getValue()).isEqualTo(DayEnum.WEDNESDAY);
        assertThat(Days.toDatex(4).getValue()).isEqualTo(DayEnum.THURSDAY);
        assertThat(Days.toDatex(5).getValue()).isEqualTo(DayEnum.FRIDAY);
        assertThat(Days.toDatex(6).getValue()).isEqualTo(DayEnum.SATURDAY);
        assertThat(Days.toDatex(7).getValue()).isEqualTo(DayEnum.SUNDAY);
    }

    @Test
    void toDatexReturnsNullForNull() {
        assertThat(Days.toDatex(null)).isNull();
    }

    @Test
    void toDatexReturnsNullForOutOfRangeWeekday() {
        assertThat(Days.toDatex(0)).isNull();
        assertThat(Days.toDatex(8)).isNull();
    }

    @Test
    void toOcpiRoundTripsAllDays() {
        for (int weekday = 1; weekday <= 7; weekday++) {
            assertThat(Days.toOcpi(Days.toDatex(weekday))).isEqualTo(weekday);
        }
    }

    @Test
    void toOcpiReturnsNullForNull() {
        assertThat(Days.toOcpi(null)).isNull();
    }

    @Test
    void toOcpiReturnsNullWhenValueIsUnset() {
        assertThat(Days.toOcpi(new _DayEnum())).isNull();
    }
}
