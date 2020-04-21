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
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.api.Metadata;
import org.apache.unomi.api.PartialList;
import org.apache.unomi.api.lists.UserList;
import org.apache.unomi.graphql.converters.UserListConverter;

import java.util.ArrayList;
import java.util.List;

import static org.apache.unomi.graphql.types.output.CDPListConnection.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class CDPListConnection {

    public static final String TYPE_NAME = "CDP_ListConnection";

    private final PartialList<Metadata> userLists;

    public CDPListConnection(final PartialList<Metadata> userLists) {
        this.userLists = userLists;
    }

    @GraphQLField
    public Integer totalCount(final DataFetchingEnvironment environment) {
        return userLists != null ? (int) userLists.getTotalSize() : null;
    }

    @GraphQLField
    public List<CDPListEdge> edges(final DataFetchingEnvironment environment) {
        if (userLists == null) {
            return null;
        }

        final List<Metadata> userLists = this.userLists.getList();

        final List<CDPListEdge> edges = new ArrayList<>();

        final Integer first = environment.getArgument("first") != null ? environment.getArgument("first") : 0;

        for (int i = 0; i < userLists.size(); i++) {
            final UserList userList = UserListConverter.convertToUnomiList(userLists.get(i));

            edges.add(new CDPListEdge(userList, first + i));
        }

        return edges;
    }

    @GraphQLField
    public CDPPageInfo pageInfo(final DataFetchingEnvironment environment) {
        if (userLists == null) {
            return null;
        }

        final Integer first = environment.getArgument("first") != null ? environment.getArgument("first") : 0;

        return new CDPPageInfo(userLists.getOffset() > 0, userLists.getTotalSize() > first + userLists.getList().size());
    }

}
