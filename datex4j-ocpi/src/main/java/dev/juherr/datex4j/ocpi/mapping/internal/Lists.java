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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Shared "map each element, skipping nulls" helper used across the OCPI mappers. */
public final class Lists {

    private Lists() {}

    /**
     * Maps each element of {@code items} with {@code mapper}, skipping null inputs and null
     * results. Returns a fresh, mutable, empty list if {@code items} is {@code null} or empty.
     */
    public static <T, R> List<R> mapEach(List<T> items, Function<? super T, ? extends R> mapper) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        List<R> result = new ArrayList<>();
        for (T item : items) {
            if (item == null) {
                continue;
            }
            R mapped = mapper.apply(item);
            if (mapped != null) {
                result.add(mapped);
            }
        }
        return result;
    }
}
