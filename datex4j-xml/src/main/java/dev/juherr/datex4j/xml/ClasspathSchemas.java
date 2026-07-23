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

import dev.juherr.datex4j.core.DatexSchemas;
import dev.juherr.datex4j.core.DatexVersion;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Loads the DATEX II XML Schema set from the classpath.
 *
 * <p>The schemas are bundled by {@code datex4j-model} under {@code META-INF/datex4j/schema/vX.Y}.
 * Loading starts from the {@linkplain DatexSchemas#ROOT_SCHEMA root schema}, which imports every
 * module schema; imports (relative file names) are resolved back onto the classpath by {@link
 * ClasspathResourceResolver}.
 */
final class ClasspathSchemas {

    private ClasspathSchemas() {}

    /**
     * Compiles the DATEX II schema set for the given version into a validating {@link Schema}.
     *
     * @param version the DATEX II version
     * @return the compiled schema
     * @throws DatexXmlException if the schemas cannot be found or compiled
     */
    static Schema load(DatexVersion version) {
        String directory = DatexSchemas.resourceDirectory(version);
        String rootResource = DatexSchemas.rootSchema(version);
        ClassLoader loader = ClasspathSchemas.class.getClassLoader();

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (SAXException ignored) {
            // Property not supported by this implementation; safe to continue.
        }
        factory.setResourceResolver(new ClasspathResourceResolver(directory, loader));

        try (InputStream in = loader.getResourceAsStream(rootResource)) {
            if (in == null) {
                throw new DatexXmlException("DATEX II root schema not found on classpath: " + rootResource);
            }
            StreamSource source = new StreamSource(in);
            source.setSystemId(rootResource);
            return factory.newSchema(source);
        } catch (SAXException e) {
            throw new DatexXmlException("Failed to compile DATEX II schemas for version " + version, e);
        } catch (java.io.IOException e) {
            throw new DatexXmlException("Failed to read DATEX II root schema: " + rootResource, e);
        }
    }

    /** Resolves schema imports (relative file names) to classpath resources in a fixed directory. */
    private static final class ClasspathResourceResolver implements LSResourceResolver {

        private final String directory;
        private final ClassLoader loader;

        ClasspathResourceResolver(String directory, ClassLoader loader) {
            this.directory = directory;
            this.loader = loader;
        }

        @Override
        public LSInput resolveResource(
                String type, String namespaceUri, String publicId, String systemId, String baseUri) {
            if (systemId == null) {
                return null;
            }
            String fileName = systemId.substring(systemId.lastIndexOf('/') + 1);
            String resource = directory + "/" + fileName;
            InputStream stream = loader.getResourceAsStream(resource);
            if (stream == null) {
                return null;
            }
            return new StreamLsInput(publicId, resource, baseUri, stream);
        }
    }

    /** Minimal {@link LSInput} backed by a byte stream loaded from the classpath. */
    private static final class StreamLsInput implements LSInput {

        private String publicId;
        private String systemId;
        private String baseUri;
        private InputStream byteStream;

        StreamLsInput(String publicId, String systemId, String baseUri, InputStream byteStream) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.baseUri = baseUri;
            this.byteStream = byteStream;
        }

        @Override
        public InputStream getByteStream() {
            return byteStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            this.byteStream = byteStream;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getBaseURI() {
            return baseUri;
        }

        @Override
        public void setBaseURI(String baseUri) {
            this.baseUri = baseUri;
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            // Not supported; this input is always byte-based.
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public void setStringData(String stringData) {
            // Not supported; this input is always byte-based.
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public void setEncoding(String encoding) {
            // Not supported; encoding is taken from the XML declaration.
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            // Not supported.
        }
    }
}
