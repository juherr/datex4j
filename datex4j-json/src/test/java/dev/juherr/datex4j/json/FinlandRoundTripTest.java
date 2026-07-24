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
package dev.juherr.datex4j.json;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.v3_6.messagecontainer.MessageContainer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Oracle test: a real, trimmed Fintraffic AFIR {@link MessageContainer} must round-trip through
 * the conformant DATEX II JSON codec without losing information.
 *
 * <p>This test is expected to be RED until the conformant JSON codec (namespace prefixes,
 * flattened multilingual strings, enum {@code {value, _extendedValue}} encoding, {@code G}-suffix
 * attribute handling, substitution prefix+type, and the {@code readContainer}/container-aware
 * {@code write} API) lands.
 */
class FinlandRoundTripTest {

    private byte[] fixture() throws Exception {
        try (var in = getClass().getResourceAsStream("/datex-json/finland-afir-messagecontainer.v3_6.json")) {
            return in.readAllBytes();
        }
    }

    @Test
    void realFinlandMessageContainerRoundTrips() throws Exception {
        DatexJsonMapper mapper = DatexJson.builder().version(DatexVersion.V3_6).build();
        MessageContainer c1 = mapper.readContainer(fixture(), MessageContainer.class);
        byte[] out = mapper.write(c1);
        MessageContainer c2 = mapper.readContainer(out, MessageContainer.class);
        byte[] out2 = mapper.write(c2);
        assertThat(new String(out2, StandardCharsets.UTF_8)).isEqualTo(new String(out, StandardCharsets.UTF_8));
    }
}
