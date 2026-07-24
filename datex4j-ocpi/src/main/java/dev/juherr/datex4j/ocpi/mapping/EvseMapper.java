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

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureStation;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.RefillPoint;
import dev.juherr.datex4j.ocpi.mapping.internal.Images;
import dev.juherr.datex4j.ocpi.mapping.internal.Lists;
import dev.juherr.datex4j.ocpi.mapping.internal.Temporals;
import dev.juherr.datex4j.ocpi.model.v2_3.EVSE;

/**
 * Maps an OCPI {@link EVSE} to a DATEX II {@link EnergyInfrastructureStation} holding a single
 * {@link ElectricChargingPoint}, and back.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code status} (map separately via {@code StatusMapper}),
 * {@code capabilities}, {@code floorLevel}, {@code parking}, {@code directions} have no direct
 * slot; only the first refill point is read back to OCPI.
 *
 * <p><b>Images.</b> OCPI {@code images} maps to DATEX II {@code photoUrl} via {@link
 * dev.juherr.datex4j.ocpi.mapping.internal.Images}; only the image URL round-trips, other {@code
 * Image} fields (thumbnail, category, type, width, height) have no DATEX II equivalent.
 */
public final class EvseMapper {

    private final ConnectorMapper connectorMapper = new ConnectorMapper();
    private final GeoLocationMapper geoLocationMapper = new GeoLocationMapper();

    /** Builds a DATEX II station from {@code evse}, or {@code null} if {@code evse} is null. */
    public EnergyInfrastructureStation toDatex(EVSE evse) {
        if (evse == null) {
            return null;
        }
        ElectricChargingPoint point = new ElectricChargingPoint();
        point.getConnector().addAll(Lists.mapEach(evse.getConnectors(), connectorMapper::toDatex));

        EnergyInfrastructureStation station = new EnergyInfrastructureStation();
        station.setId(evse.getUid());
        station.setExternalIdentifier(evse.getEvseId());
        station.setLocationReference(geoLocationMapper.toDatex(evse.getCoordinates()));
        station.setLastUpdated(Temporals.toXmlDateTime(evse.getLastUpdated()));
        station.getPhotoUrl().addAll(Images.toDatex(evse.getImages()));
        station.getRefillPoint().add(point);
        return station;
    }

    /** Builds an OCPI EVSE from {@code station}, or {@code null} if {@code station} is null. */
    public EVSE toOcpi(EnergyInfrastructureStation station) {
        if (station == null) {
            return null;
        }
        EVSE evse = new EVSE();
        evse.setUid(station.getId());
        evse.setEvseId(station.getExternalIdentifier());
        evse.setCoordinates(geoLocationMapper.toOcpi(station.getLocationReference()));
        evse.setLastUpdated(Temporals.toIso(station.getLastUpdated()));
        evse.setImages(Images.toOcpi(station.getPhotoUrl()));

        RefillPoint first = station.getRefillPoint().isEmpty()
                ? null
                : station.getRefillPoint().get(0);
        if (first instanceof ElectricChargingPoint point) {
            evse.setConnectors(Lists.mapEach(point.getConnector(), connectorMapper::toOcpi));
        }
        return evse;
    }
}
