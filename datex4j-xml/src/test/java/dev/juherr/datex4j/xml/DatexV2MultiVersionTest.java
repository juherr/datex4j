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

import dev.juherr.datex4j.core.DatexVersion;
import jakarta.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;

/**
 * Verifies that every bundled DATEX II v2 version round-trips through {@link DatexXml}.
 *
 * <p>Unlike v3, a v2 document is rooted at {@code d2LogicalModel} (which nests both an {@code
 * exchange} and a {@code payloadPublication}), so a full, schema-valid {@code d2LogicalModel} is
 * marshalled and validated rather than a bare publication.
 */
class DatexV2MultiVersionTest {

    @Test
    void v20MarshallerRoundtripsAv20D2LogicalModel() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_0).validating(true).build();

        JAXBElement<dev.juherr.datex4j.model.v2_0.D2LogicalModel> original = v20D2LogicalModel();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v2_0.D2LogicalModel.class);

        assertThat(restored.getPayloadPublication())
                .isInstanceOf(dev.juherr.datex4j.model.v2_0.GenericPublication.class);
        assertThat(restored.getExchange().getSupplierIdentification().getNationalIdentifier())
                .isEqualTo("datex4j");
    }

    @Test
    void v21MarshallerRoundtripsAv21D2LogicalModel() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_1).validating(true).build();

        JAXBElement<dev.juherr.datex4j.model.v2_1.D2LogicalModel> original = v21D2LogicalModel();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v2_1.D2LogicalModel.class);

        assertThat(restored.getPayloadPublication())
                .isInstanceOf(dev.juherr.datex4j.model.v2_1.GenericPublication.class);
        assertThat(restored.getExchange().getSupplierIdentification().getNationalIdentifier())
                .isEqualTo("datex4j");
    }

    @Test
    void v22MarshallerRoundtripsAv22D2LogicalModel() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_2).validating(true).build();

        JAXBElement<dev.juherr.datex4j.model.v2_2.D2LogicalModel> original = v22D2LogicalModel();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v2_2.D2LogicalModel.class);

        assertThat(restored.getPayloadPublication())
                .isInstanceOf(dev.juherr.datex4j.model.v2_2.GenericPublication.class);
        assertThat(restored.getExchange().getSupplierIdentification().getNationalIdentifier())
                .isEqualTo("datex4j");
    }

    @Test
    void v23MarshallerRoundtripsAv23D2LogicalModel() {
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_3).validating(true).build();

        JAXBElement<dev.juherr.datex4j.model.v2_3.D2LogicalModel> original = v23D2LogicalModel();
        byte[] xml = marshaller.write(original);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v2_3.D2LogicalModel.class);

        assertThat(restored.getPayloadPublication())
                .isInstanceOf(dev.juherr.datex4j.model.v2_3.GenericPublication.class);
        assertThat(restored.getExchange().getSupplierIdentification().getNationalIdentifier())
                .isEqualTo("datex4j");
    }

    @Test
    void wrapsABarePublicationInAD2LogicalModelRoot() {
        // Proves the SPI wrapAsPayload(publication) path: a bare v2 PayloadPublication becomes a
        // d2LogicalModel-rooted document. Exchange is optional to marshalling, so validation is off.
        DatexMarshaller marshaller =
                DatexXml.builder().version(DatexVersion.V2_1).build();

        var publication = v21GenericPublication();
        byte[] xml = marshaller.write(publication);
        var restored = marshaller.read(xml, dev.juherr.datex4j.model.v2_1.D2LogicalModel.class);

        assertThat(restored.getPayloadPublication())
                .isInstanceOf(dev.juherr.datex4j.model.v2_1.GenericPublication.class);
    }

    private static JAXBElement<dev.juherr.datex4j.model.v2_0.D2LogicalModel> v20D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_0.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_0.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_0.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_0.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        var publication = new dev.juherr.datex4j.model.v2_0.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_0.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_0.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        root.setPayloadPublication(publication);
        return factory.createD2LogicalModel(root);
    }

    private static JAXBElement<dev.juherr.datex4j.model.v2_1.D2LogicalModel> v21D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_1.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_1.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_1.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_1.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        root.setPayloadPublication(v21GenericPublication());
        return factory.createD2LogicalModel(root);
    }

    private static dev.juherr.datex4j.model.v2_1.GenericPublication v21GenericPublication() {
        var publication = new dev.juherr.datex4j.model.v2_1.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_1.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_1.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        return publication;
    }

    private static JAXBElement<dev.juherr.datex4j.model.v2_2.D2LogicalModel> v22D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_2.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_2.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_2.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_2.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        var publication = new dev.juherr.datex4j.model.v2_2.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_2.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_2.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        root.setPayloadPublication(publication);
        return factory.createD2LogicalModel(root);
    }

    private static JAXBElement<dev.juherr.datex4j.model.v2_3.D2LogicalModel> v23D2LogicalModel() {
        var factory = new dev.juherr.datex4j.model.v2_3.ObjectFactory();
        var root = factory.createD2LogicalModel();
        root.setModelBaseVersion("2");
        var exchange = new dev.juherr.datex4j.model.v2_3.Exchange();
        var supplier = new dev.juherr.datex4j.model.v2_3.InternationalIdentifier();
        supplier.setCountry(dev.juherr.datex4j.model.v2_3.CountryEnum.GB);
        supplier.setNationalIdentifier("datex4j");
        exchange.setSupplierIdentification(supplier);
        root.setExchange(exchange);
        var publication = new dev.juherr.datex4j.model.v2_3.GenericPublication();
        publication.setLang("en");
        publication.setPublicationTime(dateTime());
        var creator = new dev.juherr.datex4j.model.v2_3.InternationalIdentifier();
        creator.setCountry(dev.juherr.datex4j.model.v2_3.CountryEnum.GB);
        creator.setNationalIdentifier("datex4j");
        publication.setPublicationCreator(creator);
        publication.setGenericPublicationName("datex4j-test");
        root.setPayloadPublication(publication);
        return factory.createD2LogicalModel(root);
    }

    private static XMLGregorianCalendar dateTime() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-07-23T10:15:30Z");
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }
}
