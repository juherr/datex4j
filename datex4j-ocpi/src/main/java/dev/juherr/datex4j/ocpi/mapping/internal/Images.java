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
package dev.juherr.datex4j.ocpi.mapping.internal;

import dev.juherr.datex4j.model.v3_7.common.UrlLink;
import dev.juherr.datex4j.ocpi.model.v2_3.Image;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts between OCPI {@link Image} links and DATEX II {@link UrlLink} photo URLs, shared by
 * {@link dev.juherr.datex4j.ocpi.mapping.LocationMapper} and {@link
 * dev.juherr.datex4j.ocpi.mapping.EvseMapper}.
 */
public final class Images {

    private Images() {}

    /**
     * Builds DATEX II photo {@link UrlLink}s from {@code images}, or an empty list if {@code
     * images} is {@code null}. Null elements and images with a null URL are skipped.
     */
    public static List<UrlLink> toDatex(List<Image> images) {
        List<UrlLink> urlLinks = new ArrayList<>();
        if (images == null) {
            return urlLinks;
        }
        for (Image image : images) {
            if (image == null || image.getUrl() == null) {
                continue;
            }
            UrlLink urlLink = new UrlLink();
            urlLink.setUrlLinkAddress(image.getUrl().toString());
            urlLinks.add(urlLink);
        }
        return urlLinks;
    }

    /**
     * Builds OCPI {@link Image} links from {@code urlLinks}, or an empty list if {@code urlLinks}
     * is {@code null}. Null elements, links with a null or blank address, and addresses that are
     * not valid URIs are skipped.
     */
    public static List<Image> toOcpi(List<UrlLink> urlLinks) {
        List<Image> images = new ArrayList<>();
        if (urlLinks == null) {
            return images;
        }
        for (UrlLink urlLink : urlLinks) {
            if (urlLink == null
                    || urlLink.getUrlLinkAddress() == null
                    || urlLink.getUrlLinkAddress().isBlank()) {
                continue;
            }
            try {
                Image image = new Image();
                image.setUrl(URI.create(urlLink.getUrlLinkAddress()));
                images.add(image);
            } catch (IllegalArgumentException e) {
                // Not a valid URI: skip this image rather than failing the whole mapping.
            }
        }
        return images;
    }
}
