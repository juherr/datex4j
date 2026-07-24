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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.v3_6.common.MultilingualString;
import dev.juherr.datex4j.model.v3_6.common.MultilingualStringValue;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.Connector;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.ElectricChargingPoint;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.EnergyInfrastructureTable;
import dev.juherr.datex4j.model.v3_6.energyinfrastructure.EnergyInfrastructureTablePublication;
import dev.juherr.datex4j.model.v3_6.locationreferencing.PointLocation;
import dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Conformance test for the DATEX II JSON codec: a synthetic {@link MessageContainer} must serialize
 * to the conformant encoding (namespace-prefixed substitution members, {@code G}-suffixed
 * attributes, flattened multilingual strings), and the real Fintraffic AFIR fixture must parse back
 * into a populated model.
 */
class ConformantJsonTest {

    private byte[] fixture() throws Exception {
        try (var in = getClass().getResourceAsStream("/datex-json/finland-afir-messagecontainer.v3_6.json")) {
            return in.readAllBytes();
        }
    }

    @Test
    void syntheticContainerSerializesToConformantJson() {
        DatexJsonMapper mapper = DatexJson.builder()
                .version(DatexVersion.V3_6)
                .prettyPrint(false)
                .build();

        MessageContainer container = new MessageContainer();
        container.getPayload().add(syntheticPublication());

        String json = mapper.writeToString(container);

        assertThat(json).contains("egiEnergyInfrastructureTablePublication");
        assertThat(json).contains("modelBaseVersionG");
        assertThat(json).contains("idG");
        assertThat(json).contains("\"values\":[{\"lang\"");
    }

    @Test
    void realFixtureParsesIntoPopulatedModel() throws Exception {
        DatexJsonMapper mapper = DatexJson.builder().version(DatexVersion.V3_6).build();

        MessageContainer container = mapper.readContainer(fixture(), MessageContainer.class);

        assertThat(container.getPayload()).isNotEmpty();
        assertThat(container.getPayload().get(0)).isInstanceOf(EnergyInfrastructureTablePublication.class);
        EnergyInfrastructureTablePublication publication =
                (EnergyInfrastructureTablePublication) container.getPayload().get(0);
        assertThat(publication.getEnergyInfrastructureTable()).isNotEmpty();
        assertThat(publication.getEnergyInfrastructureTable().get(0).getEnergyInfrastructureSite())
                .isNotEmpty();
    }

    @Test
    void realFixtureContentSurvivesRead() throws Exception {
        DatexJsonMapper mapper = DatexJson.builder().version(DatexVersion.V3_6).build();

        MessageContainer container = mapper.readContainer(fixture(), MessageContainer.class);

        EnergyInfrastructureTablePublication publication =
                (EnergyInfrastructureTablePublication) container.getPayload().get(0);
        EnergyInfrastructureSite site = publication
                .getEnergyInfrastructureTable()
                .get(0)
                .getEnergyInfrastructureSite()
                .get(0);

        // Site identity: the table/site id survives verbatim.
        assertThat(site.getId()).isEqualTo("FI*911*70d5121e-0308-11f0-9e08-42010aa40043");

        // Site name: the flattened MultilingualString survives.
        assertThat(site.getName().getValues().getValue()).anySatisfy(entry -> {
            assertThat(entry.getLang()).isEqualTo("fi");
            assertThat(entry.getValue()).isEqualTo("Porsche Destination Charging Kalastajantorpan Tennisklubi - AC 2");
        });

        // Location: locPointLocation coordinates survive (locxFacilityLocation street address is
        // known to be dropped, see DatexJson's Javadoc and datex-json/README.md).
        assertThat(site.getLocationReference()).isInstanceOf(PointLocation.class);
        PointLocation location = (PointLocation) site.getLocationReference();
        assertThat(location.getPointByCoordinates().getPointCoordinates().getLatitude())
                .isEqualTo(60.1911f);
        assertThat(location.getPointByCoordinates().getPointCoordinates().getLongitude())
                .isEqualTo(24.875f);

        // Connector: the electric charging point's connector standard survives.
        ElectricChargingPoint chargingPoint = (ElectricChargingPoint)
                site.getEnergyInfrastructureStation().get(0).getRefillPoint().get(0);
        Connector connector = chargingPoint.getConnector().get(0);
        assertThat(connector.getConnectorType().getValue().value()).isEqualTo("iec62196T2");
    }

    @Test
    void syntheticContainerRoundTripsThroughUtf8() {
        DatexJsonMapper mapper = DatexJson.builder().version(DatexVersion.V3_6).build();

        MessageContainer container = new MessageContainer();
        container.getPayload().add(syntheticPublication());

        byte[] out = mapper.write(container);
        MessageContainer reparsed = mapper.readContainer(out, MessageContainer.class);
        byte[] out2 = mapper.write(reparsed);

        assertThat(new String(out2, StandardCharsets.UTF_8)).isEqualTo(new String(out, StandardCharsets.UTF_8));
    }

    @Test
    void readContainerWithUnknownMemberKeyThrowsDatexJsonException() {
        DatexJsonMapper mapper = DatexJson.builder().version(DatexVersion.V3_6).build();
        byte[] malformed = "{\"payload\":[{\"unknownXyzMember\":{\"foo\":\"bar\"}}]}".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> mapper.readContainer(malformed, MessageContainer.class))
                .isInstanceOf(DatexJsonException.class);
    }

    private static EnergyInfrastructureTablePublication syntheticPublication() {
        EnergyInfrastructureTablePublication publication = new EnergyInfrastructureTablePublication();
        publication.setModelBaseVersion("3");
        publication.setLang("en");

        EnergyInfrastructureTable table = new EnergyInfrastructureTable();
        table.setId("FI*911");
        table.setVersion("1");

        EnergyInfrastructureSite site = new EnergyInfrastructureSite();
        site.setId("SITE*1");
        site.setVersion("1");
        site.setName(multilingual("en", "Test Site"));

        table.getEnergyInfrastructureSite().add(site);
        publication.getEnergyInfrastructureTable().add(table);
        return publication;
    }

    private static MultilingualString multilingual(String lang, String value) {
        MultilingualStringValue entry = new MultilingualStringValue();
        entry.setLang(lang);
        entry.setValue(value);
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(entry);
        MultilingualString multilingual = new MultilingualString();
        multilingual.setValues(values);
        return multilingual;
    }
}
