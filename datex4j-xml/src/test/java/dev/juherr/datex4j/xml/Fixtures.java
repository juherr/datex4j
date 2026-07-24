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
package dev.juherr.datex4j.xml;

import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/** Builders of small, schema-valid DATEX II values used across the XML tests. */
final class Fixtures {

    private Fixtures() {}

    /**
     * Builds a minimal but schema-valid v3.7 {@link SituationPublication}: it carries the required
     * {@code lang} and {@code modelBaseVersion} attributes and the required {@code publicationTime}
     * and {@code publicationCreator} elements.
     */
    static SituationPublication situationPublication() {
        SituationPublication publication = new SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(dateTime());

        var creator = new dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);

        return publication;
    }

    /** Builds the equivalent minimal, schema-valid v3.5 situation publication. */
    static dev.juherr.datex4j.model.v3_5.situation.SituationPublication situationPublicationV35() {
        var publication = new dev.juherr.datex4j.model.v3_5.situation.SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(dateTime());

        var creator = new dev.juherr.datex4j.model.v3_5.common.InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);

        return publication;
    }

    /** Builds the equivalent minimal, schema-valid v3.6 situation publication. */
    static dev.juherr.datex4j.model.v3_6.situation.SituationPublication situationPublicationV36() {
        var publication = new dev.juherr.datex4j.model.v3_6.situation.SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(dateTime());

        var creator = new dev.juherr.datex4j.model.v3_6.common.InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);

        return publication;
    }

    private static javax.xml.datatype.XMLGregorianCalendar dateTime() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z");
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("No XML datatype factory available", e);
        }
    }
}
