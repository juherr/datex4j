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

import dev.juherr.datex4j.model.v3_7.locationextension.Address;
import dev.juherr.datex4j.model.v3_7.locationextension.AddressLine;
import dev.juherr.datex4j.model.v3_7.locationextension.AddressLineTypeEnum;
import dev.juherr.datex4j.model.v3_7.locationextension.FacilityLocation;
import dev.juherr.datex4j.model.v3_7.locationextension._AddressLineTypeEnum;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.model.v2_3.Location;
import java.math.BigInteger;

/**
 * Maps the street-address and time-zone scalars of an OCPI {@link Location} to a DATEX II {@link
 * FacilityLocation} (the location-extension type carrying {@code timeZone} and {@code Address}) and
 * back. The mapping follows the IDACS deliverable 2.2.1 static dataset, which anchors address and
 * time zone under the site's location reference.
 *
 * <p>The OCPI free-form {@code address} line becomes a single {@link AddressLine} of type {@code
 * street}. OCPI {@code country} (ISO 3166-1 alpha-3) is carried on {@code countryCode} as-is; DATEX
 * II expresses country codes without prescribing the alphabet here, so no code conversion is done.
 */
public final class AddressMapper {

    private static final String DEFAULT_LANG = "en";

    /**
     * Builds a {@link FacilityLocation} from {@code location}'s address and time-zone fields, or
     * {@code null} if {@code location} is null or carries neither an address nor a time zone.
     */
    public FacilityLocation toDatex(Location location) {
        if (location == null) {
            return null;
        }
        Address address = toDatexAddress(location);
        String timeZone = blankToNull(location.getTimeZone());
        if (address == null && timeZone == null) {
            return null;
        }
        FacilityLocation facility = new FacilityLocation();
        facility.setAddress(address);
        facility.setTimeZone(timeZone);
        return facility;
    }

    /**
     * Writes {@code facility}'s address and time zone back onto {@code location}. No-op if either
     * argument is {@code null}.
     */
    public void toOcpi(FacilityLocation facility, Location location) {
        if (facility == null || location == null) {
            return;
        }
        location.setTimeZone(facility.getTimeZone());
        Address address = facility.getAddress();
        if (address == null) {
            return;
        }
        location.setAddress(firstAddressLine(address));
        location.setCity(MultilingualStrings.firstValue(address.getCity()));
        location.setPostalCode(address.getPostcode());
        location.setCountry(address.getCountryCode());
    }

    private static Address toDatexAddress(Location location) {
        String street = blankToNull(location.getAddress());
        String city = blankToNull(location.getCity());
        String postcode = blankToNull(location.getPostalCode());
        String country = blankToNull(location.getCountry());
        if (street == null && city == null && postcode == null && country == null) {
            return null;
        }
        Address address = new Address();
        address.setPostcode(postcode);
        address.setCity(MultilingualStrings.of(DEFAULT_LANG, city));
        address.setCountryCode(country);
        if (street != null) {
            address.getAddressLine().add(streetLine(street));
        }
        return address;
    }

    private static AddressLine streetLine(String street) {
        _AddressLineTypeEnum type = new _AddressLineTypeEnum();
        type.setValue(AddressLineTypeEnum.STREET);
        AddressLine line = new AddressLine();
        line.setType(type);
        line.setText(MultilingualStrings.of(DEFAULT_LANG, street));
        line.setOrder(BigInteger.ONE);
        return line;
    }

    private static String firstAddressLine(Address address) {
        for (AddressLine line : address.getAddressLine()) {
            String text = MultilingualStrings.firstValue(line.getText());
            if (text != null) {
                return text;
            }
        }
        return null;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
