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
package dev.juherr.datex4j.builders;

import dev.juherr.datex4j.model.v3_7.common.InternationalIdentifier;
import dev.juherr.datex4j.model.v3_7.common.PayloadPublication;
import java.time.OffsetDateTime;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Fluent base for building DATEX II publications, shared by the datex4j domain modules.
 *
 * <p>Every DATEX II publication extends {@code PayloadPublication} and must carry the same header:
 * a language, a model base version, a publication time and a publication creator. This builder sets
 * those with sensible defaults (language {@code en}, model base version {@code 3}, publication time
 * "now") so domain builders only add domain content. It targets the current DATEX II version
 * ({@link dev.juherr.datex4j.core.DatexVersion#current()}).
 *
 * <p>Subclasses supply the concrete publication instance through {@link #publication()} and their
 * own type through {@link #self()} so the fluent methods return the subclass type.
 *
 * @param <P> the concrete publication type
 * @param <B> the concrete builder type
 */
public abstract class PublicationBuilder<P extends PayloadPublication, B extends PublicationBuilder<P, B>> {

    private String lang = "en";
    private String modelBaseVersion = "3";
    private OffsetDateTime publicationTime;
    private InternationalIdentifier publicationCreator;

    /**
     * Returns the publication instance being configured. Called once by {@link #build()}.
     *
     * @return the publication to populate
     */
    protected abstract P publication();

    /**
     * Returns {@code this} as the concrete builder type, enabling a fluent API in subclasses.
     *
     * @return this builder
     */
    protected abstract B self();

    /**
     * Sets the language of the publication. Defaults to {@code en}.
     *
     * @param lang a language code, for example {@code en}
     * @return this builder
     */
    public B lang(String lang) {
        this.lang = lang;
        return self();
    }

    /**
     * Sets the DATEX II model base version attribute. Defaults to {@code 3}.
     *
     * @param modelBaseVersion the model base version
     * @return this builder
     */
    public B modelBaseVersion(String modelBaseVersion) {
        this.modelBaseVersion = modelBaseVersion;
        return self();
    }

    /**
     * Sets the publication time. Defaults to the moment {@link #build()} is called.
     *
     * @param publicationTime the publication time
     * @return this builder
     */
    public B publicationTime(OffsetDateTime publicationTime) {
        this.publicationTime = publicationTime;
        return self();
    }

    /**
     * Sets the publication creator from a country code and a national identifier.
     *
     * @param country a two-letter country code, for example {@code gb}
     * @param nationalIdentifier the creator's national identifier
     * @return this builder
     */
    public B publishedBy(String country, String nationalIdentifier) {
        InternationalIdentifier creator = new InternationalIdentifier();
        creator.setCountry(country);
        creator.setNationalIdentifier(nationalIdentifier);
        this.publicationCreator = creator;
        return self();
    }

    /**
     * Builds the publication, applying the configured header.
     *
     * @return the configured publication
     */
    public P build() {
        P publication = publication();
        publication.setLang(lang);
        publication.setModelBaseVersion(modelBaseVersion);
        publication.setPublicationTime(toXmlDateTime(publicationTime != null ? publicationTime : OffsetDateTime.now()));
        if (publicationCreator != null) {
            publication.setPublicationCreator(publicationCreator);
        }
        return publication;
    }

    private static XMLGregorianCalendar toXmlDateTime(OffsetDateTime value) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(value.toString());
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("No XML datatype factory available", e);
        }
    }
}
