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

import dev.juherr.datex4j.json.DatexJson;
import dev.juherr.datex4j.json.DatexJsonMapper;
import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;

/** Writes and reads a DATEX II publication with the conformant JSON facade. */
public final class JsonExample {

    private JsonExample() {}

    /**
     * Runs the example and prints the generated JSON.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        DatexJsonMapper mapper = DatexJson.builder().prettyPrint(true).build();
        SituationPublication publication = samplePublication();

        String json = mapper.writeToString(publication);
        System.out.println(json);

        SituationPublication restored =
                mapper.read(json.getBytes(java.nio.charset.StandardCharsets.UTF_8), SituationPublication.class);
        System.out.println("Read back: " + restored.getPublicationCreator().getNationalIdentifier());
    }

    static SituationPublication roundTrip() {
        DatexJsonMapper mapper = DatexJson.createMapper();
        return mapper.read(mapper.write(samplePublication()), SituationPublication.class);
    }

    private static SituationPublication samplePublication() {
        SituationPublication publication = DatexExample.sampleSituationPublication();
        InternationalIdentifier creator = publication.getPublicationCreator();
        creator.setNationalIdentifier("datex4j-json-example");
        return publication;
    }
}
