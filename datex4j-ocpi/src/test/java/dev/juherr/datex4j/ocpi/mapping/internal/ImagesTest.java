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

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.common.UrlLink;
import dev.juherr.datex4j.ocpi.model.v2_3.Image;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImagesTest {

    @Test
    void toDatexMapsImageUrlToUrlLinkAddress() {
        Image image = new Image();
        image.setUrl(URI.create("https://x/p.png"));

        List<UrlLink> urlLinks = Images.toDatex(List.of(image));

        assertThat(urlLinks).hasSize(1);
        assertThat(urlLinks.get(0).getUrlLinkAddress()).isEqualTo("https://x/p.png");
    }

    @Test
    void toDatexReturnsEmptyListForNull() {
        assertThat(Images.toDatex(null)).isEmpty();
    }

    @Test
    void toDatexSkipsNullElementsAndNullUrls() {
        Image withNullUrl = new Image();
        Image valid = new Image();
        valid.setUrl(URI.create("https://x/p.png"));

        List<UrlLink> urlLinks = Images.toDatex(Arrays.asList(null, withNullUrl, valid));

        assertThat(urlLinks).hasSize(1);
        assertThat(urlLinks.get(0).getUrlLinkAddress()).isEqualTo("https://x/p.png");
    }

    @Test
    void toOcpiMapsUrlLinkAddressToImageUrl() {
        UrlLink urlLink = new UrlLink();
        urlLink.setUrlLinkAddress("https://x/p.png");

        List<Image> images = Images.toOcpi(List.of(urlLink));

        assertThat(images).hasSize(1);
        assertThat(images.get(0).getUrl()).isEqualTo(URI.create("https://x/p.png"));
    }

    @Test
    void toOcpiReturnsEmptyListForNull() {
        assertThat(Images.toOcpi(null)).isEmpty();
    }

    @Test
    void toOcpiSkipsNullElementsAndBlankOrNullAddresses() {
        UrlLink nullAddress = new UrlLink();
        UrlLink blankAddress = new UrlLink();
        blankAddress.setUrlLinkAddress("   ");
        UrlLink valid = new UrlLink();
        valid.setUrlLinkAddress("https://x/p.png");

        List<Image> images = Images.toOcpi(Arrays.asList(null, nullAddress, blankAddress, valid));

        assertThat(images).hasSize(1);
        assertThat(images.get(0).getUrl()).isEqualTo(URI.create("https://x/p.png"));
    }

    @Test
    void toOcpiSkipsUnparseableAddress() {
        UrlLink invalid = new UrlLink();
        invalid.setUrlLinkAddress("not a valid uri with spaces and no scheme ://");

        List<Image> images = Images.toOcpi(List.of(invalid));

        assertThat(images).isEmpty();
    }
}
