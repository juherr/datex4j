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

import dev.juherr.datex4j.ocpi.mapping.internal.ConnectorFormats;
import dev.juherr.datex4j.ocpi.mapping.internal.ConnectorTypes;

/**
 * Maps OCPI {@link dev.juherr.datex4j.ocpi.model.v2_3.Connector} to a DATEX II
 * {@link dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector} and back.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code id}, {@code powerType}, {@code tariffIds}, {@code
 * termsAndConditions}, {@code capabilities}, {@code lastUpdated} have no direct DATEX II slot;
 * DATEX II {@code chargingMode}, {@code countryOfDomesticSocket} have no OCPI slot. OCPI power is
 * in watts and is carried on DATEX {@code maxPowerAtSocket} as-is.
 */
public final class ConnectorMapper {

    /** Builds a DATEX II connector from {@code ocpi}, or {@code null} if {@code ocpi} is null. */
    public dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector toDatex(
            dev.juherr.datex4j.ocpi.model.v2_3.Connector ocpi) {
        if (ocpi == null) {
            return null;
        }
        var datex = new dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector();
        if (ocpi.getStandard() != null) {
            datex.setConnectorType(ConnectorTypes.toDatex(ocpi.getStandard().getString()));
        }
        datex.setConnectorFormat(ConnectorFormats.toDatex(ocpi.getFormat()));
        if (ocpi.getMaxElectricPower() != null) {
            datex.setMaxPowerAtSocket(ocpi.getMaxElectricPower().floatValue());
        }
        if (ocpi.getMaxVoltage() != null) {
            datex.setVoltage(ocpi.getMaxVoltage().floatValue());
        }
        if (ocpi.getMaxAmperage() != null) {
            datex.setMaximumCurrent(ocpi.getMaxAmperage().floatValue());
        }
        return datex;
    }

    /** Builds an OCPI connector from {@code datex}, or {@code null} if {@code datex} is null. */
    public dev.juherr.datex4j.ocpi.model.v2_3.Connector toOcpi(
            dev.juherr.datex4j.model.v3_7.energyinfrastructure.Connector datex) {
        if (datex == null) {
            return null;
        }
        var ocpi = new dev.juherr.datex4j.ocpi.model.v2_3.Connector();
        String standard = ConnectorTypes.toOcpi(datex.getConnectorType());
        if (standard != null) {
            var wrapper = new dev.juherr.datex4j.ocpi.model.v2_3.ConnectorType();
            wrapper.setActualInstance(standard);
            ocpi.setStandard(wrapper);
        }
        ocpi.setFormat(ConnectorFormats.toOcpi(datex.getConnectorFormat()));
        ocpi.setMaxElectricPower((int) datex.getMaxPowerAtSocket());
        if (datex.getVoltage() != null) {
            ocpi.setMaxVoltage(datex.getVoltage().intValue());
        }
        if (datex.getMaximumCurrent() != null) {
            ocpi.setMaxAmperage(datex.getMaximumCurrent().intValue());
        }
        return ocpi;
    }
}
