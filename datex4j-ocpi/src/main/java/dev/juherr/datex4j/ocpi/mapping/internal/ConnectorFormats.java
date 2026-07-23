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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ConnectorFormatTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._ConnectorFormatTypeEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.ConnectorFormat;

/** Translates OCPI {@link ConnectorFormat} to the DATEX II connector-format enum and back. */
public final class ConnectorFormats {

    private ConnectorFormats() {}

    /** Maps {@code format} to a DATEX II connector-format wrapper, or {@code null} if input is null. */
    public static _ConnectorFormatTypeEnum toDatex(ConnectorFormat format) {
        if (format == null) {
            return null;
        }
        _ConnectorFormatTypeEnum wrapper = new _ConnectorFormatTypeEnum();
        wrapper.setValue(
                format == ConnectorFormat.SOCKET
                        ? ConnectorFormatTypeEnum.SOCKET
                        : ConnectorFormatTypeEnum.OTHER_CABLE);
        return wrapper;
    }

    /** Maps {@code format} back to an OCPI connector format, or {@code null} if input/value is null. */
    public static ConnectorFormat toOcpi(_ConnectorFormatTypeEnum format) {
        if (format == null || format.getValue() == null) {
            return null;
        }
        return format.getValue() == ConnectorFormatTypeEnum.SOCKET ? ConnectorFormat.SOCKET : ConnectorFormat.CABLE;
    }
}
