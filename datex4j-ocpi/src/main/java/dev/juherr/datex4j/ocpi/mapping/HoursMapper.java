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

import dev.juherr.datex4j.model.v3_7.common.DayWeekMonth;
import dev.juherr.datex4j.model.v3_7.common.OverallPeriod;
import dev.juherr.datex4j.model.v3_7.common.Period;
import dev.juherr.datex4j.model.v3_7.common.TimePeriodOfDay;
import dev.juherr.datex4j.model.v3_7.common._DayEnum;
import dev.juherr.datex4j.model.v3_7.facilities.OpenAllHours;
import dev.juherr.datex4j.model.v3_7.facilities.OperatingHours;
import dev.juherr.datex4j.model.v3_7.facilities.OperatingHoursSpecification;
import dev.juherr.datex4j.ocpi.mapping.internal.Days;
import dev.juherr.datex4j.ocpi.mapping.internal.Temporals;
import dev.juherr.datex4j.ocpi.model.v2_3.Hours;
import dev.juherr.datex4j.ocpi.model.v2_3.RegularHours;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Maps an OCPI {@link Hours} to a DATEX II {@link OperatingHours} and back.
 *
 * <p><b>Scope.</b> Only the basic subset of OCPI opening hours is mapped in this iteration:
 * {@code twentyfourseven} and {@code regularHours}. OCPI {@code exceptionalOpenings} and {@code
 * exceptionalClosings} (one-off exceptional openings/closings) have no DATEX II equivalent mapped
 * here and are silently dropped.
 *
 * <p><b>Unset required field.</b> {@link OverallPeriod#getOverallStartTime()} is XSD-required but
 * left unset on {@code toDatex}, since no OCPI field maps to it (consistent with this codebase's
 * existing convention of leaving unmappable required fields unset).
 *
 * <p><b>Reverse-mapping collapse.</b> {@code toOcpi} reads only the first applicable day and the
 * first time slot of each {@link Period}, so an externally-authored {@link
 * OperatingHoursSpecification} with multi-day or multi-slot periods is collapsed to one weekday and
 * one time slot per period. OCPI-originated round-trips are unaffected, since {@code toDatex} only
 * ever emits single-day, single-slot periods.
 */
public final class HoursMapper {

    /**
     * Builds a DATEX II {@link OperatingHours} from {@code hours}, or {@code null} if {@code hours}
     * is {@code null} or carries no usable information.
     */
    public OperatingHours toDatex(Hours hours) {
        if (hours == null) {
            return null;
        }
        if (Boolean.TRUE.equals(hours.getTwentyfourseven())) {
            return new OpenAllHours();
        }
        List<RegularHours> regularHours = hours.getRegularHours();
        if (regularHours == null || regularHours.isEmpty()) {
            return null;
        }
        List<Period> periods = new ArrayList<>();
        for (RegularHours regular : regularHours) {
            Period period = toPeriod(regular);
            if (period != null) {
                periods.add(period);
            }
        }
        if (periods.isEmpty()) {
            return null;
        }
        OverallPeriod overallPeriod = new OverallPeriod();
        overallPeriod.getValidPeriod().addAll(periods);
        OperatingHoursSpecification specification = new OperatingHoursSpecification();
        specification.setOverallPeriod(overallPeriod);
        return specification;
    }

    private Period toPeriod(RegularHours regular) {
        if (regular == null) {
            return null;
        }
        _DayEnum day = Days.toDatex(regular.getWeekday());
        if (day == null) {
            return null;
        }
        DayWeekMonth dayWeekMonth = new DayWeekMonth();
        dayWeekMonth.getApplicableDay().add(day);

        Period period = new Period();
        period.getRecurringDayWeekMonthPeriod().add(dayWeekMonth);

        XMLGregorianCalendar startTime = Temporals.toXmlTime(regular.getPeriodBegin());
        XMLGregorianCalendar endTime = Temporals.toXmlTime(regular.getPeriodEnd());
        if (startTime != null && endTime != null) {
            TimePeriodOfDay timePeriodOfDay = new TimePeriodOfDay();
            timePeriodOfDay.setStartTimeOfPeriod(startTime);
            timePeriodOfDay.setEndTimeOfPeriod(endTime);
            period.getRecurringTimePeriodOfDay().add(timePeriodOfDay);
        }
        return period;
    }

    /**
     * Builds an OCPI {@link Hours} from {@code operatingHours}, or {@code null} if {@code
     * operatingHours} is {@code null}, an unsupported subtype, or carries no usable information.
     */
    public Hours toOcpi(OperatingHours operatingHours) {
        if (operatingHours == null) {
            return null;
        }
        if (operatingHours instanceof OpenAllHours) {
            Hours hours = new Hours();
            hours.setTwentyfourseven(true);
            return hours;
        }
        if (operatingHours instanceof OperatingHoursSpecification specification) {
            return toOcpi(specification);
        }
        return null;
    }

    private Hours toOcpi(OperatingHoursSpecification specification) {
        OverallPeriod overallPeriod = specification.getOverallPeriod();
        if (overallPeriod == null || overallPeriod.getValidPeriod().isEmpty()) {
            return null;
        }
        List<RegularHours> regularHours = new ArrayList<>();
        for (Period period : overallPeriod.getValidPeriod()) {
            RegularHours regular = toRegularHours(period);
            if (regular != null) {
                regularHours.add(regular);
            }
        }
        if (regularHours.isEmpty()) {
            return null;
        }
        Hours hours = new Hours();
        hours.setTwentyfourseven(false);
        hours.setRegularHours(regularHours);
        return hours;
    }

    private RegularHours toRegularHours(Period period) {
        if (period == null) {
            return null;
        }
        Integer weekday = firstWeekday(period.getRecurringDayWeekMonthPeriod());
        if (weekday == null) {
            return null;
        }
        List<TimePeriodOfDay> timePeriods = period.getRecurringTimePeriodOfDay();
        if (timePeriods.isEmpty()) {
            return null;
        }
        TimePeriodOfDay timePeriodOfDay = timePeriods.get(0);
        String begin = Temporals.toHhmm(timePeriodOfDay.getStartTimeOfPeriod());
        String end = Temporals.toHhmm(timePeriodOfDay.getEndTimeOfPeriod());
        if (begin == null || end == null) {
            return null;
        }
        RegularHours regular = new RegularHours();
        regular.setWeekday(weekday);
        regular.setPeriodBegin(begin);
        regular.setPeriodEnd(end);
        return regular;
    }

    private Integer firstWeekday(List<DayWeekMonth> dayWeekMonths) {
        for (DayWeekMonth dayWeekMonth : dayWeekMonths) {
            if (dayWeekMonth == null) {
                continue;
            }
            for (_DayEnum day : dayWeekMonth.getApplicableDay()) {
                Integer weekday = Days.toOcpi(day);
                if (weekday != null) {
                    return weekday;
                }
            }
        }
        return null;
    }
}
