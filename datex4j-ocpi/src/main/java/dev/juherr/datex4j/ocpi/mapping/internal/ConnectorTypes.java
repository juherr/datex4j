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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ConnectorTypeEnum;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure._ConnectorTypeEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * Translates OCPI connector-standard strings to the DATEX II connector-type enum and back. Unknown
 * OCPI values map to {@link ConnectorTypeEnum#OTHER} with the original string kept in the DATEX II
 * {@code _extendedValue} attribute so no information is lost.
 */
public final class ConnectorTypes {

    private static final Map<String, ConnectorTypeEnum> TO_DATEX = new HashMap<>();

    static {
        TO_DATEX.put("CHADEMO", ConnectorTypeEnum.CHADEMO);
        TO_DATEX.put("IEC_62196_T1", ConnectorTypeEnum.IEC_62196_T_1);
        TO_DATEX.put("IEC_62196_T1_COMBO", ConnectorTypeEnum.IEC_62196_T_1_COMBO);
        TO_DATEX.put("IEC_62196_T2", ConnectorTypeEnum.IEC_62196_T_2);
        TO_DATEX.put("IEC_62196_T2_COMBO", ConnectorTypeEnum.IEC_62196_T_2_COMBO);
        TO_DATEX.put("IEC_62196_T3A", ConnectorTypeEnum.IEC_62196_T_3_A);
        TO_DATEX.put("IEC_62196_T3C", ConnectorTypeEnum.IEC_62196_T_3_C);
        TO_DATEX.put("DOMESTIC_F", ConnectorTypeEnum.DOMESTIC_F);
        TO_DATEX.put("TESLA_S", ConnectorTypeEnum.TESLA_S);
        TO_DATEX.put("TESLA_R", ConnectorTypeEnum.TESLA_R);
        TO_DATEX.put("PANTOGRAPH_TOP_DOWN", ConnectorTypeEnum.PANTOGRAPH_TOP_DOWN);
        TO_DATEX.put("PANTOGRAPH_BOTTOM_UP", ConnectorTypeEnum.PANTOGRAPH_BOTTOM_UP);
    }

    private static final Map<ConnectorTypeEnum, String> TO_OCPI = new HashMap<>();

    static {
        TO_DATEX.forEach((ocpi, datex) -> TO_OCPI.put(datex, ocpi));
    }

    private ConnectorTypes() {}

    /**
     * Maps an OCPI connector-standard string to a DATEX II connector-type wrapper, or {@code null}
     * if {@code ocpiStandard} is null. Unknown strings map to {@link ConnectorTypeEnum#OTHER} with
     * the original value preserved in {@code _extendedValue}.
     */
    public static _ConnectorTypeEnum toDatex(String ocpiStandard) {
        if (ocpiStandard == null) {
            return null;
        }
        _ConnectorTypeEnum wrapper = new _ConnectorTypeEnum();
        ConnectorTypeEnum mapped = TO_DATEX.get(ocpiStandard.toUpperCase());
        if (mapped != null) {
            wrapper.setValue(mapped);
        } else {
            wrapper.setValue(ConnectorTypeEnum.OTHER);
            wrapper.set_ExtendedValue(ocpiStandard);
        }
        return wrapper;
    }

    /**
     * Maps a DATEX II connector-type wrapper back to an OCPI connector-standard string, or {@code
     * null} if {@code type} or its value is null.
     */
    public static String toOcpi(_ConnectorTypeEnum type) {
        if (type == null || type.getValue() == null) {
            return null;
        }
        if (type.getValue() == ConnectorTypeEnum.OTHER && type.get_ExtendedValue() != null) {
            return type.get_ExtendedValue();
        }
        return TO_OCPI.get(type.getValue());
    }
}
