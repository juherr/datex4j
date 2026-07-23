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
package dev.juherr.datex4j.ocpi.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import dev.juherr.datex4j.model.v3_7.energyinfrastructure.ElectricChargingPointStatus;
import dev.juherr.datex4j.model.v3_7.energyinfrastructure.RefillPointStatusEnum;
import dev.juherr.datex4j.ocpi.model.v2_3.Status;
import org.junit.jupiter.api.Test;

class StatusMapperTest {

    private final StatusMapper mapper = new StatusMapper();

    @Test
    void toDatexMapsAvailable() {
        ElectricChargingPointStatus status = mapper.toDatex(Status.AVAILABLE);
        assertThat(status.getStatus().getValue()).isEqualTo(RefillPointStatusEnum.AVAILABLE);
    }

    @Test
    void toDatexMapsOutOfOrder() {
        ElectricChargingPointStatus status = mapper.toDatex(Status.OUTOFORDER);
        assertThat(status.getStatus().getValue()).isEqualTo(RefillPointStatusEnum.OUT_OF_ORDER);
    }

    @Test
    void roundTripsThroughOcpi() {
        assertThat(mapper.toOcpi(mapper.toDatex(Status.CHARGING))).isEqualTo(Status.CHARGING);
    }

    @Test
    void nullInputsYieldNull() {
        assertThat(mapper.toDatex(null)).isNull();
        assertThat(mapper.toOcpi(null)).isNull();
    }
}
