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
package org.apache.unomi.graphql.commands;

import com.google.common.base.Strings;
import graphql.schema.DataFetchingEnvironment;
import org.apache.unomi.api.Persona;
import org.apache.unomi.graphql.services.ServiceManager;

import java.util.Map;
import java.util.Objects;

public class DeleteAllPersonalDataCommand extends BaseCommand<Boolean> {

    private final DataFetchingEnvironment environment;

    private DeleteAllPersonalDataCommand(Builder builder) {
        super(builder);

        this.environment = builder.environment;
    }

    @Override
    public Boolean execute() {
        final ServiceManager serviceManager = environment.getContext();

        final Map<String, Object> cdpProfileIdInput = environment.getArgument("profileID");

        final String personaId = (String) cdpProfileIdInput.get("id");

        final Persona persona = serviceManager.getProfileService().loadPersona(personaId);

        if (persona == null) {
            throw new IllegalStateException(String.format("The persona with id \"%s\" not found", personaId));
        }

        serviceManager.getProfileService().delete(personaId, true);

        return true;
    }

    public static Builder create(final DataFetchingEnvironment environment) {
        return new Builder(environment);
    }

    public static class Builder extends BaseCommand.Builder<Builder> {

        private final DataFetchingEnvironment environment;


        public Builder(final DataFetchingEnvironment environment) {
            this.environment = environment;
        }

        private void validate() {
            final Map<String, Object> cdpProfileIDInput = environment.getArgument("profileID");

            Objects.requireNonNull(cdpProfileIDInput, "The \"profileID\" variable can not be null");

            final String personaId = (String) cdpProfileIDInput.get("id");

            if (Strings.isNullOrEmpty(personaId)) {
                throw new IllegalArgumentException("The \"id\" variable can not be null or empty");
            }
        }

        public DeleteAllPersonalDataCommand build() {
            validate();

            return new DeleteAllPersonalDataCommand(this);
        }

    }

}