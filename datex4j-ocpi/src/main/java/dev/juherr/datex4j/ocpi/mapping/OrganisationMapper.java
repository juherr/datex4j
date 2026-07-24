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

import dev.juherr.datex4j.model.v3_7.facilities.Organisation;
import dev.juherr.datex4j.model.v3_7.facilities.OrganisationSpecification;
import dev.juherr.datex4j.ocpi.mapping.internal.MultilingualStrings;
import dev.juherr.datex4j.ocpi.mapping.internal.Organisations;
import dev.juherr.datex4j.ocpi.mapping.internal.Uris;
import dev.juherr.datex4j.ocpi.model.v2_3.BusinessDetails;
import dev.juherr.datex4j.ocpi.model.v2_3.Image;
import java.net.URI;

/**
 * Maps an OCPI {@link BusinessDetails} to a DATEX II {@link Organisation} and back.
 *
 * <p>DATEX II {@link Organisation} is abstract; the concrete {@link OrganisationSpecification} is
 * used to carry the name, website and logo link. {@code toOcpi} only understands {@link
 * OrganisationSpecification}: any other {@link Organisation} subtype yields {@code null}.
 *
 * <p><b>Unmapped fields.</b> OCPI has no equivalent for DATEX II {@code externalCode}, {@code
 * legalName}, {@code description}, {@code linkToGeneralInformation}, {@code available24hours},
 * {@code responsibility}, {@code publishingAgreement}, {@code type}, {@code
 * nationalOrganisationNumber}, {@code nationalRegister}, {@code vatIdentificationNumber}, {@code
 * organisationUnit}, {@code subOrganisation}, {@code id}, {@code version}; these are left unset.
 */
public final class OrganisationMapper {

    private static final String DEFAULT_LANG = "en";

    /** Builds a DATEX II organisation from {@code details}, or {@code null} if {@code details} is null. */
    public Organisation toDatex(BusinessDetails details) {
        if (details == null) {
            return null;
        }
        OrganisationSpecification organisation = new OrganisationSpecification();
        organisation.setName(MultilingualStrings.of(DEFAULT_LANG, details.getName()));
        if (details.getWebsite() != null) {
            organisation.setLinkToWebform(details.getWebsite().toString());
        }
        if (details.getLogo() != null && details.getLogo().getUrl() != null) {
            organisation.setLinkToLogo(details.getLogo().getUrl().toString());
        }
        return organisation;
    }

    /**
     * Builds OCPI business details from {@code organisation}, or {@code null} if {@code
     * organisation} is null or not an {@link OrganisationSpecification}.
     */
    public BusinessDetails toOcpi(Organisation organisation) {
        if (!(organisation instanceof OrganisationSpecification spec)) {
            return null;
        }
        BusinessDetails details = new BusinessDetails();
        details.setName(Organisations.nameOf(organisation));
        details.setWebsite(Uris.parse(spec.getLinkToWebform()));
        URI logoUri = Uris.parse(spec.getLinkToLogo());
        if (logoUri != null) {
            Image logo = new Image();
            logo.setUrl(logoUri);
            details.setLogo(logo);
        }
        return details;
    }
}
