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

import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import dev.juherr.datex4j.model.v3_7.common.MultilingualStringValue;

/** Builds and reads DATEX II {@link MultilingualString} values from plain OCPI text. */
public final class MultilingualStrings {

    private MultilingualStrings() {}

    /** Wraps {@code text} in a single-entry multilingual string, or {@code null} if text is null. */
    public static MultilingualString of(String lang, String text) {
        if (text == null) {
            return null;
        }
        MultilingualStringValue value = new MultilingualStringValue();
        value.setLang(lang);
        value.setValue(text);
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(value);
        MultilingualString result = new MultilingualString();
        result.setValues(values);
        return result;
    }

    /** Returns the first value's text, or {@code null} if absent. */
    public static String firstValue(MultilingualString string) {
        if (string == null
                || string.getValues() == null
                || string.getValues().getValue().isEmpty()) {
            return null;
        }
        return string.getValues().getValue().get(0).getValue();
    }
}
