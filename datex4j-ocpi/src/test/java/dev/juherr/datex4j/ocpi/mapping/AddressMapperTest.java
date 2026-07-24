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
import static org.assertj.core.api.Assertions.assertThatCode;

import dev.juherr.datex4j.model.v3_7.locationextension.Address;
import dev.juherr.datex4j.model.v3_7.locationextension.AddressLineTypeEnum;
import dev.juherr.datex4j.model.v3_7.locationextension.FacilityLocation;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import org.junit.jupiter.api.Test;

class AddressMapperTest {

    private final AddressMapper mapper = new AddressMapper();

    private static Location sampleLocation() {
        Location location = new Location();
        location.setAddress("F. Rooseveltlaan 3A");
        location.setCity("Amsterdam");
        location.setPostalCode("1078NX");
        location.setCountry("NLD");
        location.setTimeZone("Europe/Amsterdam");
        return location;
    }

    @Test
    void toDatexBuildsFacilityLocationWithAddressAndTimeZone() {
        FacilityLocation facility = mapper.toDatex(sampleLocation());

        assertThat(facility).isNotNull();
        assertThat(facility.getTimeZone()).isEqualTo("Europe/Amsterdam");
        Address address = facility.getAddress();
        assertThat(address.getPostcode()).isEqualTo("1078NX");
        assertThat(address.getCountryCode()).isEqualTo("NLD");
        assertThat(MultilingualStrings.firstValue(address.getCity())).isEqualTo("Amsterdam");
        assertThat(address.getAddressLine()).hasSize(1);
        assertThat(address.getAddressLine().get(0).getType().getValue()).isEqualTo(AddressLineTypeEnum.STREET);
        assertThat(MultilingualStrings.firstValue(
                        address.getAddressLine().get(0).getText()))
                .isEqualTo("F. Rooseveltlaan 3A");
    }

    @Test
    void roundTripsAddressAndTimeZone() {
        Location roundTrip = new Location();
        mapper.toOcpi(mapper.toDatex(sampleLocation()), roundTrip);

        assertThat(roundTrip.getAddress()).isEqualTo("F. Rooseveltlaan 3A");
        assertThat(roundTrip.getCity()).isEqualTo("Amsterdam");
        assertThat(roundTrip.getPostalCode()).isEqualTo("1078NX");
        assertThat(roundTrip.getCountry()).isEqualTo("NLD");
        assertThat(roundTrip.getTimeZone()).isEqualTo("Europe/Amsterdam");
    }

    @Test
    void toDatexReturnsNullWhenNoAddressOrTimeZone() {
        assertThat(mapper.toDatex(new Location())).isNull();
        assertThat(mapper.toDatex(null)).isNull();
    }

    @Test
    void toDatexMapsTimeZoneOnlyWhenAddressAbsent() {
        Location location = new Location();
        location.setTimeZone("Europe/Paris");

        FacilityLocation facility = mapper.toDatex(location);

        assertThat(facility).isNotNull();
        assertThat(facility.getTimeZone()).isEqualTo("Europe/Paris");
        assertThat(facility.getAddress()).isNull();
    }

    @Test
    void toDatexMapsAddressOnlyWhenTimeZoneAbsent() {
        Location location = new Location();
        location.setCity("Berlin");

        FacilityLocation facility = mapper.toDatex(location);

        assertThat(facility).isNotNull();
        assertThat(facility.getTimeZone()).isNull();
        assertThat(MultilingualStrings.firstValue(facility.getAddress().getCity()))
                .isEqualTo("Berlin");
    }

    @Test
    void toOcpiIgnoresNullInputs() {
        Location location = sampleLocation();
        assertThatCode(() -> mapper.toOcpi(null, location)).doesNotThrowAnyException();
        assertThat(location.getCity()).isEqualTo("Amsterdam");
        assertThatCode(() -> mapper.toOcpi(new FacilityLocation(), null)).doesNotThrowAnyException();
    }
}
