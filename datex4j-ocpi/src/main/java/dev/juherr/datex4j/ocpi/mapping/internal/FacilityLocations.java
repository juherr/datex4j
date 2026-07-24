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

import dev.juherr.datex4j.model.v3_7.locationextension.FacilityLocation;
import dev.juherr.datex4j.model.v3_7.locationreferencing.LocationReference;
import dev.juherr.datex4j.model.v3_7.locationreferencing._LocationReferenceExtensionType;

/**
 * Reads and attaches the DATEX II {@link FacilityLocation} carried by a {@link LocationReference}'s
 * typed extension slot ({@code _locationReferenceExtension / facilityLocation}). This is the
 * location-extension anchor for street address and time zone on an energy-infrastructure site.
 */
public final class FacilityLocations {

    private FacilityLocations() {}

    /** Returns the {@link FacilityLocation} carried by {@code ref}, or {@code null} if absent. */
    public static FacilityLocation of(LocationReference ref) {
        if (ref == null) {
            return null;
        }
        _LocationReferenceExtensionType extension = ref.get_LocationReferenceExtension();
        return extension == null ? null : extension.getFacilityLocation();
    }

    /**
     * Attaches {@code facility} to {@code ref}'s typed extension slot, creating the extension holder
     * on demand. No-op if either argument is {@code null} (a {@code null} {@code ref} means the site
     * has no location reference to anchor the facility location onto).
     */
    public static void attach(LocationReference ref, FacilityLocation facility) {
        if (ref == null || facility == null) {
            return;
        }
        _LocationReferenceExtensionType extension = ref.get_LocationReferenceExtension();
        if (extension == null) {
            extension = new _LocationReferenceExtensionType();
            ref.set_LocationReferenceExtension(extension);
        }
        extension.setFacilityLocation(facility);
    }
}
