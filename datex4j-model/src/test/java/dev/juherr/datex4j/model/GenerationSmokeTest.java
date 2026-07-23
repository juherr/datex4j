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
package dev.juherr.datex4j.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the JAXB code generation produced usable classes for every bundled DATEX II version.
 * This is a guard against a silently broken generation pipeline, not a test of DATEX II semantics.
 */
class GenerationSmokeTest {

    @Test
    void v37ConcretePublicationExtendsPayloadPublication() {
        var publication = new dev.juherr.datex4j.model.v3_7.situation.SituationPublication();
        assertThat(publication).isInstanceOf(dev.juherr.datex4j.model.v3_7.common.PayloadPublication.class);
    }

    @Test
    void v36ConcretePublicationExtendsPayloadPublication() {
        var publication = new dev.juherr.datex4j.model.v3_6.situation.SituationPublication();
        assertThat(publication).isInstanceOf(dev.juherr.datex4j.model.v3_6.common.PayloadPublication.class);
    }

    @Test
    void v37ObjectFactoryWrapsPayloadRootElement() {
        var publication = new dev.juherr.datex4j.model.v3_7.situation.SituationPublication();
        JAXBElement<?> payload = new dev.juherr.datex4j.model.v3_7.d2payload.ObjectFactory().createPayload(publication);

        assertThat(payload).isNotNull();
        assertThat(payload.getName().getNamespaceURI()).isEqualTo("http://datex2.eu/schema/3/d2Payload");
        assertThat(payload.getName().getLocalPart()).isEqualTo("payload");
    }

    @Test
    void v36ObjectFactoryWrapsPayloadRootElement() {
        var publication = new dev.juherr.datex4j.model.v3_6.situation.SituationPublication();
        JAXBElement<?> payload = new dev.juherr.datex4j.model.v3_6.d2payload.ObjectFactory().createPayload(publication);

        assertThat(payload).isNotNull();
        assertThat(payload.getName().getNamespaceURI()).isEqualTo("http://datex2.eu/schema/3/d2Payload");
        assertThat(payload.getName().getLocalPart()).isEqualTo("payload");
    }
}
