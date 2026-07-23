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
package dev.juherr.datex4j.examples;

import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import java.nio.charset.StandardCharsets;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Minimal end-to-end example: build a DATEX II publication, write it to validated XML, then read it
 * back. Run with {@code ./mvnw -pl examples exec:java}.
 */
public final class DatexExample {

    private DatexExample() {}

    /**
     * Runs the example and prints the produced XML.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        // A single marshaller can be shared; here we enable schema validation on read and write.
        DatexMarshaller marshaller = DatexXml.builder().validating(true).build();

        SituationPublication publication = sampleSituationPublication();

        // Write: the publication is automatically wrapped in the DATEX II <payload> root element.
        String xml = marshaller.writeToString(publication);
        System.out.println(xml);

        // Read: parse the document back into a typed publication.
        SituationPublication restored =
                marshaller.read(xml.getBytes(StandardCharsets.UTF_8), SituationPublication.class);
        System.out.println("Read back a publication from: "
                + restored.getPublicationCreator().getNationalIdentifier());
    }

    /** Builds a small, schema-valid situation publication. */
    static SituationPublication sampleSituationPublication() {
        SituationPublication publication = new SituationPublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(now());

        InternationalIdentifier creator = new InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j-examples");
        publication.setPublicationCreator(creator);

        return publication;
    }

    private static XMLGregorianCalendar now() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z");
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("No XML datatype factory available", e);
        }
    }
}
