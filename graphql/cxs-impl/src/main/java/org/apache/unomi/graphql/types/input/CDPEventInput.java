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
package org.apache.unomi.graphql.types.input;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLID;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName("CDP_Event")
public class CDPEventInput {

    public static final String TYPE_NAME = "CDP_EventInput";

    @GraphQLID
    @GraphQLField
    private String id;

    @GraphQLField
    private String cdp_sourceID;

    @GraphQLField
    @GraphQLNonNull
    private CDPProfileIDInput cdp_profileID;

    @GraphQLID
    @GraphQLField
    @GraphQLNonNull
    private String cdp_objectID;

    @GraphQLField
    private CDPConsentUpdateEventInput cdp_consentUpdateEvent;

    public CDPEventInput(
            final @GraphQLID @GraphQLName("id") String id,
            final @GraphQLName("cdp_sourceID") String cdp_sourceID,
            final @GraphQLNonNull @GraphQLName("cdp_profileID") CDPProfileIDInput cdp_profileID,
            final @GraphQLID @GraphQLNonNull @GraphQLName("cdp_objectID") String cdp_objectID,
            final @GraphQLName("cdp_consentUpdateEvent") CDPConsentUpdateEventInput cdp_consentUpdateEvent) {
        this.id = id;
        this.cdp_sourceID = cdp_sourceID;
        this.cdp_profileID = cdp_profileID;
        this.cdp_objectID = cdp_objectID;
        this.cdp_consentUpdateEvent = cdp_consentUpdateEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdp_sourceID() {
        return cdp_sourceID;
    }

    public void setCdp_sourceID(String cdp_sourceID) {
        this.cdp_sourceID = cdp_sourceID;
    }

    public CDPProfileIDInput getCdp_profileID() {
        return cdp_profileID;
    }

    public void setCdp_profileID(CDPProfileIDInput cdp_profileID) {
        this.cdp_profileID = cdp_profileID;
    }

    public String getCdp_objectID() {
        return cdp_objectID;
    }

    public void setCdp_objectID(String cdp_objectID) {
        this.cdp_objectID = cdp_objectID;
    }

    public CDPConsentUpdateEventInput getCdp_consentUpdateEvent() {
        return cdp_consentUpdateEvent;
    }

    public CDPEventInput setCdp_consentUpdateEvent(CDPConsentUpdateEventInput cdp_consentUpdateEvent) {
        this.cdp_consentUpdateEvent = cdp_consentUpdateEvent;
        return this;
    }

}
