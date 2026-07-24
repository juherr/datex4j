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
package dev.juherr.datex4j.json.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.juherr.datex4j.model.v3_7.locationextension.AddressLineTypeEnum;
import dev.juherr.datex4j.model.v3_7.locationextension._AddressLineTypeEnum;
import org.junit.jupiter.api.Test;

class DatexEnumJsonTest {

    private static ObjectMapper newMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(_AddressLineTypeEnum.class, new DatexEnumJson.Serializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

    private static _AddressLineTypeEnum wrapper(AddressLineTypeEnum value, String extendedValue) {
        _AddressLineTypeEnum wrapper = new _AddressLineTypeEnum();
        wrapper.setValue(value);
        wrapper.set_ExtendedValue(extendedValue);
        return wrapper;
    }

    @Test
    void serializesPlainValueWithoutExtendedValue() throws Exception {
        _AddressLineTypeEnum wrapper = wrapper(AddressLineTypeEnum.STREET, null);

        String json = newMapper().writeValueAsString(wrapper);

        assertThat(json).isEqualTo("{\"value\":\"street\"}");
    }

    @Test
    void serializesExtendedValueWithExtendedValueAttribute() throws Exception {
        _AddressLineTypeEnum wrapper = wrapper(AddressLineTypeEnum.fromValue("_extended"), "myCustom");

        String json = newMapper().writeValueAsString(wrapper);

        assertThat(json).isEqualTo("{\"value\":\"_extended\",\"_extendedValue\":\"myCustom\"}");
    }
}
