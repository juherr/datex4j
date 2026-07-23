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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/** Converts between OCPI ISO-8601 timestamp strings and DATEX II {@link XMLGregorianCalendar} values. */
public final class Temporals {

    private static final DatatypeFactory DATATYPE_FACTORY = createDatatypeFactory();

    private Temporals() {}

    /**
     * Parses {@code iso} into an {@link XMLGregorianCalendar}, or {@code null} if {@code iso} is
     * {@code null}, blank, or not a valid ISO-8601 date/time.
     */
    public static XMLGregorianCalendar toXmlDateTime(String iso) {
        if (iso == null || iso.isBlank()) {
            return null;
        }
        try {
            return DATATYPE_FACTORY.newXMLGregorianCalendar(iso);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Formats {@code cal} as an ISO-8601 string, or {@code null} if {@code cal} is {@code null}. */
    public static String toIso(XMLGregorianCalendar cal) {
        if (cal == null) {
            return null;
        }
        return cal.toXMLFormat();
    }

    /**
     * Parses {@code hhmm} (a local time in {@code "HH:MM"} form) into a time-only {@link
     * XMLGregorianCalendar}, or {@code null} if {@code hhmm} is {@code null}, blank, or not a valid
     * {@code "HH:MM"} time.
     */
    public static XMLGregorianCalendar toXmlTime(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            return null;
        }
        String[] parts = hhmm.split(":", -1);
        if (parts.length != 2) {
            return null;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return null;
            }
            return DATATYPE_FACTORY.newXMLGregorianCalendarTime(hour, minute, 0, DatatypeConstants.FIELD_UNDEFINED);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Formats {@code cal} as a zero-padded {@code "HH:MM"} local time, or {@code null} if {@code
     * cal} is {@code null} or has no hour/minute set.
     */
    public static String toHhmm(XMLGregorianCalendar cal) {
        if (cal == null) {
            return null;
        }
        int hour = cal.getHour();
        int minute = cal.getMinute();
        if (hour == DatatypeConstants.FIELD_UNDEFINED || minute == DatatypeConstants.FIELD_UNDEFINED) {
            return null;
        }
        return String.format("%02d:%02d", hour, minute);
    }

    private static DatatypeFactory createDatatypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
