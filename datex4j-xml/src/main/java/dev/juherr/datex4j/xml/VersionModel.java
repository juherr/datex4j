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

    /** DATEX II v3.6 model (no AFIR modules). */
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
    },

    /** DATEX II v3.7 model (adds the AFIR modules). */
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
    };

    private final String contextPath;

    VersionModel(String packageSegment, List<String> modules) {
        this.contextPath = ModelPackages.contextPath(packageSegment, modules);
    }

    /** Returns the enum constant that backs the given public {@link DatexVersion}. */
    static VersionModel of(DatexVersion version) {
        return switch (version) {
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

        /** Module package suffixes added by DATEX II v3.6. */
        private static final List<String> V3_6_ADDITIONS = List.of("controlledzone", "trafficregulation");

        /** Module package suffixes added by DATEX II v3.7 (AFIR). */
        private static final List<String> AFIR = List.of("afirenergyinfrastructure", "afirfacilities");

        /** Module set for DATEX II v3.5. */
        static final List<String> V3_5 = BASE;

        /** Module set for DATEX II v3.6. */
        static final List<String> V3_6 = concat(BASE, V3_6_ADDITIONS);

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
