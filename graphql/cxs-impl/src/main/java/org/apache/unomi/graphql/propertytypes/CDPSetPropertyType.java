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
package org.apache.unomi.graphql.propertytypes;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLPrettify;

import java.util.List;

@GraphQLName("CDP_SetProperty")
public class CDPSetPropertyType extends CDPPropertyType {

    private List<CDPPropertyType> properties;

    public CDPSetPropertyType(@GraphQLName("name") String name,
                              @GraphQLName("minOccurrences") Integer minOccurrences,
                              @GraphQLName("maxOccurrences") Integer maxOccurrences,
                              @GraphQLName("tags") List<String> tags,
                              @GraphQLName("properties") List<CDPPropertyType> properties) {
        super(name, minOccurrences, maxOccurrences, tags);
        this.properties = properties;
    }

    @GraphQLField
    @GraphQLPrettify
    public List<CDPPropertyType> getProperties() {
        return properties;
    }

    @Override
    public String getCDPPropertyType() {
        return "set";
    }

}