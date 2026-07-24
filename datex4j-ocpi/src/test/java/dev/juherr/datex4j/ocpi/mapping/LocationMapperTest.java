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

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import dev.juherr.datex4j.model.v3_7.common.MultilingualStringValue;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.locationextension.FacilityLocation;
import dev.juherr.datex4j.ocpi.mapping.internal.FacilityLocations;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.BusinessDetails;
import dev.juherr.datex4j.ocpi.model.v2_3.DisplayText;
import dev.juherr.datex4j.ocpi.model.v2_3.EVSE;
import dev.juherr.datex4j.ocpi.model.v2_3.EnergyMix;
import dev.juherr.datex4j.ocpi.model.v2_3.GeoLocation;
import dev.juherr.datex4j.ocpi.model.v2_3.Hours;
import dev.juherr.datex4j.ocpi.model.v2_3.Image;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import dev.juherr.datex4j.ocpi.model.v2_3.RegularHours;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

    private final LocationMapper mapper = new LocationMapper();

    private static Location sampleLocation() {
        Location location = new Location();
        location.setId("LOC-1");
        location.setName("Main Street Hub");
        GeoLocation geo = new GeoLocation();
        geo.setLatitude("52.1");
        geo.setLongitude("4.3");
        location.setCoordinates(geo);
        EVSE evse = new EVSE();
        evse.setUid("EVSE-1");
        location.setEvses(List.of(evse));
        location.setLastUpdated("2026-07-23T10:15:30Z");
        BusinessDetails operator = new BusinessDetails();
        operator.setName("Acme Charging");
        location.setOperator(operator);
        return location;
    }

    @Test
    void toDatexBuildsSiteWithNameAndStation() {
        EnergyInfrastructureSite site = mapper.toDatex(sampleLocation());

        assertThat(site.getId()).isEqualTo("LOC-1");
        assertThat(site.getName().getValues().getValue().get(0).getValue()).isEqualTo("Main Street Hub");
        assertThat(site.getEnergyInfrastructureStation()).hasSize(1);
        assertThat(site.getEnergyInfrastructureStation().get(0).getId()).isEqualTo("EVSE-1");
        assertThat(site.getLastUpdated().toXMLFormat()).isEqualTo("2026-07-23T10:15:30Z");
    }

    @Test
    void roundTripsIdNameAndEvseCount() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(sampleLocation()));

        assertThat(roundTrip.getId()).isEqualTo("LOC-1");
        assertThat(roundTrip.getName()).isEqualTo("Main Street Hub");
        assertThat(roundTrip.getEvses()).hasSize(1);
        assertThat(roundTrip.getLastUpdated()).isEqualTo("2026-07-23T10:15:30Z");
        assertThat(roundTrip.getOperator().getName()).isEqualTo("Acme Charging");
    }

    @Test
    void toDatexSkipsNullEvseElements() {
        Location location = sampleLocation();
        EVSE evse = location.getEvses().get(0);
        location.setEvses(Arrays.asList(evse, null));

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getEnergyInfrastructureStation()).hasSize(1).doesNotContainNull();
    }

    @Test
    void toOcpiSkipsNullStationElements() {
        EnergyInfrastructureSite site = mapper.toDatex(sampleLocation());
        site.getEnergyInfrastructureStation().add(null);

        Location location = mapper.toOcpi(site);

        assertThat(location.getEvses()).hasSize(1).doesNotContainNull();
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }

    @Test
    void toDatexAddsEnergyMixToEveryChargingPoint() {
        Location location = sampleLocation();
        EnergyMix energyMix = new EnergyMix();
        energyMix.setIsGreenEnergy(true);
        location.setEnergyMix(energyMix);

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getEnergyInfrastructureStation()).isNotEmpty();
        for (var station : site.getEnergyInfrastructureStation()) {
            for (var refillPoint : station.getRefillPoint()) {
                assertThat(refillPoint).isInstanceOf(ElectricChargingPoint.class);
                ElectricChargingPoint point = (ElectricChargingPoint) refillPoint;
                assertThat(point.getElectricEnergyMix()).hasSize(1);
                assertThat(point.getElectricEnergyMix().get(0).isIsGreenEnergy())
                        .isTrue();
            }
        }
    }

    @Test
    void roundTripsEnergyMixGreenFlag() {
        Location location = sampleLocation();
        EnergyMix energyMix = new EnergyMix();
        energyMix.setIsGreenEnergy(true);
        location.setEnergyMix(energyMix);

        Location roundTrip = mapper.toOcpi(mapper.toDatex(location));

        assertThat(roundTrip.getEnergyMix()).isNotNull();
        assertThat(roundTrip.getEnergyMix().getIsGreenEnergy()).isTrue();
    }

    @Test
    void toOcpiLeavesEnergyMixNullWhenSiteHasNone() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(sampleLocation()));

        assertThat(roundTrip.getEnergyMix()).isNull();
    }

    @Test
    void toDatexMapsImagesToPhotoUrl() {
        Location location = sampleLocation();
        Image image = new Image();
        image.setUrl(URI.create("https://x/p.png"));
        location.setImages(List.of(image));

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getPhotoUrl()).hasSize(1);
        assertThat(site.getPhotoUrl().get(0).getUrlLinkAddress()).isEqualTo("https://x/p.png");
    }

    @Test
    void roundTripsImageUrl() {
        Location location = sampleLocation();
        Image image = new Image();
        image.setUrl(URI.create("https://x/p.png"));
        location.setImages(List.of(image));

        Location roundTrip = mapper.toOcpi(mapper.toDatex(location));

        assertThat(roundTrip.getImages()).hasSize(1);
        assertThat(roundTrip.getImages().get(0).getUrl()).isEqualTo(URI.create("https://x/p.png"));
    }

    @Test
    void toDatexSkipsImagesWithNullUrl() {
        Location location = sampleLocation();
        Image withNullUrl = new Image();
        location.setImages(Arrays.asList(withNullUrl, null));

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getPhotoUrl()).isEmpty();
    }

    @Test
    void toDatexMapsFirstDirectionToAdditionalInformation() {
        Location location = sampleLocation();
        DisplayText direction = new DisplayText();
        direction.setLanguage("en");
        direction.setText("Turn left");
        location.setDirections(List.of(direction));

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getAdditionalInformation()).hasSize(1);
        assertThat(MultilingualStrings.firstValue(
                        site.getAdditionalInformation().get(0)))
                .isEqualTo("Turn left");
    }

    @Test
    void roundTripsFirstDirection() {
        Location location = sampleLocation();
        DisplayText direction = new DisplayText();
        direction.setLanguage("en");
        direction.setText("Turn left");
        location.setDirections(List.of(direction));

        Location roundTrip = mapper.toOcpi(mapper.toDatex(location));

        assertThat(roundTrip.getDirections()).hasSize(1);
        assertThat(roundTrip.getDirections().get(0).getText()).isEqualTo("Turn left");
        assertThat(roundTrip.getDirections().get(0).getLanguage()).isEqualTo("en");
    }

    @Test
    void toOcpiLeavesDirectionsEmptyWhenSiteHasNoAdditionalInformation() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(sampleLocation()));

        assertThat(roundTrip.getDirections()).isNullOrEmpty();
    }

    @Test
    void toOcpiDefaultsDirectionLanguageWhenDatexLangIsAbsent() {
        EnergyInfrastructureSite site = mapper.toDatex(sampleLocation());
        MultilingualStringValue value = new MultilingualStringValue();
        value.setLang(null);
        value.setValue("Turn left");
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(value);
        MultilingualString info = new MultilingualString();
        info.setValues(values);
        site.getAdditionalInformation().add(info);

        Location location = mapper.toOcpi(site);

        assertThat(location.getDirections()).hasSize(1);
        assertThat(location.getDirections().get(0).getLanguage()).isEqualTo("en");
        assertThat(location.getDirections().get(0).getText()).isEqualTo("Turn left");
    }

    @Test
    void toDatexMapsTwentyfourSevenOpeningTimesToOperatingHours() {
        Location location = sampleLocation();
        Hours hours = new Hours();
        hours.setTwentyfourseven(true);
        location.setOpeningTimes(hours);

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getOperatingHours()).isNotNull();
    }

    @Test
    void roundTripsRegularOpeningTimes() {
        Location location = sampleLocation();
        RegularHours regular = new RegularHours();
        regular.setWeekday(1);
        regular.setPeriodBegin("08:00");
        regular.setPeriodEnd("20:00");
        Hours hours = new Hours();
        hours.setTwentyfourseven(false);
        hours.setRegularHours(List.of(regular));
        location.setOpeningTimes(hours);

        Location roundTrip = mapper.toOcpi(mapper.toDatex(location));

        assertThat(roundTrip.getOpeningTimes()).isNotNull();
        assertThat(roundTrip.getOpeningTimes().getRegularHours()).hasSize(1);
        RegularHours roundTripRegular =
                roundTrip.getOpeningTimes().getRegularHours().get(0);
        assertThat(roundTripRegular.getWeekday()).isEqualTo(1);
        assertThat(roundTripRegular.getPeriodBegin()).isEqualTo("08:00");
        assertThat(roundTripRegular.getPeriodEnd()).isEqualTo("20:00");
    }

    @Test
    void toOcpiLeavesOpeningTimesNullWhenSiteHasNone() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(sampleLocation()));

        assertThat(roundTrip.getOpeningTimes()).isNull();
    }

    private static Location addressedLocation() {
        Location location = sampleLocation();
        location.setAddress("Main Street 1");
        location.setCity("Springfield");
        location.setPostalCode("12345");
        location.setCountry("USA");
        location.setTimeZone("Europe/Amsterdam");
        return location;
    }

    @Test
    void toDatexAnchorsAddressAndTimeZoneOnLocationReference() {
        EnergyInfrastructureSite site = mapper.toDatex(addressedLocation());

        FacilityLocation facility = FacilityLocations.of(site.getLocationReference());
        assertThat(facility).isNotNull();
        assertThat(facility.getTimeZone()).isEqualTo("Europe/Amsterdam");
        assertThat(facility.getAddress().getPostcode()).isEqualTo("12345");
    }

    @Test
    void roundTripsAddressAndTimeZone() {
        Location roundTrip = mapper.toOcpi(mapper.toDatex(addressedLocation()));

        assertThat(roundTrip.getAddress()).isEqualTo("Main Street 1");
        assertThat(roundTrip.getCity()).isEqualTo("Springfield");
        assertThat(roundTrip.getPostalCode()).isEqualTo("12345");
        assertThat(roundTrip.getCountry()).isEqualTo("USA");
        assertThat(roundTrip.getTimeZone()).isEqualTo("Europe/Amsterdam");
    }

    @Test
    void toDatexDropsAddressWhenLocationHasNoCoordinates() {
        Location location = addressedLocation();
        location.setCoordinates(null);

        EnergyInfrastructureSite site = mapper.toDatex(location);

        assertThat(site.getLocationReference()).isNull();
    }
}
