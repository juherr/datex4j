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
package dev.juherr.datex4j.it.support;

import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import dev.juherr.datex4j.model.v3_7.common.MultilingualStringValue;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureSite;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTable;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.EnergyInfrastructureTablePublication;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/** Builds small, hand-authored DATEX II AFIR publications that validate against the v3.7 schema. */
public final class SyntheticDatasets {

    private SyntheticDatasets() {}

    /** A minimal, XSD-valid AFIR energy-infrastructure table publication. */
    public static EnergyInfrastructureTablePublication energyInfrastructureTable() {
        EnergyInfrastructureTablePublication publication = new EnergyInfrastructureTablePublication();
        publication.setLang("en");
        publication.setModelBaseVersion("3");
        publication.setPublicationTime(now());
        publication.setPublicationCreator(creator());
        publication.getEnergyInfrastructureTable().add(table());
        return publication;
    }

    private static InternationalIdentifier creator() {
        InternationalIdentifier creator = new InternationalIdentifier();
        creator.setCountry("fr");
        creator.setNationalIdentifier("datex4j-synthetic");
        return creator;
    }

    private static EnergyInfrastructureTable table() {
        EnergyInfrastructureTable table = new EnergyInfrastructureTable();
        table.setId("synthetic-table-1");
        table.setVersion("1");
        table.setTableName("Synthetic AFIR recharging table");
        table.getEnergyInfrastructureSite().add(site());
        return table;
    }

    private static EnergyInfrastructureSite site() {
        EnergyInfrastructureSite site = new EnergyInfrastructureSite();
        site.setId("synthetic-site-1");
        site.setVersion("1");
        site.setName(name("Synthetic recharging site"));
        return site;
    }

    private static MultilingualString name(String text) {
        MultilingualStringValue value = new MultilingualStringValue();
        value.setLang("en");
        value.setValue(text);
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(value);
        MultilingualString name = new MultilingualString();
        name.setValues(values);
        return name;
    }

    private static XMLGregorianCalendar now() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2026, 0, 1, 0, 0, 0));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
