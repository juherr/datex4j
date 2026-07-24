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

import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricEnergyMix;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureStation;
import dev.juherr.datex4j.ocpi.mapping.internal.Images;
import dev.juherr.datex4j.ocpi.mapping.internal.Lists;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.mapping.internal.Temporals;
import dev.juherr.datex4j.ocpi.model.v2_3.DisplayText;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps an OCPI {@link Location} to a DATEX II {@link EnergyInfrastructureSite} and back.
 *
 * <p><b>Unmapped fields.</b> OCPI {@code country_code}, {@code party_id}, {@code city}, {@code
 * postal_code}, {@code suboperator} are not mapped in this iteration (DATEX II {@code
 * EnergyInfrastructureSite} has only {@code operator} and {@code owner} organisation slots, no
 * third one); DATEX II {@code typeOfSite}, {@code brand} have no OCPI equivalent.
 *
 * <p><b>Address and time zone.</b> OCPI {@code address} and {@code time_zone} are deliberately left
 * unmapped: DATEX II only models a street-address/time-zone concept via the
 * {@link dev.juherr.datex4j.model.v3_7.locationextension.FacilityLocation} location-extension type,
 * which is reachable solely through the generic {@code _extension} ({@code xs:any}) mechanism, not
 * through any typed property of {@link EnergyInfrastructureSite}. Mapping them is deferred to a
 * future profile/extension mechanism.
 *
 * <p><b>Opening hours.</b> OCPI {@code opening_times} maps to DATEX II {@code operatingHours} via
 * {@link HoursMapper}, covering only the basic subset: 24/7 and regular weekly hours. Exceptional
 * openings/closings ({@code exceptional_openings}, {@code exceptional_closings}) have no DATEX II
 * equivalent mapped here and are not round-tripped.
 *
 * <p><b>Directions.</b> OCPI {@code directions} (a list of localized texts) is an <b>approximate</b>
 * mapping to DATEX II {@code additionalInformation} (free-text multilingual strings) &mdash; the
 * two fields are not semantically equivalent. Only the first {@code DisplayText} round-trips; on
 * {@code toDatex}, its language (defaulting to {@code "en"} when absent) and text become one
 * {@code MultilingualString} appended to {@code additionalInformation}; on {@code toOcpi}, the
 * first {@code additionalInformation} entry is read back into a single-element {@code directions}
 * list.
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
    private final HoursMapper hoursMapper = new HoursMapper();

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
        site.setOperatingHours(hoursMapper.toDatex(location.getOpeningTimes()));
        site.getPhotoUrl().addAll(Images.toDatex(location.getImages()));
        site.getEnergyInfrastructureStation().addAll(Lists.mapEach(location.getEvses(), evseMapper::toDatex));
        if (location.getEnergyMix() != null) {
            applyEnergyMix(site, location.getEnergyMix());
        }
        applyFirstDirection(site, location.getDirections());
        return site;
    }

    private void applyFirstDirection(EnergyInfrastructureSite site, List<DisplayText> directions) {
        if (directions == null || directions.isEmpty()) {
            return;
        }
        DisplayText direction = directions.get(0);
        if (direction == null || direction.getText() == null) {
            return;
        }
        String lang = direction.getLanguage() != null ? direction.getLanguage() : DEFAULT_LANG;
        MultilingualString info = MultilingualStrings.of(lang, direction.getText());
        if (info != null) {
            site.getAdditionalInformation().add(info);
        }
    }

    private void applyEnergyMix(EnergyInfrastructureSite site, EnergyMix energyMix) {
        ElectricEnergyMix mix = energyMixMapper.toDatex(energyMix);
        for (ElectricChargingPoint point : chargingPoints(site)) {
            point.getElectricEnergyMix().add(mix);
        }
    }

    private static List<ElectricChargingPoint> chargingPoints(EnergyInfrastructureSite site) {
        List<ElectricChargingPoint> points = new ArrayList<>();
        for (EnergyInfrastructureStation station : site.getEnergyInfrastructureStation()) {
            if (station == null) {
                continue;
            }
            for (var refillPoint : station.getRefillPoint()) {
                if (refillPoint instanceof ElectricChargingPoint point) {
                    points.add(point);
                }
            }
        }
        return points;
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
        location.setOpeningTimes(hoursMapper.toOcpi(site.getOperatingHours()));
        location.setImages(Images.toOcpi(site.getPhotoUrl()));
        location.setEvses(Lists.mapEach(site.getEnergyInfrastructureStation(), evseMapper::toOcpi));
        location.setEnergyMix(energyMixMapper.toOcpi(findFirstEnergyMix(site)));
        location.setDirections(firstDirection(site.getAdditionalInformation()));
        return location;
    }

    private static List<DisplayText> firstDirection(List<MultilingualString> additionalInformation) {
        if (additionalInformation == null || additionalInformation.isEmpty()) {
            return null;
        }
        MultilingualString info = additionalInformation.get(0);
        String text = MultilingualStrings.firstValue(info);
        if (text == null) {
            return null;
        }
        String lang = MultilingualStrings.firstLang(info, DEFAULT_LANG);
        DisplayText direction = new DisplayText();
        direction.setLanguage(lang);
        direction.setText(text);
        return List.of(direction);
    }

    private static ElectricEnergyMix findFirstEnergyMix(EnergyInfrastructureSite site) {
        for (ElectricChargingPoint point : chargingPoints(site)) {
            if (!point.getElectricEnergyMix().isEmpty()) {
                return point.getElectricEnergyMix().get(0);
            }
        }
        return null;
    }
}
