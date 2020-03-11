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

import static org.apache.unomi.graphql.types.input.CDPEventTypeInput.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class CDPEventTypeInput {

    public static final String TYPE_NAME = "CDP_EventTypeInput";

    @GraphQLField
    private String id;

    @GraphQLField
    private String scope;

    @GraphQLField
    private String typeName;

    @GraphQLField
    private List<CDPPropertyInput> properties = new ArrayList<>();

    public CDPEventTypeInput(final @GraphQLName("id") String id,
                             final @GraphQLName("scope") String scope,
                             final @GraphQLName("typeName") String typeName,
                             final @GraphQLName("properties") List<CDPPropertyInput> properties) {
        this.id = id;
        this.scope = scope;
        this.typeName = typeName;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public String getScope() {
        return scope;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<CDPPropertyInput> getProperties() {
        return properties;
    }

}
