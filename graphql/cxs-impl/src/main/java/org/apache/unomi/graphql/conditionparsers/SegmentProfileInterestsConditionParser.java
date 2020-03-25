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
package org.apache.unomi.graphql.conditionparsers;

import org.apache.unomi.api.conditions.Condition;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SegmentProfileInterestsConditionParser {

    private final List<ConditionDecorator> conditions;

    private final Condition condition;

    public SegmentProfileInterestsConditionParser(List<ConditionDecorator> conditions, final Condition condition) {
        this.conditions = conditions;
        this.condition = condition;
    }

//    public Map<String, Object> parse() {
//        final Optional<Condition> conditionOp = conditions.stream()
//                .map(ConditionDecorator::getCondition)
//                .filter(c -> "profilePropertyCondition".equals(c.getConditionTypeId())
//                        && Objects.nonNull(c.getParameter("propertyName"))
//                        && c.getParameter("propertyName").toString().startsWith("properties.interests."))
//                .findFirst();
//    }

}
