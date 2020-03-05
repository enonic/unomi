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
import graphql.annotations.annotationTypes.GraphQLID;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.annotationTypes.GraphQLPrettify;
import org.apache.unomi.api.PropertyType;

import java.util.ArrayList;
import java.util.List;

@GraphQLName("CDP_PropertyInterface")
public class CDPPropertyType {

    private String name;
    private Integer minOccurrences;
    private Integer maxOccurrences;
    private List<String> tags;

    public CDPPropertyType(@GraphQLName("name") String name,
                           @GraphQLName("minOccurrences") Integer minOccurrences,
                           @GraphQLName("maxOccurrences") Integer maxOccurrences,
                           @GraphQLName("tags") List<String> tags) {
        this.name = name;
        this.minOccurrences = minOccurrences;
        this.maxOccurrences = maxOccurrences;
        this.tags = tags;
    }

    public CDPPropertyType(PropertyType propertyType) {
        this.name = propertyType.getItemId();
        // this.minOccurrences;
        // this.maxOccurrences;
        this.tags = new ArrayList<>(propertyType.getMetadata().getTags());
    }

    public CDPPropertyType(CDPPropertyType input) {
        this.name = input.name;
        this.minOccurrences = input.minOccurrences;
        this.maxOccurrences = input.maxOccurrences;
        this.tags = input.tags;
    }

    @GraphQLID
    @GraphQLNonNull
    @GraphQLField
    @GraphQLPrettify
    public String getName() {
        return name;
    }

    @GraphQLField
    @GraphQLPrettify
    public Integer getMinOccurrences() {
        return minOccurrences;
    }

    @GraphQLField
    @GraphQLPrettify
    public Integer getMaxOccurrences() {
        return maxOccurrences;
    }

    @GraphQLField
    @GraphQLPrettify
    public List<String> getTags() {
        return tags;
    }

    public String getCDPPropertyType() {
        return "string";
    }

}