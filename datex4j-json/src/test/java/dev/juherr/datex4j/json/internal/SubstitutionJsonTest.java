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
package dev.juherr.datex4j.json.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.juherr.datex4j.model.v3_6.facilities.OrganisationSpecification;
import dev.juherr.datex4j.model.v3_6.locationextension.FacilityLocation;
import org.junit.jupiter.api.Test;

class SubstitutionJsonTest {

    @Test
    void buildsMemberKeyFromPrefixAndConcreteSimpleName() {
        assertThat(SubstitutionJson.memberKey(new FacilityLocation())).isEqualTo("locxFacilityLocation");
        assertThat(SubstitutionJson.memberKey(new OrganisationSpecification()))
                .isEqualTo("facOrganisationSpecification");
    }

    @Test
    void resolvesClassFromMemberKeyAndVersionSegment() {
        assertThat(SubstitutionJson.resolveClass("locxFacilityLocation", "v3_6"))
                .isEqualTo(FacilityLocation.class);
        assertThat(SubstitutionJson.resolveClass("facOrganisationSpecification", "v3_6"))
                .isEqualTo(OrganisationSpecification.class);
    }

    @Test
    void roundTripsMemberKeyAndResolveClassForBothFixtures() {
        String facilityLocationKey = SubstitutionJson.memberKey(new FacilityLocation());
        assertThat(SubstitutionJson.resolveClass(facilityLocationKey, "v3_6")).isEqualTo(FacilityLocation.class);

        String organisationSpecificationKey = SubstitutionJson.memberKey(new OrganisationSpecification());
        assertThat(SubstitutionJson.resolveClass(organisationSpecificationKey, "v3_6"))
                .isEqualTo(OrganisationSpecification.class);
    }

    @Test
    void rejectsUnknownPrefixWhenResolvingClass() {
        assertThatThrownBy(() -> SubstitutionJson.resolveClass("zzzSomething", "v3_6"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
