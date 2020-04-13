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

package org.apache.unomi.graphql.types.output;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLID;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.annotationTypes.GraphQLPrettify;
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.api.Persona;
import org.apache.unomi.graphql.fetchers.profile.ProfileConsentsDataFetcher;
import org.apache.unomi.graphql.fetchers.profile.ProfileInterestsDataFetcher;
import org.apache.unomi.graphql.fetchers.profile.ProfileListsDataFetcher;
import org.apache.unomi.graphql.fetchers.profile.ProfileSegmentsDataFetcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.unomi.graphql.types.output.CDPPersona.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class CDPPersona implements CDPProfileInterface {

    public static final String TYPE_NAME = "CDP_Persona";

    private Persona persona;

    public CDPPersona(Persona persona) {
        this.persona = persona;
    }

    @GraphQLID
    @GraphQLField
    public String id() {
        return persona != null ? persona.getItemId() : null;
    }

    @GraphQLField
    @GraphQLNonNull
    public String cdp_name() {
        return persona != null ? (String) persona.getProperty("cdp_name") : null;
    }

    @GraphQLField
    @GraphQLNonNull
    public CDPView cdp_view() {
        return persona != null ? new CDPView(persona.getScope()) : null;
    }

    @Override
    public Object getProperty(final String propertyName) {
        return persona != null ? persona.getProperty(propertyName) : null;
    }

    @Override
    @GraphQLField
    @GraphQLPrettify
    public List<CDPProfileID> cdp_profileIDs(final DataFetchingEnvironment environment) throws Exception {
        if (persona == null) {
            return null;
        }

        List<String> profileIds = (List<String>) persona.getProperty("mergedWith");
        return profileIds != null ? profileIds.stream().map(CDPProfileID::new).collect(Collectors.toList()) : null;
    }

    @GraphQLField
    public List<CDPSegment> cdp_segments(
            final @GraphQLName("views") List<String> viewIds, final DataFetchingEnvironment environment) throws Exception {
        return persona != null ? new ProfileSegmentsDataFetcher(persona, viewIds).get(environment) : null;
    }

    @GraphQLField
    public List<CDPInterest> cdp_interests(
            final @GraphQLName("views") List<String> viewIds,
            final DataFetchingEnvironment environment) throws Exception {
        return persona != null ? new ProfileInterestsDataFetcher(persona).get(environment) : null;
    }

    @GraphQLField
    public List<CDPConsent> cdp_consents(final DataFetchingEnvironment environment) throws Exception {
        return persona != null ? new ProfileConsentsDataFetcher(persona).get(environment) : null;
    }

    @Override
    public List<CDPList> cdp_lists(final @GraphQLName("views") List<String> viewIds, final DataFetchingEnvironment environment) throws Exception {
        return persona != null ? new ProfileListsDataFetcher(persona, viewIds).get(environment) : null;
    }

    public Persona getPersona() {
        return persona;
    }

}
