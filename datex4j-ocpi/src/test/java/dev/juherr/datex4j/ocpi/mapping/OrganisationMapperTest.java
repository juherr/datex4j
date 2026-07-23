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

import dev.juherr.datex4j.model.v3_7.facilities.Organisation;
import dev.juherr.datex4j.model.v3_7.facilities.OrganisationSpecification;
import dev.juherr.datex4j.ocpi.model.v2_3.BusinessDetails;
import dev.juherr.datex4j.ocpi.model.v2_3.Image;
import java.net.URI;
import org.junit.jupiter.api.Test;

class OrganisationMapperTest {

    private final OrganisationMapper mapper = new OrganisationMapper();

    private static BusinessDetails sampleBusinessDetails() {
        BusinessDetails details = new BusinessDetails();
        details.setName("Acme Charging");
        details.setWebsite(URI.create("https://acme.example"));
        Image logo = new Image();
        logo.setUrl(URI.create("https://acme.example/logo.png"));
        details.setLogo(logo);
        return details;
    }

    @Test
    void toDatexBuildsOrganisationWithNameWebsiteAndLogo() {
        Organisation organisation = mapper.toDatex(sampleBusinessDetails());

        assertThat(organisation).isInstanceOf(OrganisationSpecification.class);
        OrganisationSpecification spec = (OrganisationSpecification) organisation;
        assertThat(spec.getName().getValues().getValue().get(0).getValue()).isEqualTo("Acme Charging");
        assertThat(spec.getLinkToWebform()).isEqualTo("https://acme.example");
        assertThat(spec.getLinkToLogo()).isEqualTo("https://acme.example/logo.png");
    }

    @Test
    void roundTripsNameAndWebsite() {
        BusinessDetails roundTrip = mapper.toOcpi(mapper.toDatex(sampleBusinessDetails()));

        assertThat(roundTrip.getName()).isEqualTo("Acme Charging");
        assertThat(roundTrip.getWebsite()).isEqualTo(URI.create("https://acme.example"));
        assertThat(roundTrip.getLogo().getUrl()).isEqualTo(URI.create("https://acme.example/logo.png"));
    }

    @Test
    void toDatexHandlesMissingWebsiteAndLogo() {
        BusinessDetails details = new BusinessDetails();
        details.setName("Acme Charging");

        Organisation organisation = mapper.toDatex(details);

        OrganisationSpecification spec = (OrganisationSpecification) organisation;
        assertThat(spec.getLinkToWebform()).isNull();
        assertThat(spec.getLinkToLogo()).isNull();
    }

    @Test
    void toOcpiReturnsNullForNonAnOrganisationSubtype() {
        Organisation organisation = new Organisation() {};

        assertThat(mapper.toOcpi(organisation)).isNull();
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
