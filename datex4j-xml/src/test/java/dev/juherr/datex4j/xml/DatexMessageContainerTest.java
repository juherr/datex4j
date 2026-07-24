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

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer;
import dev.juherr.datex4j.model.v3_7.situation.SituationPublication;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;

/** Reads and round-trips DATEX II v3.7 Exchange 2020 {@code MessageContainer} documents. */
class DatexMessageContainerTest {

    private final DatexMarshaller marshaller = DatexXml.createMarshaller();

    @Test
    void readsAStandardMessageContainer() {
        MessageContainer container = marshaller.read(fixture("message-container-v3_7.xml"), MessageContainer.class);

        assertThat(container.getModelBaseVersion()).isEqualTo("3");
        assertThat(container.getPayload()).hasSize(1);
        assertThat(container.getPayload().get(0)).isInstanceOf(SituationPublication.class);
        assertThat(container.getExchangeInformation()).isNotNull();

        SituationPublication payload =
                (SituationPublication) container.getPayload().get(0);
        assertThat(payload.getLang()).isEqualTo("en");
        assertThat(payload.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j");
    }

    @Test
    void roundTripsAStandardMessageContainer() {
        MessageContainer original = marshaller.read(fixture("message-container-v3_7.xml"), MessageContainer.class);

        byte[] rewritten = marshaller.write(original);
        MessageContainer restored = marshaller.read(rewritten, MessageContainer.class);

        assertThat(restored.getModelBaseVersion()).isEqualTo("3");
        assertThat(restored.getPayload()).hasSize(1);
        assertThat(restored.getPayload().get(0)).isInstanceOf(SituationPublication.class);
        assertThat(restored.getExchangeInformation()).isNotNull();

        SituationPublication payload =
                (SituationPublication) restored.getPayload().get(0);
        assertThat(payload.getLang()).isEqualTo("en");
        assertThat(payload.getPublicationTime())
                .isEqualTo(((SituationPublication) original.getPayload().get(0)).getPublicationTime());
        assertThat(payload.getPublicationCreator().getCountry()).isEqualTo("gb");
        assertThat(payload.getPublicationCreator().getNationalIdentifier()).isEqualTo("datex4j");
    }

    @Test
    void writesAMessageContainerToRootedXml() {
        MessageContainer container = marshaller.read(fixture("message-container-v3_7.xml"), MessageContainer.class);

        String xml = marshaller.writeToString(container);

        assertThat(xml).contains("messageContainer").contains("http://datex2.eu/schema/3/messageContainer");
    }

    @Test
    void dropsUnknownNationalExtensionsAndStillReads() {
        MessageContainer container =
                marshaller.read(fixture("message-container-v3_7-nl-extensions.xml"), MessageContainer.class);

        assertThat(container.getPayload()).hasSize(1);
        SituationPublication payload =
                (SituationPublication) container.getPayload().get(0);
        assertThat(payload.getPublicationCreator().getCountry()).isEqualTo("nl");
        assertThat(container.getExchangeInformation()).isNotNull();
    }

    private static byte[] fixture(String name) {
        try (InputStream in = DatexMessageContainerTest.class.getResourceAsStream("/messagecontainer/" + name)) {
            if (in == null) {
                throw new IllegalStateException("Missing test fixture: " + name);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
