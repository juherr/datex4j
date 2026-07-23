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
package dev.juherr.datex4j.ocpi.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.common.DayEnum;
import dev.juherr.datex4j.model.v3_7.common.DayWeekMonth;
import dev.juherr.datex4j.model.v3_7.common.Period;
import dev.juherr.datex4j.model.v3_7.common.TimePeriodOfDay;
import dev.juherr.datex4j.model.v3_7.facilities.OpenAllHours;
import dev.juherr.datex4j.model.v3_7.facilities.OperatingHours;
import dev.juherr.datex4j.model.v3_7.facilities.OperatingHoursSpecification;
import dev.juherr.datex4j.ocpi.model.v2_3.Hours;
import dev.juherr.datex4j.ocpi.model.v2_3.RegularHours;
import java.util.List;
import org.junit.jupiter.api.Test;

class HoursMapperTest {

    private final HoursMapper mapper = new HoursMapper();

    private static RegularHours regularHours(Integer weekday, String begin, String end) {
        RegularHours regular = new RegularHours();
        regular.setWeekday(weekday);
        regular.setPeriodBegin(begin);
        regular.setPeriodEnd(end);
        return regular;
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }

    @Test
    void toDatexMapsTwentyfourSevenToOpenAllHours() {
        Hours hours = new Hours();
        hours.setTwentyfourseven(true);

        OperatingHours operatingHours = mapper.toDatex(hours);

        assertThat(operatingHours).isInstanceOf(OpenAllHours.class);
    }

    @Test
    void roundTripsTwentyfourSeven() {
        Hours hours = new Hours();
        hours.setTwentyfourseven(true);

        Hours roundTrip = mapper.toOcpi(mapper.toDatex(hours));

        assertThat(roundTrip.getTwentyfourseven()).isTrue();
    }

    @Test
    void toDatexMapsOneRegularHoursEntryToOperatingHoursSpecification() {
        Hours hours = new Hours();
        hours.setTwentyfourseven(false);
        hours.setRegularHours(List.of(regularHours(1, "08:00", "20:00")));

        OperatingHours operatingHours = mapper.toDatex(hours);

        assertThat(operatingHours).isInstanceOf(OperatingHoursSpecification.class);
        OperatingHoursSpecification specification = (OperatingHoursSpecification) operatingHours;
        assertThat(specification.getOverallPeriod()).isNotNull();
        assertThat(specification.getOverallPeriod().getValidPeriod()).hasSize(1);

        Period period = specification.getOverallPeriod().getValidPeriod().get(0);
        assertThat(period.getRecurringDayWeekMonthPeriod()).hasSize(1);
        DayWeekMonth dayWeekMonth = period.getRecurringDayWeekMonthPeriod().get(0);
        assertThat(dayWeekMonth.getApplicableDay()).hasSize(1);
        assertThat(dayWeekMonth.getApplicableDay().get(0).getValue()).isEqualTo(DayEnum.MONDAY);

        assertThat(period.getRecurringTimePeriodOfDay()).hasSize(1);
        TimePeriodOfDay timePeriodOfDay = period.getRecurringTimePeriodOfDay().get(0);
        assertThat(timePeriodOfDay.getStartTimeOfPeriod().toXMLFormat()).startsWith("08:00:00");
        assertThat(timePeriodOfDay.getEndTimeOfPeriod().toXMLFormat()).startsWith("20:00:00");
    }

    @Test
    void roundTripsOneRegularHoursEntry() {
        Hours hours = new Hours();
        hours.setTwentyfourseven(false);
        hours.setRegularHours(List.of(regularHours(1, "08:00", "20:00")));

        Hours roundTrip = mapper.toOcpi(mapper.toDatex(hours));

        assertThat(roundTrip.getRegularHours()).hasSize(1);
        RegularHours regular = roundTrip.getRegularHours().get(0);
        assertThat(regular.getWeekday()).isEqualTo(1);
        assertThat(regular.getPeriodBegin()).isEqualTo("08:00");
        assertThat(regular.getPeriodEnd()).isEqualTo("20:00");
    }

    @Test
    void toDatexReturnsNullWhenHoursCarriesNoUsableInformation() {
        Hours hours = new Hours();

        assertThat(mapper.toDatex(hours)).isNull();
    }

    @Test
    void toDatexSkipsRegularHoursWithUnmappableWeekday() {
        Hours hours = new Hours();
        hours.setRegularHours(List.of(regularHours(0, "08:00", "20:00")));

        assertThat(mapper.toDatex(hours)).isNull();
    }

    @Test
    void toOcpiReturnsNullForSpecificationWithoutOverallPeriod() {
        OperatingHoursSpecification specification = new OperatingHoursSpecification();

        assertThat(mapper.toOcpi(specification)).isNull();
    }

    @Test
    void toDatexSkipsTimePeriodWhenPeriodBeginIsUnparsable() {
        Hours hours = new Hours();
        hours.setTwentyfourseven(false);
        hours.setRegularHours(List.of(regularHours(1, "whoops", "20:00")));

        OperatingHours operatingHours = mapper.toDatex(hours);

        assertThat(operatingHours).isInstanceOf(OperatingHoursSpecification.class);
        OperatingHoursSpecification specification = (OperatingHoursSpecification) operatingHours;
        assertThat(specification.getOverallPeriod().getValidPeriod()).hasSize(1);
        Period period = specification.getOverallPeriod().getValidPeriod().get(0);
        assertThat(period.getRecurringDayWeekMonthPeriod()).hasSize(1);
        assertThat(period.getRecurringTimePeriodOfDay()).isEmpty();
    }
}
