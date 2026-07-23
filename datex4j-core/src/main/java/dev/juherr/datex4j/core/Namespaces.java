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
package dev.juherr.datex4j.core;

/**
 * DATEX II version 3 XML namespace URIs.
 *
 * <p>Every DATEX II v3 module lives in its own namespace under the shared {@link #BASE} prefix, for
 * example {@code http://datex2.eu/schema/3/common}. These constants let callers reference
 * namespaces without repeating string literals; {@link #module(String)} builds the URI for any
 * module by name.
 */
public final class Namespaces {

    /** Common prefix shared by all DATEX II v3 module namespaces: {@value}. */
    public static final String BASE = "http://datex2.eu/schema/3";

    /** Namespace of the {@code Common} module. */
    public static final String COMMON = module("common");

    /** Namespace of the {@code CommonExtension} module. */
    public static final String COMMON_EXTENSION = module("commonExtension");

    /** Namespace of the {@code LocationReferencing} module. */
    public static final String LOCATION_REFERENCING = module("locationReferencing");

    /** Namespace of the {@code LocationExtension} module. */
    public static final String LOCATION_EXTENSION = module("locationExtension");

    /** Namespace of the {@code Situation} module. */
    public static final String SITUATION = module("situation");

    /** Namespace of the {@code Facilities} module. */
    public static final String FACILITIES = module("facilities");

    /** Namespace of the {@code EnergyInfrastructure} module. */
    public static final String ENERGY_INFRASTRUCTURE = module("energyInfrastructure");

    /** Namespace of the {@code AfirEnergyInfrastructure} module. */
    public static final String AFIR_ENERGY_INFRASTRUCTURE = module("afirEnergyInfrastructure");

    /** Namespace of the {@code AfirFacilities} module. */
    public static final String AFIR_FACILITIES = module("afirFacilities");

    /** Namespace of the {@code Parking} module. */
    public static final String PARKING = module("parking");

    /** Namespace of the {@code RoadTrafficData} module. */
    public static final String ROAD_TRAFFIC_DATA = module("roadTrafficData");

    /** Namespace of the {@code Vms} module. */
    public static final String VMS = module("vms");

    /** Namespace of the {@code ControlledZone} module. */
    public static final String CONTROLLED_ZONE = module("controlledZone");

    /** Namespace of the {@code FaultAndStatus} module. */
    public static final String FAULT_AND_STATUS = module("faultAndStatus");

    /** Namespace of the {@code ReroutingManagementEnhanced} module. */
    public static final String REROUTING_MANAGEMENT_ENHANCED = module("reroutingManagementEnhanced");

    /** Namespace of the {@code TrafficManagementPlan} module. */
    public static final String TRAFFIC_MANAGEMENT_PLAN = module("trafficManagementPlan");

    /** Namespace of the {@code TrafficRegulation} module. */
    public static final String TRAFFIC_REGULATION = module("trafficRegulation");

    /** Namespace of the {@code UrbanExtensions} module. */
    public static final String URBAN_EXTENSIONS = module("urbanExtensions");

    /** Namespace of the {@code D2Payload} root module. */
    public static final String D2_PAYLOAD = module("d2Payload");

    private Namespaces() {}

    /**
     * Builds the namespace URI for a DATEX II v3 module.
     *
     * @param module the module name exactly as used by DATEX II, for example {@code "situation"}
     * @return the fully-qualified namespace URI, for example {@code
     *     http://datex2.eu/schema/3/situation}
     * @throws NullPointerException if {@code module} is {@code null}
     */
    public static String module(String module) {
        if (module == null) {
            throw new NullPointerException("module");
        }
        return BASE + "/" + module;
    }
}
