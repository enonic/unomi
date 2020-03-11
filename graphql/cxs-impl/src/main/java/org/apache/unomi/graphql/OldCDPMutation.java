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
package org.apache.unomi.graphql;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.graphql.propertytypes.CDPIdentifierPropertyType;
import org.apache.unomi.graphql.propertytypes.CDPPropertyType;
import org.apache.unomi.graphql.propertytypes.CDPSetPropertyType;
import org.apache.unomi.graphql.propertytypes.CDPStringPropertyType;
import org.apache.unomi.graphql.types.input.CDPEventInput;
import org.apache.unomi.graphql.types.input.CDPEventTypeInput;
import org.apache.unomi.graphql.types.input.CDPPropertyInput;
import org.apache.unomi.graphql.types.input.CDPSetPropertyTypeInput;
import org.apache.unomi.graphql.types.output.CDPEventType;

import java.util.ArrayList;
import java.util.List;

@GraphQLName("CDP_Mutation")
public class OldCDPMutation {

    CDPGraphQLProvider cdpGraphQLProvider;

    public OldCDPMutation(CDPGraphQLProvider cdpGraphQLProvider) {
        this.cdpGraphQLProvider = cdpGraphQLProvider;
    }

    @GraphQLField
    public CDPEventType createOrUpdateEventType(DataFetchingEnvironment env, @GraphQLName("eventType") CDPEventTypeInput cdpEventTypeInput) {

        CDPEventType cdpEventType = new CDPEventType(cdpEventTypeInput.getId(), cdpEventTypeInput.getScope(), cdpEventTypeInput.getTypeName(), new ArrayList<>());
        for (CDPPropertyInput propertyTypeInput : cdpEventTypeInput.getProperties()) {
            CDPPropertyType propertyType = getPropertyType(propertyTypeInput);
            cdpEventType.getProperties().add(propertyType);
        }
        cdpGraphQLProvider.getEventTypes().put(cdpEventType.getTypeName(), cdpEventType);
        cdpGraphQLProvider.updateGraphQLTypes();
        if (cdpGraphQLProvider.getCdpProviderManager() != null) {
            cdpGraphQLProvider.getCdpProviderManager().refreshProviders();
        }
        return cdpEventType;

    }

    @GraphQLField
    public int processEvents(DataFetchingEnvironment env, @GraphQLName("events") List<CDPEventInput> events) {
        return 0;
    }

    private CDPPropertyType getPropertyType(CDPPropertyInput cdpPropertyTypeInput) {
        CDPPropertyType propertyType = null;
        if (cdpPropertyTypeInput.getIdentifierPropertyTypeInput() != null) {
            propertyType = getIdentifierPropertyType(cdpPropertyTypeInput.getIdentifierPropertyTypeInput());
        } else if (cdpPropertyTypeInput.getStringPropertyTypeInput() != null) {
            propertyType = getStringPropertyType(cdpPropertyTypeInput.getStringPropertyTypeInput());
        } else if (cdpPropertyTypeInput.getSetPropertyTypeInput() != null) {
            propertyType = getSetPropertyType(cdpPropertyTypeInput.getSetPropertyTypeInput());
        }
        return propertyType;
    }

    private CDPPropertyType getSetPropertyType(CDPSetPropertyTypeInput cdpSetPropertyTypeInput) {
        List<CDPPropertyType> setProperties = null;
        if (cdpSetPropertyTypeInput.getProperties() != null) {
            setProperties = new ArrayList<>();
            for (CDPPropertyInput setProperty : cdpSetPropertyTypeInput.getProperties()) {
                CDPPropertyType subPropertyType = getPropertyType(setProperty);
                if (subPropertyType != null) {
                    setProperties.add(subPropertyType);
                }
            }
        }
        return new CDPSetPropertyType(
                cdpSetPropertyTypeInput.getName(),
                cdpSetPropertyTypeInput.getMinOccurrences(),
                cdpSetPropertyTypeInput.getMaxOccurrences(),
                cdpSetPropertyTypeInput.getTags(),
                setProperties);
    }

    private CDPPropertyType getStringPropertyType(CDPStringPropertyType stringPropertyType) {
        return new CDPStringPropertyType(
                stringPropertyType.getName(),
                stringPropertyType.getMinOccurrences(),
                stringPropertyType.getMaxOccurrences(),
                stringPropertyType.getTags(),
                stringPropertyType.getRegexp(),
                stringPropertyType.getDefaultValue()
        );
    }

    private CDPPropertyType getIdentifierPropertyType(CDPIdentifierPropertyType identifierPropertyType) {
        return new CDPIdentifierPropertyType(
                identifierPropertyType.getName(),
                identifierPropertyType.getMinOccurrences(),
                identifierPropertyType.getMaxOccurrences(),
                identifierPropertyType.getTags(),
                identifierPropertyType.getRegexp(),
                identifierPropertyType.getDefaultValue()
        );
    }

}
