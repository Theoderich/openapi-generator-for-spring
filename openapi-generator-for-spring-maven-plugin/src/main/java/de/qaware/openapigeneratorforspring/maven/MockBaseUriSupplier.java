/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: Maven Plugin
 * %%
 * Copyright (C) 2020 - 2022 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.openapigeneratorforspring.maven;

import de.qaware.openapigeneratorforspring.common.supplier.OpenApiBaseUriSupplier;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class MockBaseUriSupplier implements OpenApiBaseUriSupplier {
    @Override
    public URI getBaseUri() {
        return URI.create("http://localhost");
    }
}
