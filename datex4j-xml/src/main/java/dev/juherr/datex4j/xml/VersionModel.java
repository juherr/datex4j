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

import dev.juherr.datex4j.core.DatexVersion;
import jakarta.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Per-version bridge to the generated DATEX II model.
 *
 * <p>Each supported {@link DatexVersion} is generated into its own package tree ({@code
 * dev.juherr.datex4j.model.v3_6.*}, {@code ...v3_7.*}), so the {@code PayloadPublication} base type
 * and the {@code payload} {@code ObjectFactory} differ per version. This enum encapsulates those
 * differences: the JAXB context path, how to recognize a publication, and how to wrap it in the
 * {@code payload} root element.
 */
enum VersionModel {

    /** DATEX II v3.0 model (Common, LocationReferencing, Situation only). */
    V3_0("v3_0", ModelPackages.V3_0) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_0.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_0.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_0.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.1 model (adds RoadTrafficData and Vms). */
    V3_1("v3_1", ModelPackages.V3_1) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_1.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_1.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_1.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.2 model (adds CommonExtension, Location/EnergyInfrastructure, Facilities, TrafficRegulation). */
    V3_2("v3_2", ModelPackages.V3_2) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_2.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_2.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_2.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.3 model (adds Parking). */
    V3_3("v3_3", ModelPackages.V3_3) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_3.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_3.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_3.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.4 model (drops TrafficRegulation relative to v3.3). */
    V3_4("v3_4", ModelPackages.V3_4) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_4.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_4.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_4.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.5 model (no ControlledZone, TrafficRegulation, MessageContainer family or AFIR). */
    V3_5("v3_5", ModelPackages.V3_5) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_5.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_5.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_5.common.PayloadPublication) value);
        }
    },

    /** DATEX II v3.6 model (adds the Exchange 2020 MessageContainer family; no AFIR modules). */
    V3_6("v3_6", ModelPackages.V3_6) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_6.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_6.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_6.common.PayloadPublication) value);
        }

        @Override
        boolean isMessageContainer(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer;
        }

        @Override
        JAXBElement<?> wrapAsMessageContainer(Object value) {
            return new dev.juherr.datex4j.model.v3_6.messagecontainer.ObjectFactory()
                    .createMessageContainer((dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer) value);
        }
    },

    /** DATEX II v3.7 model (adds the AFIR modules; keeps the Exchange 2020 MessageContainer family). */
    V3_7("v3_7", ModelPackages.V3_7) {
        @Override
        boolean isPayloadPublication(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_7.common.PayloadPublication;
        }

        @Override
        JAXBElement<?> wrapAsPayload(Object value) {
            return new dev.juherr.datex4j.model.v3_7.d2payload.ObjectFactory()
                    .createPayload((dev.juherr.datex4j.model.v3_7.common.PayloadPublication) value);
        }

        @Override
        boolean isMessageContainer(Object value) {
            return value instanceof dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer;
        }

        @Override
        JAXBElement<?> wrapAsMessageContainer(Object value) {
            return new dev.juherr.datex4j.model.v3_7.messagecontainer.ObjectFactory()
                    .createMessageContainer((dev.juherr.datex4j.model.v3_7.messagecontainer.MessageContainer) value);
        }
    };

    private final String contextPath;

    VersionModel(String packageSegment, List<String> modules) {
        this.contextPath = ModelPackages.contextPath(packageSegment, modules);
    }

    /** Returns the enum constant that backs the given public {@link DatexVersion}. */
    static VersionModel of(DatexVersion version) {
        return switch (version) {
            case V3_0 -> V3_0;
            case V3_1 -> V3_1;
            case V3_2 -> V3_2;
            case V3_3 -> V3_3;
            case V3_4 -> V3_4;
            case V3_5 -> V3_5;
            case V3_6 -> V3_6;
            case V3_7 -> V3_7;
        };
    }

    /** Returns the colon-separated JAXB context path for this version's model packages. */
    String contextPath() {
        return contextPath;
    }

    /** Tells whether the value is a {@code PayloadPublication} of this version's model. */
    abstract boolean isPayloadPublication(Object value);

    /** Wraps a {@code PayloadPublication} of this version in its {@code payload} root element. */
    abstract JAXBElement<?> wrapAsPayload(Object value);

    /**
     * Tells whether the value is a {@code MessageContainer} of this version's model. Only DATEX II
     * v3.6 and v3.7 ship the Exchange 2020 {@code MessageContainer} family; earlier versions return
     * {@code false}.
     */
    boolean isMessageContainer(Object value) {
        return false;
    }

    /**
     * Wraps a {@code MessageContainer} of this version in its {@code messageContainer} root element.
     * Only supported for DATEX II v3.6 and v3.7.
     */
    JAXBElement<?> wrapAsMessageContainer(Object value) {
        throw new IllegalArgumentException("MessageContainer is not part of the DATEX II " + name() + " model");
    }

    /**
     * Builds JAXB context paths from the generated package layout. Kept in a nested class so its
     * arrays are fully initialized before the enum constants reference them at construction time.
     */
    private static final class ModelPackages {

        /** Module package suffixes published by DATEX II v3.5, the oldest supported version. */
        private static final List<String> BASE = List.of(
                "common",
                "commonextension",
                "locationreferencing",
                "locationextension",
                "situation",
                "facilities",
                "energyinfrastructure",
                "parking",
                "roadtrafficdata",
                "vms",
                "faultandstatus",
                "reroutingmanagementenhanced",
                "trafficmanagementplan",
                "urbanextensions",
                "d2payload");

        /**
         * Module sets for DATEX II v3.0 through v3.4. These predate the v3.5 BASE set and do not
         * layer cleanly onto it (for example {@code trafficregulation} appears in v3.2/v3.3, is
         * dropped in v3.4/v3.5, then reappears in v3.6), so each is derived directly from the
         * xs:include/xs:import graph of that version's root DATEXII_3_D2Payload.xsd.
         */
        static final List<String> V3_0 = List.of("common", "locationreferencing", "situation", "d2payload");

        /** Module set for DATEX II v3.1. */
        static final List<String> V3_1 =
                List.of("common", "locationreferencing", "situation", "roadtrafficdata", "vms", "d2payload");

        /** Module set for DATEX II v3.2. */
        static final List<String> V3_2 = List.of(
                "common",
                "commonextension",
                "locationreferencing",
                "locationextension",
                "situation",
                "facilities",
                "energyinfrastructure",
                "roadtrafficdata",
                "vms",
                "trafficregulation",
                "d2payload");

        /** Module set for DATEX II v3.3 (adds parking to v3.2). */
        static final List<String> V3_3 = concat(V3_2, List.of("parking"));

        /** Module set for DATEX II v3.4 (v3.3 without trafficregulation). */
        static final List<String> V3_4 = List.of(
                "common",
                "commonextension",
                "locationreferencing",
                "locationextension",
                "situation",
                "facilities",
                "energyinfrastructure",
                "parking",
                "roadtrafficdata",
                "vms",
                "d2payload");

        /** Module package suffixes added by DATEX II v3.6. */
        private static final List<String> V3_6_ADDITIONS = List.of("controlledzone", "trafficregulation");

        /**
         * Exchange 2020 module package suffixes, introduced in DATEX II v3.6. These carry the {@code
         * MessageContainer} root and its {@code exchangeInformation} envelope; they must be on the
         * JAXB context path for the {@code messageContainer} root element to be recognized.
         */
        private static final List<String> EXCHANGE_2020 =
                List.of("cisinformation", "exchangeinformation", "informationmanagement", "messagecontainer");

        /** Module package suffixes added by DATEX II v3.7 (AFIR). */
        private static final List<String> AFIR = List.of("afirenergyinfrastructure", "afirfacilities");

        /** Module set for DATEX II v3.5. */
        static final List<String> V3_5 = BASE;

        /** Module set for DATEX II v3.6. */
        static final List<String> V3_6 = concat(concat(BASE, V3_6_ADDITIONS), EXCHANGE_2020);

        /** Module set for DATEX II v3.7. */
        static final List<String> V3_7 = concat(V3_6, AFIR);

        private ModelPackages() {}

        private static List<String> concat(List<String> base, List<String> additions) {
            List<String> modules = new ArrayList<>(base);
            modules.addAll(additions);
            return List.copyOf(modules);
        }

        static String contextPath(String packageSegment, List<String> modules) {
            return modules.stream()
                    .map(module -> "dev.juherr.datex4j.model." + packageSegment + "." + module)
                    .collect(Collectors.joining(":"));
        }
    }
}
