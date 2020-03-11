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
package org.apache.unomi.graphql.schema;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.annotations.AnnotationsSchemaCreator;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.graphQLProcessors.GraphQLInputProcessor;
import graphql.annotations.processor.graphQLProcessors.GraphQLOutputProcessor;
import graphql.annotations.processor.typeFunctions.DefaultTypeFunction;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.language.InputObjectTypeDefinition;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.apache.unomi.api.PropertyType;
import org.apache.unomi.api.services.ProfileService;
import org.apache.unomi.graphql.fetchers.CustomerPropertyDataFetcher;
import org.apache.unomi.graphql.providers.GraphQLAdditionalTypesProvider;
import org.apache.unomi.graphql.providers.GraphQLCodeRegistryProvider;
import org.apache.unomi.graphql.providers.GraphQLExtensionsProvider;
import org.apache.unomi.graphql.providers.GraphQLMutationProvider;
import org.apache.unomi.graphql.providers.GraphQLProcessEventsProvider;
import org.apache.unomi.graphql.providers.GraphQLQueryProvider;
import org.apache.unomi.graphql.providers.GraphQLTypesProvider;
import org.apache.unomi.graphql.types.RootMutation;
import org.apache.unomi.graphql.types.RootQuery;
import org.apache.unomi.graphql.types.input.CDPEventInput;
import org.apache.unomi.graphql.types.input.CDPProfilePropertiesFilterInput;
import org.apache.unomi.graphql.types.input.CDPProfileUpdateEventInput;
import org.apache.unomi.graphql.types.output.CDPProfile;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component(service = GraphQLSchemaUpdater.class)
public class GraphQLSchemaUpdater {

    private final List<GraphQLQueryProvider> queryProviders = new ArrayList<>();

    private final List<GraphQLMutationProvider> mutationProviders = new ArrayList<>();

    private final List<GraphQLTypesProvider> typesProviders = new ArrayList<>();

    private final List<GraphQLExtensionsProvider> extensionsProviders = new ArrayList<>();

    private final List<GraphQLProcessEventsProvider> eventsProviders = new ArrayList<>();

    private final List<GraphQLAdditionalTypesProvider> additionalTypesProviders = new ArrayList<>();

    private GraphQLCodeRegistryProvider codeRegistryProvider;

    private GraphQL graphQL;

    private ProfileService profileService;

    private ScheduledExecutorService executorService;

    private ScheduledFuture<?> updateFuture;

    private boolean isActivated;

    private GraphQLAnnotations graphQLAnnotations;

    private GraphQLInputProcessor graphQLInputProcessor;

    private GraphQLOutputProcessor graphQLOutputProcessor;

    @Activate
    public void activate() {
        this.isActivated = true;
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        updateSchema();
    }

    @Deactivate
    public void deactivate() {
        this.isActivated = false;

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setGraphQLInputProcessor(GraphQLInputProcessor graphQLInputProcessor) {
        this.graphQLInputProcessor = graphQLInputProcessor;
    }

    public void unsetGraphQLInputProcessor(GraphQLInputProcessor graphQLInputProcessor) {
        this.graphQLInputProcessor = null;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setGraphQLOutputProcessor(GraphQLOutputProcessor graphQLOutputProcessor) {
        this.graphQLOutputProcessor = graphQLOutputProcessor;
    }

    public void unsetGraphQLOutputProcessor(GraphQLOutputProcessor graphQLOutputProcessor) {
        this.graphQLOutputProcessor = null;
    }


    @Reference
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindQueryProvider(GraphQLQueryProvider provider) {
        queryProviders.add(provider);

        updateSchema();
    }

    public void unbindQueryProvider(GraphQLQueryProvider provider) {
        queryProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindMutationProvider(GraphQLMutationProvider provider) {
        mutationProviders.add(provider);

        updateSchema();
    }

    public void unbindMutationProvider(GraphQLMutationProvider provider) {
        mutationProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindTypesProvider(GraphQLTypesProvider provider) {
        typesProviders.add(provider);

        updateSchema();
    }

    public void unbindTypesProvider(GraphQLTypesProvider provider) {
        typesProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindExtensionsProvider(GraphQLExtensionsProvider provider) {
        extensionsProviders.add(provider);

        updateSchema();
    }

    public void unbindExtensionsProvider(GraphQLExtensionsProvider provider) {
        extensionsProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindEventsProviders(GraphQLProcessEventsProvider provider) {
        eventsProviders.add(provider);

        updateSchema();
    }

    public void unbindEventsProviders(GraphQLProcessEventsProvider provider) {
        eventsProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindAdditionalTypes(GraphQLAdditionalTypesProvider provider) {
        additionalTypesProviders.add(provider);

        updateSchema();
    }

    public void unbindAdditionalTypes(GraphQLAdditionalTypesProvider provider) {
        additionalTypesProviders.remove(provider);

        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void bindCodeRegistryProvider(GraphQLCodeRegistryProvider codeRegistryProvider) {
        this.codeRegistryProvider = codeRegistryProvider;

        updateSchema();
    }

    public void unbindCodeRegistryProvider(GraphQLCodeRegistryProvider codeRegistryProvider) {
        this.codeRegistryProvider = null;

        updateSchema();
    }

    public void updateSchema() {
        if (!isActivated) {
            return;
        }

        final GraphQLSchema graphQLSchema = createGraphQLSchema();

        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();

//        try {
//            if (updateFuture != null) {
//                updateFuture.cancel(true);
//            }
//
//            updateFuture = executorService.scheduleWithFixedDelay(() -> {
//                final GraphQLSchema graphQLSchema = createGraphQLSchema();
//
//                this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
//            }, 0, 60, TimeUnit.SECONDS); // TODO should be configurable
//        } catch (Exception e) {
//            executorService.shutdown();
//            throw e;
//        }
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

    private GraphQLSchema createGraphQLSchema() {
        this.graphQLAnnotations = new GraphQLAnnotations();

        final AnnotationsSchemaCreator.Builder schemaBuilder = AnnotationsSchemaCreator.newAnnotationsSchema();

        setUpContainer();
        setUpTypes(schemaBuilder);
        setUpExtensions();
        setUpCodeRegister();
//        setUpDynamicFields();

        createEventInputTypes();

        final Map<String, GraphQLType> typeRegistry = graphQLAnnotations.getContainer().getTypeRegistry();

        setUpQueries(typeRegistry);
        setUpMutations(typeRegistry);

        additionalTypesProviders.forEach(additionalTypesProvider -> {
            if (additionalTypesProvider.getAdditionalTypes() != null) {
                schemaBuilder.additionalTypes(additionalTypesProvider.getAdditionalTypes());
            }
        });

        graphQLAnnotations.registerTypeFunction(new DefaultTypeFunction(graphQLInputProcessor, graphQLOutputProcessor));

        return schemaBuilder
                .query(RootQuery.class)
                .mutation(RootMutation.class)
                .setAnnotationsProcessor(graphQLAnnotations)
                .build();
    }

    private void registerDynamicOutputFields(
            final GraphQLAnnotations graphQLAnnotations, final String target, final String graphQLTypeName, final Class<?> clazz) {
        final Collection<PropertyType> propertyTypes = profileService.getTargetPropertyTypes(target);

        final GraphQLCodeRegistry.Builder codeRegisterBuilder = graphQLAnnotations.getContainer().getCodeRegistryBuilder();

        final List<GraphQLFieldDefinition> fieldDefinitions = propertyTypes.stream().filter(propertyType -> propertyType != null && propertyType.getItemId().matches("[_A-Za-z][_0-9A-Za-z]*")).map(propertyType -> {

            final GraphQLFieldDefinition.Builder fieldBuilder = GraphQLFieldDefinition.newFieldDefinition();

            fieldBuilder.type((GraphQLOutputType) convert(propertyType.getValueTypeId()));
            fieldBuilder.name(propertyType.getItemId());

            codeRegisterBuilder.dataFetcher(
                    FieldCoordinates.coordinates(graphQLTypeName, propertyType.getItemId()),
                    new CustomerPropertyDataFetcher(propertyType.getItemId()));

            return fieldBuilder.build();
        }).collect(Collectors.toList());

        final GraphQLObjectType transformedObjectType = graphQLAnnotations.object(clazz)
                .transform(builder -> fieldDefinitions.forEach(builder::field));

        graphQLAnnotations.getContainer().getTypeRegistry().put(graphQLTypeName, transformedObjectType);
    }

    private void registerDynamicInputFields(
            final GraphQLAnnotations graphQLAnnotations, final String target, final String graphQLTypeName, final Class<?> clazz) {
        final Collection<PropertyType> propertyTypes = profileService.getTargetPropertyTypes(target);

        final List<GraphQLInputObjectField> fieldDefinitions = propertyTypes.stream().filter(propertyType -> propertyType != null && propertyType.getItemId().matches("[_A-Za-z][_0-9A-Za-z]*")).map(propertyType -> {

            final GraphQLInputObjectField.Builder fieldBuilder = GraphQLInputObjectField.newInputObjectField();

            fieldBuilder.type((GraphQLInputType) convert(propertyType.getValueTypeId()));
            fieldBuilder.name(propertyType.getItemId());

            return fieldBuilder.build();
        }).collect(Collectors.toList());

        final GraphQLInputObjectType transformedObjectType = getInputObjectType(clazz)
                .transform(builder -> fieldDefinitions.forEach(builder::field));

        graphQLAnnotations.getContainer().getTypeRegistry().put(graphQLTypeName, transformedObjectType);
    }

    private void setUpTypes(final AnnotationsSchemaCreator.Builder schemaBuilder) {
        if (!typesProviders.isEmpty()) {
            for (GraphQLTypesProvider typesProvider : typesProviders) {
                if (typesProvider.getTypes() != null) {
                    schemaBuilder.additionalTypes(typesProvider.getTypes());
                }
            }
        }
    }

    private void setUpExtensions() {
        if (!extensionsProviders.isEmpty()) {
            for (GraphQLExtensionsProvider extensionsProvider : extensionsProviders) {
                if (extensionsProvider.getExtensions() != null) {
                    extensionsProvider.getExtensions().forEach(graphQLAnnotations::registerTypeExtension);
                }
            }
        }
    }

    private void setUpCodeRegister() {
        if (codeRegistryProvider != null) {
            graphQLAnnotations.getContainer().setCodeRegistryBuilder(
                    codeRegistryProvider.getCodeRegistry(
                            graphQLAnnotations.getContainer().getCodeRegistryBuilder().build()
                    ));
        }
    }

    private void setUpDynamicFields() {
        registerDynamicOutputFields(graphQLAnnotations, "profiles", CDPProfile.TYPE_NAME, CDPProfile.class);
        registerDynamicInputFields(graphQLAnnotations, "profiles", CDPProfilePropertiesFilterInput.TYPE_NAME, CDPProfilePropertiesFilterInput.class);
    }

    private void setUpQueries(final Map<String, GraphQLType> typeRegistry) {
        if (!queryProviders.isEmpty()) {
            for (GraphQLQueryProvider queryProvider : queryProviders) {
                final Set<GraphQLFieldDefinition> queries = queryProvider.getQueries(graphQLAnnotations);

                if (queries != null) {
                    final GraphQLObjectType transformedCdpQueryType = graphQLAnnotations.object(RootQuery.class)
                            .transform(builder -> queries.forEach(builder::field));
                    typeRegistry.put(RootQuery.TYPE_NAME, transformedCdpQueryType);
                }
            }
        }
    }

    private void setUpMutations(final Map<String, GraphQLType> typeRegistry) {
        if (!mutationProviders.isEmpty()) {
            for (GraphQLMutationProvider mutationProvider : mutationProviders) {
                final Set<GraphQLFieldDefinition> mutations = mutationProvider.getMutations(graphQLAnnotations);

                if (mutations != null) {
                    final GraphQLObjectType transformedMutationType = graphQLAnnotations.object(RootMutation.class)
                            .transform(builder -> mutations.forEach(builder::field));
                    typeRegistry.put(RootMutation.TYPE_NAME, transformedMutationType);
                }
            }
        }
    }

    private void setUpContainer() {
        final ProcessingElementsContainer container = graphQLAnnotations.getContainer();

        container.setInputPrefix("");
        container.setInputSuffix("");
    }

    private GraphQLType convert(final String type) {
        switch (type) {
            case "integer":
                return Scalars.GraphQLInt;
            case "float":
                return Scalars.GraphQLFloat;
            case "set":
                return null; // TODO
            case "boolean":
                return Scalars.GraphQLBoolean;
            default: {
                return Scalars.GraphQLString;
            }
        }
    }

    private void createEventInputTypes() {
        final Collection<PropertyType> propertyTypes = profileService.getTargetPropertyTypes("profiles");

        if (propertyTypes == null) {
            return;
        }

        final GraphQLInputObjectType.Builder profileUpdateEventInput = GraphQLInputObjectType.newInputObject()
                .name(CDPProfileUpdateEventInput.TYPE_NAME)
                .fields(getInputObjectType(CDPProfileUpdateEventInput.class).getFieldDefinitions())
                .definition(InputObjectTypeDefinition.newInputObjectDefinition()
                        .additionalData("clazz", CDPProfileUpdateEventInput.class.getName()).build()
                );

        propertyTypes.stream().filter(propertyType -> propertyType != null && propertyType.getItemId().matches("[_A-Za-z][_0-9A-Za-z]*"))
                .forEach(propertyType -> profileUpdateEventInput.field(
                        GraphQLInputObjectField.newInputObjectField()
                                .type((GraphQLInputType) convert(propertyType.getValueTypeId()))
                                .name(propertyType.getItemId())
                                .build()));

        final Map<String, GraphQLType> typeRegistry = graphQLAnnotations.getTypeRegistry();

        typeRegistry.put(CDPProfileUpdateEventInput.TYPE_NAME, profileUpdateEventInput.build());

        final GraphQLInputObjectType.Builder builder = GraphQLInputObjectType.newInputObject()
                .name("CDP_EventInput")
                .fields(getInputObjectType(CDPEventInput.class).getFieldDefinitions());

        builder.field(GraphQLInputObjectField.newInputObjectField()
                .name("cdp_profileUpdateEvent")
                .type(profileUpdateEventInput)
                .build());

        if (!eventsProviders.isEmpty()) {
            eventsProviders.forEach(eventProvider -> {
                if (eventProvider.getProcessEvents() != null) {
                    eventProvider.getProcessEvents().forEach(eventProcessor -> {
                        int index = eventProcessor.getSimpleName().indexOf("_");
                        String eventName = eventProcessor.getSimpleName().substring(0, index).toLowerCase() + "_" + eventProcessor.getSimpleName().substring(index + 1);
                        String fieldName = eventName.replace("Input", "");

                        final GraphQLInputObjectType.Builder eventInput = GraphQLInputObjectType.newInputObject()
                                .name(eventProcessor.getSimpleName())
                                .fields(getInputObjectType(eventProcessor).getFieldDefinitions())
                                .definition(InputObjectTypeDefinition.newInputObjectDefinition()
                                        .additionalData("clazz", eventProcessor.getName()).build()
                                );

                        builder.field(GraphQLInputObjectField.newInputObjectField()
                                .name(fieldName)
                                .type(eventInput)
                                .build());
                    });
                }
            });
        }

        typeRegistry.put("CDP_EventInput", builder.build());
    }

    public GraphQLInputObjectType getInputObjectType(final Class<?> annotatedClass) {
        return (GraphQLInputObjectType) graphQLAnnotations.getObjectHandler().getTypeRetriever()
                .getGraphQLType(annotatedClass, graphQLAnnotations.getContainer(), true);
    }

}
