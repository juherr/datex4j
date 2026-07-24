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
import dev.juherr.datex4j.model.v3_7.common.MultilingualString;
import dev.juherr.datex4j.model.v3_7.common.MultilingualStringValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultilingualStringJsonTest {

    private static ObjectMapper newMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultilingualString.class, new MultilingualStringJson.Serializer());
        module.addDeserializer(MultilingualString.class, new MultilingualStringJson.Deserializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

    private static MultilingualString oneValue(String lang, String value) {
        MultilingualStringValue msv = new MultilingualStringValue();
        msv.setLang(lang);
        msv.setValue(value);
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(msv);
        MultilingualString ms = new MultilingualString();
        ms.setValues(values);
        return ms;
    }

    @Test
    void serializesToFlatValuesArray() throws Exception {
        MultilingualString ms = oneValue("fi", "x");

        String json = newMapper().writeValueAsString(ms);

        assertThat(json).isEqualTo("{\"values\":[{\"lang\":\"fi\",\"value\":\"x\"}]}");
    }

    @Test
    void deserializesFlatValuesArray() throws Exception {
        String json = "{\"values\":[{\"lang\":\"fi\",\"value\":\"x\"}]}";

        MultilingualString ms = newMapper().readValue(json, MultilingualString.class);

        List<MultilingualStringValue> values = ms.getValues().getValue();
        assertThat(values).hasSize(1);
        assertThat(values.get(0).getLang()).isEqualTo("fi");
        assertThat(values.get(0).getValue()).isEqualTo("x");
    }

    @Test
    void roundTripsMultipleValues() throws Exception {
        MultilingualStringValue fi = new MultilingualStringValue();
        fi.setLang("fi");
        fi.setValue("Kärkitie 4");
        MultilingualStringValue en = new MultilingualStringValue();
        en.setLang("en");
        en.setValue("Sharp Street 4");
        MultilingualString.Values values = new MultilingualString.Values();
        values.getValue().add(fi);
        values.getValue().add(en);
        MultilingualString ms = new MultilingualString();
        ms.setValues(values);

        ObjectMapper mapper = newMapper();
        String json = mapper.writeValueAsString(ms);
        MultilingualString roundTripped = mapper.readValue(json, MultilingualString.class);

        List<MultilingualStringValue> result = roundTripped.getValues().getValue();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLang()).isEqualTo("fi");
        assertThat(result.get(0).getValue()).isEqualTo("Kärkitie 4");
        assertThat(result.get(1).getLang()).isEqualTo("en");
        assertThat(result.get(1).getValue()).isEqualTo("Sharp Street 4");
    }
}
