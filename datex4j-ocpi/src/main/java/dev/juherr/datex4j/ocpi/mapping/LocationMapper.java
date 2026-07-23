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
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureStation;
import dev.juherr.datex4j.ocpi.mapping.internal.Images;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.mapping.internal.Temporals;
import dev.juherr.datex4j.ocpi.model.v2_3.EVSE;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps an OCPI {@link Location} to a DATEX II {@link EnergyInfrastructureSite} and back.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code country_code}, {@code party_id}, {@code address},
 * {@code city}, {@code postal_code}, {@code time_zone}, {@code opening_times}, {@code directions}
 * are not mapped in this iteration; DATEX II {@code typeOfSite}, {@code brand} have no OCPI
 * equivalent.
 *
 * <p><b>Images.</b> OCPI {@code images} maps to DATEX II {@code photoUrl} via {@link
 * dev.juherr.datex4j.ocpi.mapping.internal.Images}; only the image URL round-trips, other {@code
 * Image} fields (thumbnail, category, type, width, height) have no DATEX II equivalent.
 *
 * <p><b>Energy mix.</b> OCPI models {@code energy_mix} once per {@code Location}, while DATEX II
 * models {@link dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix} once per
 * {@link dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint}. On {@code
 * toDatex}, the mapped mix is added to every charging point under the site; on {@code toOcpi},
 * only the first charging point with a mix is read back.
 */
public final class LocationMapper {

    private static final String DEFAULT_LANG = "en";

    private final EvseMapper evseMapper = new EvseMapper();
    private final GeoLocationMapper geoLocationMapper = new GeoLocationMapper();
    private final OrganisationMapper organisationMapper = new OrganisationMapper();
    private final EnergyMixMapper energyMixMapper = new EnergyMixMapper();

    /** Builds a DATEX II site from {@code location}, or {@code null} if {@code location} is null. */
    public EnergyInfrastructureSite toDatex(Location location) {
        if (location == null) {
            return null;
        }
        EnergyInfrastructureSite site = new EnergyInfrastructureSite();
        site.setId(location.getId());
        site.setName(MultilingualStrings.of(DEFAULT_LANG, location.getName()));
        site.setLocationReference(geoLocationMapper.toDatex(location.getCoordinates()));
        site.setLastUpdated(Temporals.toXmlDateTime(location.getLastUpdated()));
        site.setOperator(organisationMapper.toDatex(location.getOperator()));
        site.setOwner(organisationMapper.toDatex(location.getOwner()));
        site.getPhotoUrl().addAll(Images.toDatex(location.getImages()));
        if (location.getEvses() != null) {
            for (var evse : location.getEvses()) {
                var station = evseMapper.toDatex(evse);
                if (station != null) {
                    site.getEnergyInfrastructureStation().add(station);
                }
            }
        }
        if (location.getEnergyMix() != null) {
            applyEnergyMix(site, location.getEnergyMix());
        }
        return site;
    }

    private void applyEnergyMix(EnergyInfrastructureSite site, EnergyMix energyMix) {
        for (EnergyInfrastructureStation station : site.getEnergyInfrastructureStation()) {
            if (station == null) {
                continue;
            }
            for (var refillPoint : station.getRefillPoint()) {
                if (refillPoint instanceof ElectricChargingPoint point) {
                    point.getElectricEnergyMix().add(energyMixMapper.toDatex(energyMix));
                }
            }
        }
    }

    /** Builds an OCPI location from {@code site}, or {@code null} if {@code site} is null. */
    public Location toOcpi(EnergyInfrastructureSite site) {
        if (site == null) {
            return null;
        }
        Location location = new Location();
        location.setId(site.getId());
        location.setName(MultilingualStrings.firstValue(site.getName()));
        location.setCoordinates(geoLocationMapper.toOcpi(site.getLocationReference()));
        location.setLastUpdated(Temporals.toIso(site.getLastUpdated()));
        location.setOperator(organisationMapper.toOcpi(site.getOperator()));
        location.setOwner(organisationMapper.toOcpi(site.getOwner()));
        location.setImages(Images.toOcpi(site.getPhotoUrl()));
        List<EVSE> evses = new ArrayList<>();
        for (EnergyInfrastructureStation station : site.getEnergyInfrastructureStation()) {
            EVSE mapped = evseMapper.toOcpi(station);
            if (mapped != null) {
                evses.add(mapped);
            }
        }
        location.setEvses(evses);
        location.setEnergyMix(energyMixMapper.toOcpi(findFirstEnergyMix(site)));
        return location;
    }

    private static ElectricEnergyMix findFirstEnergyMix(EnergyInfrastructureSite site) {
        for (EnergyInfrastructureStation station : site.getEnergyInfrastructureStation()) {
            if (station == null) {
                continue;
            }
            for (var refillPoint : station.getRefillPoint()) {
                if (refillPoint instanceof ElectricChargingPoint point
                        && !point.getElectricEnergyMix().isEmpty()) {
                    return point.getElectricEnergyMix().get(0);
                }
            }
        }
        return null;
    }
}
