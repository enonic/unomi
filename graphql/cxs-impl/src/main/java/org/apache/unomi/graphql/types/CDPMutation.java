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
package org.apache.unomi.graphql.types;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLID;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.graphql.commands.CreateOrUpdateProfilePropertiesCommand;
import org.apache.unomi.graphql.commands.DeleteAllPersonalDataCommand;
import org.apache.unomi.graphql.commands.DeleteProfileCommand;
import org.apache.unomi.graphql.commands.DeleteProfilePropertiesCommand;
import org.apache.unomi.graphql.commands.ProcessEventsCommand;
import org.apache.unomi.graphql.commands.segments.CreateOrUpdateSegmentCommand;
import org.apache.unomi.graphql.commands.segments.DeleteSegmentCommand;
import org.apache.unomi.graphql.types.input.CDPEventInput;
import org.apache.unomi.graphql.types.input.CDPProfileIDInput;
import org.apache.unomi.graphql.types.input.CDPPropertyInput;
import org.apache.unomi.graphql.types.input.CDPSegmentInput;
import org.apache.unomi.graphql.types.output.CDPSegment;

import java.util.List;

import static org.apache.unomi.graphql.CDPGraphQLConstants.SEGMENT_ARGUMENT_NAME;
import static org.apache.unomi.graphql.types.CDPMutation.TYPE_NAME;

@GraphQLName(TYPE_NAME)
public class CDPMutation {

    public static final String TYPE_NAME = "CDP_Mutation";

    @GraphQLField
    public boolean createOrUpdateProfileProperties(
            final @GraphQLName("properties") List<CDPPropertyInput> properties,
            final DataFetchingEnvironment environment) {

        return CreateOrUpdateProfilePropertiesCommand.create(properties)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public int processEvents(
            final @GraphQLNonNull @GraphQLName("events") List<CDPEventInput> eventInputs,
            final DataFetchingEnvironment environment
    ) {
        return ProcessEventsCommand.create(eventInputs, environment)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public boolean deleteProfile(
            final @GraphQLNonNull @GraphQLName("profileID") CDPProfileIDInput profileIDInput,
            final DataFetchingEnvironment environment) {
        return DeleteProfileCommand.create(environment)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public boolean deleteProfileProperties(
            final @GraphQLNonNull @GraphQLName("propertyNames") List<String> propertyNames,
            final DataFetchingEnvironment environment) {
        return DeleteProfilePropertiesCommand.create(propertyNames)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public boolean deleteAllPersonalData(
            final @GraphQLNonNull @GraphQLName("profileID") CDPProfileIDInput profileIDInput,
            final DataFetchingEnvironment environment) {
        return DeleteAllPersonalDataCommand.create(environment)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public CDPSegment createOrUpdateSegment(
            final @GraphQLName(SEGMENT_ARGUMENT_NAME) CDPSegmentInput segmentInput,
            final DataFetchingEnvironment environment
    ) {
        return CreateOrUpdateSegmentCommand.create(segmentInput, environment)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

    @GraphQLField
    public boolean deleteSegment(
            final @GraphQLID @GraphQLName("segmentID") String segmentId,
            final DataFetchingEnvironment environment
    ) {
        return DeleteSegmentCommand.create(segmentId)
                .setServiceManager(environment.getContext())
                .build()
                .execute();
    }

}
