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
package dev.juherr.datex4j.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.juherr.datex4j.model.v3_7.parking.ParkingTablePublication;
import dev.juherr.datex4j.xml.DatexMarshaller;
import dev.juherr.datex4j.xml.DatexXml;
import org.junit.jupiter.api.Test;

class ParkingTablePublicationBuilderTest {

    @Test
    void buildsAndRoundtripsThroughDatexXml() {
        ParkingTablePublication publication = ParkingTablePublicationBuilder.parkingTablePublication()
                .publishedBy("gb", "datex4j-parking")
                .build();

        DatexMarshaller marshaller = DatexXml.createMarshaller();
        byte[] xml = marshaller.write(publication);
        ParkingTablePublication restored = marshaller.read(xml, ParkingTablePublication.class);

        assertEquals("datex4j-parking", restored.getPublicationCreator().getNationalIdentifier());
    }
}
