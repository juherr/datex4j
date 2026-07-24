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
package dev.juherr.datex4j.it;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.validation.DatexValidator;
import dev.juherr.datex4j.validation.ValidationMessage;
import dev.juherr.datex4j.validation.ValidationResult;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Documents how the committed NDW truck-parking status feed relates to the bundled DATEX II schema
 * sets. It was hoped v3.0 would validate this feed; it does not, and neither does any other bundled
 * version, for two distinct and independently verified reasons captured below. These assertions pin
 * the exact XSD errors so any future schema change that would make the feed valid is noticed.
 *
 * <p>Reason 1 (v3.0 through v3.2): DATEX II did not publish a {@code Parking} module until v3.3, so
 * the feed's {@code parking:ParkingStatusPublication} payload type cannot be resolved at all
 * ({@code cvc-elt.4.2}). A truck-parking publication is therefore not expressible in v3.0.
 *
 * <p>Reason 2 (v3.3 onward, including the current version): once {@code Parking} exists the payload
 * type resolves, but the feed emits {@code targetClass="par:ParkingTable"} whereas the schema fixes
 * that attribute to {@code prk:ParkingTable} ({@code cvc-complex-type.3.1}) — the real-world prefix
 * drift documented in the dataset README. This is a defect of the source feed, not of any bundled
 * schema, so no bundled version validates it.
 */
class TruckParkingV30ValidationTest {

    private static final String RESOURCE = "/datasets/netherlands/truckparking-status.xml";

    private static byte[] fixture() throws Exception {
        try (InputStream in = TruckParkingV30ValidationTest.class.getResourceAsStream(RESOURCE)) {
            assertThat(in).as("committed NDW fixture on classpath").isNotNull();
            return in.readAllBytes();
        }
    }

    private static List<String> messages(ValidationResult result) {
        return result.errors().stream().map(ValidationMessage::message).toList();
    }

    @Test
    void ndwTruckParkingIsNotExpressibleInV30() throws Exception {
        ValidationResult result = DatexValidator.forVersion(DatexVersion.V3_0).validate(fixture());

        // Not valid, and specifically because v3.0 has no Parking module: the payload type is
        // unresolvable. (Error codes are locale-independent; the plugin's messages may be localized.)
        assertThat(result.isValid()).isFalse();
        assertThat(messages(result))
                .as("v3.0 cannot resolve the Parking payload type")
                .anySatisfy(
                        message -> assertThat(message).contains("cvc-elt.4.2").contains("ParkingStatusPublication"));
    }

    @Test
    void ndwTruckParkingFailsEvenOnCurrentVersionDueToPrefixDrift() throws Exception {
        ValidationResult result =
                DatexValidator.forVersion(DatexVersion.current()).validate(fixture());

        // The current version has Parking, so the payload type resolves, but the feed's
        // targetClass="par:ParkingTable" violates the schema's fixed "prk:ParkingTable".
        assertThat(result.isValid()).isFalse();
        assertThat(messages(result))
                .as("current version rejects the feed's targetClass prefix drift")
                .anySatisfy(message -> assertThat(message).contains("cvc-complex-type.3.1"));
    }
}
