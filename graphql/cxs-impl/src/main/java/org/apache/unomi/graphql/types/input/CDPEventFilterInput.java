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
package org.apache.unomi.graphql.types.input;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

import java.util.ArrayList;
import java.util.List;

import static org.apache.unomi.graphql.types.input.CDPEventFilterInput.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class CDPEventFilterInput {

    public static final String TYPE_NAME = "CDP_EventFilterInput";

    @GraphQLField
    private List<CDPEventFilterInput> and = new ArrayList<>();

    @GraphQLField
    private List<CDPEventFilterInput> or = new ArrayList<>();

    @GraphQLField
    private String id_equals;

    @GraphQLField
    private String cdp_clientID_equals;

    @GraphQLField
    private String cdp_sourceID_equals;

    @GraphQLField
    private String cdp_profileID_equals;

    private CDPEventFilterInput(
            final @GraphQLName("and") List<CDPEventFilterInput> and,
            final @GraphQLName("or") List<CDPEventFilterInput> or,
            final @GraphQLName("id_equals") String id_equals,
            final @GraphQLName("cdp_clientID_equals") String cdp_clientID_equals,
            final @GraphQLName("cdp_sourceID_equals") String cdp_sourceID_equals,
            final @GraphQLName("cdp_profileID_equals") String cdp_profileID_equals) {
        this.and = and;
        this.or = or;
        this.id_equals = id_equals;
        this.cdp_clientID_equals = cdp_clientID_equals;
        this.cdp_sourceID_equals = cdp_sourceID_equals;
        this.cdp_profileID_equals = cdp_profileID_equals;
    }

    public List<CDPEventFilterInput> getAnd() {
        return and;
    }

    public List<CDPEventFilterInput> getOr() {
        return or;
    }

    public String getId_equals() {
        return id_equals;
    }

    public String getCdp_clientID_equals() {
        return cdp_clientID_equals;
    }

    public String getCdp_sourceID_equals() {
        return cdp_sourceID_equals;
    }

    public String getCdp_profileID_equals() {
        return cdp_profileID_equals;
    }

}

