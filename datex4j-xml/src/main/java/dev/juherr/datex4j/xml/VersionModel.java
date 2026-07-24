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

import dev.juherr.datex4j.core.DatexVersion;
import dev.juherr.datex4j.model.spi.DatexModelProvider;
import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * ServiceLoader-based registry of the {@link DatexModelProvider} implementations present on the
 * classpath.
 *
 * <p>Each {@code datex4j-model-vX_Y} module ships one provider, registered through {@link
 * ServiceLoader}. This registry indexes them by {@link DatexVersion} once, at class-initialization
 * time, so callers resolve the bridge to a version's generated model without any hard-coded
 * reference to the generated classes. A version whose module is absent from the classpath surfaces
 * a clear error rather than a {@code null}.
 */
final class VersionModel {

    private static final Map<DatexVersion, DatexModelProvider> PROVIDERS = load();

    private VersionModel() {}

    private static Map<DatexVersion, DatexModelProvider> load() {
        Map<DatexVersion, DatexModelProvider> providers = new EnumMap<>(DatexVersion.class);
        for (DatexModelProvider provider : ServiceLoader.load(DatexModelProvider.class)) {
            DatexModelProvider previous = providers.putIfAbsent(provider.version(), provider);
            if (previous != null) {
                throw new IllegalStateException("Duplicate DatexModelProvider for DATEX II " + provider.version()
                        + ": " + previous.getClass().getName() + " and "
                        + provider.getClass().getName());
            }
        }
        return providers;
    }

    /**
     * Returns the {@link DatexModelProvider} that backs the given {@link DatexVersion}.
     *
     * @throws IllegalArgumentException if no provider for that version is on the classpath, which
     *     usually means the corresponding {@code datex4j-model-vX_Y} module is missing
     */
    static DatexModelProvider of(DatexVersion version) {
        DatexModelProvider provider = PROVIDERS.get(version);
        if (provider == null) {
            throw new IllegalArgumentException("No DATEX II model provider for " + version
                    + " on the classpath; add the datex4j-model-"
                    + version.name().toLowerCase(java.util.Locale.ROOT)
                    + " module (or the datex4j-model aggregate)");
        }
        return provider;
    }
}
