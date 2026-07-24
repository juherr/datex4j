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
package dev.juherr.datex4j.model.v3_7.spi;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.spi.DatexModelProvider;
import jakarta.xml.bind.JAXBElement;

/**
 * {@link DatexModelProvider} for the DATEX II v3_7 generated model. Registered through {@link
 * java.util.ServiceLoader}; see {@code META-INF/services/dev.juherr.datex4j.model.spi.DatexModelProvider}.
 */
public final class DatexModelProviderV37 implements DatexModelProvider {

    /** Colon-separated JAXB context path over this version's generated model packages. */
    private static final String CONTEXT_PATH = String.join(
            ":",
            "dev.juherr.datex4j.model.v3_7.common",
            "dev.juherr.datex4j.model.v3_7.commonextension",
            "dev.juherr.datex4j.model.v3_7.locationreferencing",
            "dev.juherr.datex4j.model.v3_7.locationextension",
            "dev.juherr.datex4j.model.v3_7.situation",
            "dev.juherr.datex4j.model.v3_7.facilities",
            "dev.juherr.datex4j.model.v3_7.energyinfrastructure",
            "dev.juherr.datex4j.model.v3_7.parking",
            "dev.juherr.datex4j.model.v3_7.roadtrafficdata",
            "dev.juherr.datex4j.model.v3_7.vms",
            "dev.juherr.datex4j.model.v3_7.faultandstatus",
            "dev.juherr.datex4j.model.v3_7.reroutingmanagementenhanced",
            "dev.juherr.datex4j.model.v3_7.trafficmanagementplan",
            "dev.juherr.datex4j.model.v3_7.urbanextensions",
            "dev.juherr.datex4j.model.v3_7.d2payload",
            "dev.juherr.datex4j.model.v3_7.controlledzone",
            "dev.juherr.datex4j.model.v3_7.trafficregulation",
            "dev.juherr.datex4j.model.v3_7.cisinformation",
            "dev.juherr.datex4j.model.v3_7.exchangeinformation",
            "dev.juherr.datex4j.model.v3_7.informationmanagement",
            "dev.juherr.datex4j.model.v3_7.messagecontainer",
            "dev.juherr.datex4j.model.v3_7.afirenergyinfrastructure",
            "dev.juherr.datex4j.model.v3_7.afirfacilities");

    @Override
    public DatexVersion version() {
        return DatexVersion.V3_7;
    }

    @Override
    public String contextPath() {
        return CONTEXT_PATH;
    }

    @Override
    public boolean isPayloadPublication(Object value) {
        return value instanceof dev.juherr.datex4j.model.v3_7.common.PayloadPublication;
    }

    @Override
    public JAXBElement<?> wrapAsPayload(Object value) {
        return new dev.juherr.datex4j.model.v3_7.d2payload.ObjectFactory()
                .createPayload((dev.juherr.datex4j.model.v3_7.common.PayloadPublication) value);
    }

    @Override
    public boolean isMessageContainer(Object value) {
        return value instanceof dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer;
    }

    @Override
    public JAXBElement<?> wrapAsMessageContainer(Object value) {
        return new dev.juherr.datex4j.model.v3_7.messagecontainer.ObjectFactory()
                .createMessageContainer((dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer) value);
    }
}
