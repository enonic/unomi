/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.unomi.graphql.providers;

import graphql.annotations.processor.typeFunctions.TypeFunction;
import org.apache.unomi.graphql.function.DateFunction;
import org.apache.unomi.graphql.function.DateTimeFunction;
import org.apache.unomi.graphql.function.JSONFunction;
import org.apache.unomi.graphql.types.input.CDPEventProcessor;
import org.apache.unomi.graphql.types.input.CDPProfileUpdateEventInput;
import org.apache.unomi.graphql.types.output.CDPConsentUpdateEvent;
import org.apache.unomi.graphql.types.output.CDPListsUpdateEvent;
import org.apache.unomi.graphql.types.output.CDPProfileUpdateEvent;
import org.apache.unomi.graphql.types.output.CDPSessionEvent;
import org.apache.unomi.graphql.types.output.UnomiEvent;
import org.osgi.service.component.annotations.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CDPDefaultGraphQLProvider
        implements GraphQLAdditionalTypesProvider, GraphQLTypeFunctionProvider, GraphQLProcessEventsProvider {

    @Override
    public Set<Class<?>> getAdditionalTypes() {
        final Set<Class<?>> additionalTypes = new HashSet<>();

        additionalTypes.add(CDPSessionEvent.class);
        additionalTypes.add(CDPProfileUpdateEvent.class);
        additionalTypes.add(CDPConsentUpdateEvent.class);
        additionalTypes.add(CDPListsUpdateEvent.class);
        additionalTypes.add(UnomiEvent.class);

        return additionalTypes;
    }

    @Override
    public Set<TypeFunction> getTypeFunctions() {
        final Set<TypeFunction> typeFunctions = new HashSet<>();

        typeFunctions.add(new DateTimeFunction());
        typeFunctions.add(new DateFunction());
        typeFunctions.add(new JSONFunction());

        return typeFunctions;
    }

    @Override
    public Set<Class<? extends CDPEventProcessor>> getProcessEvents() {
        final Set<Class<? extends CDPEventProcessor>> eventProcessors = new HashSet<>();

        eventProcessors.add(CDPProfileUpdateEventInput.class);

        return eventProcessors;
    }

}
