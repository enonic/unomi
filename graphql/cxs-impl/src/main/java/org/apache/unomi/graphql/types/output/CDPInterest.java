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
import graphql.annotations.annotationTypes.GraphQLPrettify;

@GraphQLName("CDP_Interest")
public class CDPInterest {

    private String topic;

    private Double score;

    public CDPInterest(@GraphQLID final String topic, final Double score) {
        this.topic = topic;
        this.score = score;
    }

    @GraphQLID
    @GraphQLField
    @GraphQLPrettify
    public String getTopic() {
        return topic;
    }

    @GraphQLField
    @GraphQLPrettify
    public Double getScore() {
        return score;
    }

}
