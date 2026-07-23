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

import dev.juherr.datex4j.model.v3_7.common.DayEnum;
import dev.juherr.datex4j.model.v3_7.common._DayEnum;

/** Converts between OCPI ISO-8601 weekday numbers (1=Monday..7=Sunday) and DATEX II {@link DayEnum}. */
public final class Days {

    private static final DayEnum[] BY_WEEKDAY = {
        DayEnum.MONDAY,
        DayEnum.TUESDAY,
        DayEnum.WEDNESDAY,
        DayEnum.THURSDAY,
        DayEnum.FRIDAY,
        DayEnum.SATURDAY,
        DayEnum.SUNDAY
    };

    private Days() {}

    /**
     * Maps {@code weekday} (1=Monday..7=Sunday) to a DATEX II {@link _DayEnum}, or {@code null} if
     * {@code weekday} is {@code null} or out of the 1..7 range.
     */
    public static _DayEnum toDatex(Integer weekday) {
        if (weekday == null || weekday < 1 || weekday > 7) {
            return null;
        }
        _DayEnum wrapper = new _DayEnum();
        wrapper.setValue(BY_WEEKDAY[weekday - 1]);
        return wrapper;
    }

    /**
     * Maps {@code day} back to an OCPI weekday number (1=Monday..7=Sunday), or {@code null} if
     * {@code day} is {@code null} or its value is unset.
     */
    public static Integer toOcpi(_DayEnum day) {
        if (day == null || day.getValue() == null) {
            return null;
        }
        for (int i = 0; i < BY_WEEKDAY.length; i++) {
            if (BY_WEEKDAY[i] == day.getValue()) {
                return i + 1;
            }
        }
        return null;
    }
}
