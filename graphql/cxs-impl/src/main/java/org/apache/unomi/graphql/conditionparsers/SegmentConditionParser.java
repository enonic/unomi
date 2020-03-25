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
import org.apache.unomi.graphql.schema.ComparisonConditionTranslator;
import org.apache.unomi.graphql.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SegmentConditionParser {

    private static final Predicate<Condition> IS_BOOLEAN_CONDITION_TYPE =
            condition -> "booleanCondition".equals(condition.getConditionTypeId());

    private final Condition segmentCondition;

    private Map<FilterType, List<ConditionDecorator>> groupedConditionsByFilterType = new HashMap<>();

    public SegmentConditionParser(final Condition segmentCondition) {
        this.segmentCondition = segmentCondition;
    }

    public Map<String, Object> parseCondition() {
        final Map<String, ConditionDecorator> context = new HashMap<>();

        populate(context, segmentCondition);

        context.entrySet().stream()
                .filter(entry -> !entry.getValue().getConditionTypeId().equals("booleanCondition"))
                .forEach(entry -> {
                    final FilterType filterType = entry.getValue().getFilterType();

                    if (!groupedConditionsByFilterType.containsKey(filterType)) {
                        groupedConditionsByFilterType.put(filterType, new ArrayList<>());
                    }

                    groupedConditionsByFilterType.get(filterType).add(entry.getValue());
                });

        final Map<FilterType, String> rootConditionIdPerFilterType = new HashMap<>();

        groupedConditionsByFilterType.forEach((filterType, conditionDecorators) -> {
            if (conditionDecorators != null && !conditionDecorators.isEmpty()) {
                ConditionDecorator member = conditionDecorators.get(0);

                do {
                    if (member.getParentId() != null && context.get(member.getParentId()).getParentId() != null) {
                        member = context.get(member.getParentId());
                    }
                }
                while (context.get(member.getParentId()).getParentId() != null);

                rootConditionIdPerFilterType.put(filterType, member.getId());
            }
        });

        final Map<String, Object> dataHolder = new LinkedHashMap<>();

        rootConditionIdPerFilterType.forEach((filterType, conditionId) -> {
            final ConditionDecorator conditionDecorator = context.get(conditionId);

            final List<ConditionDecorator> conditionDecorators = groupedConditionsByFilterType.get(filterType);

            switch (filterType) {
                case CONSENTS_CONTAINS:
                    dataHolder.put(filterType.getValue(), new SegmentProfileConsentsConditionParser(conditionDecorators).parse());
                    break;
                case SEGMENTS_CONTAINS:
                    dataHolder.put(filterType.getValue(), new SegmentProfileSegmentsConditionParser(conditionDecorators).parse());
                    break;
                case PROFILE_IDS_CONTAINS:
                    dataHolder.put(filterType.getValue(), new SegmentProfileIDsConditionParser(conditionDecorators).parse());
                    break;
                case LISTS_CONTAINS:
                    dataHolder.put(filterType.getValue(), new SegmentProfileListConditionParser(conditionDecorators).parse());
                    break;
                case EVENTS:
                    dataHolder.put(filterType.getValue(), new SegmentProfileEventsConditionParser(conditionDecorators).parse());
                    break;
                case PROPERTIES:
                    dataHolder.put(filterType.getValue(), processProfileProperties(conditionDecorator.getCondition()));
                    break;
                case INTERESTS:
                    dataHolder.put(filterType.getValue(), processInterests(conditionDecorator.getCondition()));
                    break;
                default: {
                    // do nothing
                }
            }
        });

        return dataHolder;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> processProfileProperties(final Condition condition) {
        final Map<String, Object> fieldsMap = new LinkedHashMap<>();

        ((List<Condition>) condition.getParameter("subConditions")).forEach(subCondition -> {
            if (IS_BOOLEAN_CONDITION_TYPE.test(subCondition)) {
                final List<Map<String, Object>> conditionList = ((List<Condition>) subCondition.getParameter("subConditions"))
                        .stream()
                        .map(this::processProfileProperties)
                        .collect(Collectors.toList());

                fieldsMap.put(subCondition.getParameter("operator").toString(), conditionList);
            } else {
                final Map<String, Object> fieldAsTuple = createProfilePropertiesField(subCondition);
                fieldsMap.put(fieldAsTuple.get("fieldName").toString(), fieldAsTuple.get("fieldValue"));
            }
        });

        return fieldsMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> processInterests(final Condition condition) {
        final Map<String, Object> fieldsMap = new LinkedHashMap<>();

        ((List<Condition>) condition.getParameter("subConditions")).forEach(subCondition -> {
            if (IS_BOOLEAN_CONDITION_TYPE.test(subCondition)) {
                final List<Map<String, Object>> conditionList = ((List<Condition>) subCondition.getParameter("subConditions"))
                        .stream()
                        .map(this::processInterests)
                        .collect(Collectors.toList());

                fieldsMap.put(subCondition.getParameter("operator").toString(), conditionList);
            } else {
                final Map<String, Object> fieldAsTuple = createInterestField(subCondition);
                fieldsMap.put(fieldAsTuple.get("fieldName").toString(), fieldAsTuple.get("fieldValue"));
                fieldsMap.put("topic_equals", fieldAsTuple.get("topicName"));
            }
        });

        return fieldsMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createProfilePropertiesField(final Condition condition) {
        final String propertyName = condition.getParameter("propertyName").toString().replaceAll("properties.", "");

        final String comparisonOperator = ComparisonConditionTranslator.translateFromUnomiToGraphQL(condition.getParameter("comparisonOperator").toString());

        final String fieldName = propertyName + "_" + comparisonOperator;

        final Map<String, Object> tuple = new HashMap<>();

        tuple.put("fieldName", fieldName);

        if (condition.getParameter("propertyValueDate") != null) {
            tuple.put("fieldValue", DateUtils.offsetDateTimeFromMap((Map<String, Object>) condition.getParameter("propertyValueDate")));
        } else if (condition.getParameter("propertyValueInteger") != null) {
            tuple.put("fieldValue", condition.getParameter("propertyValueInteger"));
        } else {
            tuple.put("fieldValue", condition.getParameter("propertyValue"));
        }

        return tuple;
    }

    private Map<String, Object> createInterestField(final Condition condition) {
        final String comparisonOperator =
                ComparisonConditionTranslator.translateFromUnomiToGraphQL(condition.getParameter("comparisonOperator").toString());

        final String fieldName = "score_" + comparisonOperator;

        final Map<String, Object> tuple = new HashMap<>();

        tuple.put("fieldName", fieldName);
        tuple.put("fieldValue", condition.getParameter("propertyValueInteger"));
        tuple.put("topicName", condition.getParameter("propertyName").toString().replaceAll("properties.interests.", ""));

        return tuple;
    }

    private void populate(final Map<String, ConditionDecorator> context, final Condition condition) {
        populate(context, null, condition);
    }

    @SuppressWarnings("unchecked")
    private void populate(final Map<String, ConditionDecorator> context, final ConditionDecorator parentDecorator, final Condition condition) {
        final ConditionDecorator conditionDecorator = createConditionDecorator(parentDecorator, condition);
        context.put(conditionDecorator.getId(), conditionDecorator);

        if (IS_BOOLEAN_CONDITION_TYPE.test(condition)) {
            final List<Condition> subConditions = (List<Condition>) condition.getParameter("subConditions");

            conditionDecorator.setSubConditions(subConditions);

            subConditions.forEach(subCondition -> populate(context, conditionDecorator, subCondition));
        }
    }

    private ConditionDecorator createConditionDecorator(final ConditionDecorator parentDecorator, final Condition condition) {
        final ConditionDecorator decorator = new ConditionDecorator();

        decorator.setId(UUID.randomUUID().toString());
        decorator.setCondition(condition);
        decorator.setConditionTypeId(condition.getConditionTypeId());

        if (parentDecorator != null) {
            decorator.setParentId(parentDecorator.getId());
        }

        if (IS_BOOLEAN_CONDITION_TYPE.test(condition)) {
            decorator.setOperator(condition.getParameter("operator").toString());

            return decorator;
        }

        if ("profilePropertyCondition".equals(condition.getConditionTypeId())) {
            final Object propertyName = condition.getParameter("propertyName");

            if (propertyName != null) {
                if (propertyName.toString().startsWith("properties.interests.")) {
                    decorator.setFilterType(FilterType.INTERESTS);
                } else if (propertyName.toString().equals("itemId")) {
                    decorator.setFilterType(FilterType.PROFILE_IDS_CONTAINS);
                } else if (propertyName.toString().startsWith("consents.")) {
                    decorator.setFilterType(FilterType.CONSENTS_CONTAINS);
                } else {
                    decorator.setFilterType(FilterType.PROPERTIES);
                }
            }
        } else if ("profileSegmentCondition".equals(condition.getConditionTypeId())) {
            decorator.setFilterType(FilterType.SEGMENTS_CONTAINS);
        } else if ("pastEventCondition".equals(condition.getConditionTypeId())) {
            decorator.setFilterType(FilterType.EVENTS);
        } else if ("profileUserListCondition".equals(condition.getConditionTypeId())) {
            decorator.setFilterType(FilterType.LISTS_CONTAINS);
        }

        return decorator;
    }

}
