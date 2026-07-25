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
package dev.juherr.datex4j.model.v2_1.spi;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.spi.DatexModelProvider;
import dev.juherr.datex4j.model.v2_1.D2LogicalModel;
import dev.juherr.datex4j.model.v2_1.ObjectFactory;
import dev.juherr.datex4j.model.v2_1.PayloadPublication;
import jakarta.xml.bind.JAXBElement;

/**
 * {@link DatexModelProvider} for the DATEX II v2_1 generated model. Registered through {@link
 * java.util.ServiceLoader}; see {@code META-INF/services/dev.juherr.datex4j.model.spi.DatexModelProvider}.
 *
 * <p>DATEX II v2 ships a single, monolithic schema whose whole model lives in one package, so the
 * JAXB context path is that single package. Unlike v3 (where the {@code payload} element is itself
 * the document root), a v2 {@code PayloadPublication} is not a document root: the root is {@code
 * d2LogicalModel}, which nests the publication. {@link #wrapAsPayload(Object)} therefore builds a
 * {@link D2LogicalModel} carrying the publication and returns its root {@link JAXBElement}.
 */
public final class DatexModelProviderV21 implements DatexModelProvider {

    @Override
    public DatexVersion version() {
        return DatexVersion.V2_1;
    }

    @Override
    public String contextPath() {
        return "dev.juherr.datex4j.model.v2_1";
    }

    @Override
    public boolean isPayloadPublication(Object value) {
        return value instanceof PayloadPublication;
    }

    @Override
    public JAXBElement<?> wrapAsPayload(Object value) {
        ObjectFactory factory = new ObjectFactory();
        D2LogicalModel root = factory.createD2LogicalModel();
        root.setPayloadPublication((PayloadPublication) value);
        return factory.createD2LogicalModel(root);
    }
}
