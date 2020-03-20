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

package org.apache.unomi.graphql.fetchers.list;

import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.api.PartialList;
import org.apache.unomi.api.Profile;
import org.apache.unomi.api.conditions.Condition;
import org.apache.unomi.api.query.Query;
import org.apache.unomi.graphql.fetchers.ConnectionParams;
import org.apache.unomi.graphql.fetchers.ProfileConnectionDataFetcher;
import org.apache.unomi.graphql.services.ServiceManager;
import org.apache.unomi.graphql.types.output.CDPList;
import org.apache.unomi.graphql.types.output.CDPProfileConnection;

public class ListProfileConnectionDataFetcher extends ProfileConnectionDataFetcher {

    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inctive";

    private String type;

    public ListProfileConnectionDataFetcher(final String type) {
        this.type = type;
    }

    @Override
    public CDPProfileConnection get(DataFetchingEnvironment environment) throws Exception {
        final CDPList cdpList = environment.getSource();
        final ServiceManager serviceManager = environment.getContext();
        final ConnectionParams params = parseConnectionParams(environment);

        Condition listIdCondition = createPropertyCondition("systemProperties.lists", "contains", cdpList.getId(), serviceManager.getDefinitionsService());

        final Query query = new Query();
        query.setOffset(params.getFirst());
        query.setLimit(params.getSize());
        query.setCondition(listIdCondition);

        PartialList<Profile> profiles = serviceManager.getProfileService().search(query, Profile.class);

        return createProfileConnection(profiles);
    }

}