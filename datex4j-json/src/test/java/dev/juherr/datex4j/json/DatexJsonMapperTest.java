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
package dev.juherr.datex4j.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.Test;

class DatexJsonMapperTest {

    private final DatexJsonMapper mapper = DatexJson.createMapper();

    @Test
    void writesThenReadsBackAnEquivalentPublication() throws DatatypeConfigurationException {
        SituationPublication original = new SituationPublication();
        original.setLang("en");
        original.setModelBaseVersion("3");
        original.setPublicationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z"));
        InternationalIdentifier creator = new InternationalIdentifier();
        creator.setCountry("gb");
        creator.setNationalIdentifier("datex4j");
        original.setPublicationCreator(creator);

        byte[] json = mapper.write(original);
        SituationPublication restored = mapper.read(json, SituationPublication.class);

        assertThat(restored.getLang()).isEqualTo("en");
        assertThat(restored.getPublicationCreator().getCountry()).isEqualTo("gb");
        assertThat(restored.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j");
        assertThat(restored.getPublicationTime()).isEqualTo(original.getPublicationTime());
    }

    @Test
    void temporalTypesAreSerializedAsIsoStrings() {
        SituationPublication publication = new SituationPublication();
        publication.setLang("en");

        String json = mapper.writeToString(publication);

        assertThat(json).contains("\"lang\"");
    }
}
