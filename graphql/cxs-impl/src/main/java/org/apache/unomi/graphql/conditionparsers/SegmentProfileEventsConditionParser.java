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
import org.apache.unomi.graphql.utils.DateUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SegmentProfileEventsConditionParser {

    private static final Predicate<Condition> IS_BOOLEAN_CONDITION_TYPE =
            condition -> "booleanCondition".equals(condition.getConditionTypeId());

    private final List<ConditionDecorator> conditions;

    public SegmentProfileEventsConditionParser(List<ConditionDecorator> conditions) {
        this.conditions = conditions;
    }

    public Map<String, Object> parse() {
        final Optional<Condition> conditionOp = conditions.stream()
                .map(ConditionDecorator::getCondition)
                .filter(condition -> "pastEventCondition".equals(condition.getConditionTypeId()))
                .findFirst();

        if (!conditionOp.isPresent()) {
            return null;
        }

        final Condition condition = conditionOp.get();

        final Map<String, Object> result = new LinkedHashMap<>();

        result.put("maximalCount", condition.getParameter("maximumEventCount"));
        result.put("minimalCount", condition.getParameter("minimumEventCount"));
        result.put("eventFilter", processProfileEventProperties((Condition) condition.getParameter("eventCondition")));

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> processProfileEventProperties(final Condition condition) {
        final Map<String, Object> fieldsMap = new LinkedHashMap<>();

        ((List<Condition>) condition.getParameter("subConditions")).forEach(subCondition -> {
            if (IS_BOOLEAN_CONDITION_TYPE.test(subCondition)) {
                final List<Map<String, Object>> conditionList = ((List<Condition>) subCondition.getParameter("subConditions"))
                        .stream()
                        .map(this::processProfileEventProperties)
                        .collect(Collectors.toList());

                fieldsMap.put(subCondition.getParameter("operator").toString(), conditionList);
            } else {
                final Map<String, Object> fieldAsTuple = createProfileEventPropertyField(subCondition);
                fieldsMap.put(fieldAsTuple.get("fieldName").toString(), fieldAsTuple.get("fieldValue"));
            }
        });

        return fieldsMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createProfileEventPropertyField(final Condition condition) {
        final Map<String, Object> tuple = new HashMap<>();

        if ("timeStamp".equals(condition.getParameter("propertyName").toString())) {
            if ("equals".equals(condition.getParameter("comparisonOperator").toString())) {
                tuple.put("fieldName", "cdp_timestamp_equals");
            } else if ("lessThan".equals(condition.getParameter("comparisonOperator").toString())) {
                tuple.put("fieldName", "cdp_timestamp_lt");
            } else if ("lessThanOrEqualTo".equals(condition.getParameter("comparisonOperator").toString())) {
                tuple.put("fieldName", "cdp_timestamp_lte");
            } else if ("greaterThan".equals(condition.getParameter("comparisonOperator").toString())) {
                tuple.put("fieldName", "cdp_timestamp_gt");
            } else {
                tuple.put("fieldName", "cdp_timestamp_gte");
            }

            tuple.put("fieldValue", DateUtils.offsetDateTimeFromMap((Map<String, Object>) condition.getParameter("propertyValueDate")).toString());
        } else {
            if ("source.itemId".equals(condition.getParameter("propertyName").toString())) {
                tuple.put("fieldName", "cdp_sourceID_equals");
            } else if ("profileId".equals(condition.getParameter("propertyName").toString())) {
                tuple.put("fieldName", "cdp_profileID_equals");
            } else if ("itemId".equals(condition.getParameter("propertyName").toString())) {
                tuple.put("fieldName", "id_equals");
            }

            tuple.put("fieldValue", condition.getParameter("propertyValue"));
        }

        return tuple;
    }
}
