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

public class ConditionDecorator {

    private String id;

    private String parentId;

    private String conditionTypeId;

    private String operator;

    private FilterType filterType;

    private Condition condition;

    private List<Condition> subConditions;

    public String getId() {
        return id;
    }

    public ConditionDecorator setId(String id) {
        this.id = id;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public ConditionDecorator setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getConditionTypeId() {
        return conditionTypeId;
    }

    public ConditionDecorator setConditionTypeId(String conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    public ConditionDecorator setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public ConditionDecorator setFilterType(FilterType filterType) {
        this.filterType = filterType;
        return this;
    }

    public Condition getCondition() {
        return condition;
    }

    public ConditionDecorator setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public List<Condition> getSubConditions() {
        return subConditions;
    }

    public ConditionDecorator setSubConditions(List<Condition> subConditions) {
        this.subConditions = subConditions;
        return this;
    }

}
