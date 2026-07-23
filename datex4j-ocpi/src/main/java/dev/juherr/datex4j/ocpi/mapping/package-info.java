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
/**
 * Bidirectional, best-effort mapping between the generated OCPI 2.3.0 model and the DATEX II v3.7
 * Energy Infrastructure model. Every mapper is stateless and thread-safe; {@code null} in yields
 * {@code null} out, and unmapped fields are silently dropped (see each mapper's "Unmapped fields").
 */
package dev.juherr.datex4j.ocpi.mapping;
