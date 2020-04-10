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
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLPrettify;

@GraphQLName("CDP_EventOccurrenceFilter")
public class CDPEventOccurrenceFilter {

    public CDPEventOccurrenceFilter() {
    }

    @GraphQLField
    @GraphQLPrettify
    public String getEventType() {
        return null;
    }

    @GraphQLField
    @GraphQLPrettify
    public String getBeforeTime() {
        return null;
    }

    @GraphQLField
    @GraphQLPrettify
    public String getAfterTime() {
        return null;
    }

    @GraphQLField
    @GraphQLPrettify
    public String getBetweenTime() {
        return null;
    }

    @GraphQLField
    @GraphQLPrettify
    public Integer getCount() {
        return null;
    }

    @GraphQLField
    @GraphQLPrettify
    public CDPEventFilter getEventFilter() {
        return null;
    }

}
