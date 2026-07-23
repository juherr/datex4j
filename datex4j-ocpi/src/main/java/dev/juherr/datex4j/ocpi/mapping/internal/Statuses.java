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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.RefillPointStatusEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._RefillPointStatusEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.Status;
import java.util.EnumMap;
import java.util.Map;

/** Translates OCPI EVSE {@link Status} to the DATEX II refill-point-status enum and back. */
public final class Statuses {

    private static final Map<Status, RefillPointStatusEnum> TO_DATEX = new EnumMap<>(Status.class);
    private static final Map<RefillPointStatusEnum, Status> TO_OCPI = new EnumMap<>(RefillPointStatusEnum.class);

    static {
        TO_DATEX.put(Status.AVAILABLE, RefillPointStatusEnum.AVAILABLE);
        TO_DATEX.put(Status.BLOCKED, RefillPointStatusEnum.BLOCKED);
        TO_DATEX.put(Status.CHARGING, RefillPointStatusEnum.CHARGING);
        TO_DATEX.put(Status.INOPERATIVE, RefillPointStatusEnum.INOPERATIVE);
        TO_DATEX.put(Status.OUTOFORDER, RefillPointStatusEnum.OUT_OF_ORDER);
        TO_DATEX.put(Status.PLANNED, RefillPointStatusEnum.PLANNED);
        TO_DATEX.put(Status.REMOVED, RefillPointStatusEnum.REMOVED);
        TO_DATEX.put(Status.RESERVED, RefillPointStatusEnum.RESERVED);
        TO_DATEX.put(Status.UNKNOWN, RefillPointStatusEnum.UNKNOWN);
        TO_DATEX.forEach((ocpi, datex) -> TO_OCPI.put(datex, ocpi));
    }

    private Statuses() {}

    public static _RefillPointStatusEnum toDatex(Status status) {
        if (status == null) {
            return null;
        }
        _RefillPointStatusEnum wrapper = new _RefillPointStatusEnum();
        wrapper.setValue(TO_DATEX.getOrDefault(status, RefillPointStatusEnum.UNKNOWN));
        return wrapper;
    }

    public static Status toOcpi(_RefillPointStatusEnum status) {
        if (status == null || status.getValue() == null) {
            return null;
        }
        return TO_OCPI.getOrDefault(status.getValue(), Status.UNKNOWN);
    }
}
