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
package org.apache.unomi.sdl.graphql.schema;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLTypeVisitorStub;
import graphql.schema.SchemaTransformer;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static graphql.util.TreeTransformerUtil.changeNode;

@Component(immediate = true)
public class DefaultGraphQLProvider implements GraphQLProvider {

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public GraphQL getGraphQL() {
        final RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.Time)
                .type("CDP_EventInterface", typeWiring -> typeWiring
                        .typeResolver(env -> null))
                .type("CDP_ProfileInterface", typeWiring -> typeWiring
                        .typeResolver(env -> {
                            env.getObject();
                            return null;
                        }))
                .type("CDP_PropertyInterface", typeWiring -> typeWiring
                        .typeResolver(env -> null))
                .transformer(this::transformSchema)
                .build();

        final SchemaParser schemaParser = new SchemaParser();
        final SchemaGenerator schemaGenerator = new SchemaGenerator();

        try (final InputStream inputStream = getSchemaAsInputStream("schema.graphql")) {
            TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

            typeRegistry.merge(schemaParser.parse(inputStream));

            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

            return GraphQL.newGraphQL(graphQLSchema)
                    .instrumentation(new TracingInstrumentation())
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private InputStream getSchemaAsInputStream(String resourceUrl) {
        try {
            return bundleContext.getBundle().getResource(resourceUrl).openConnection().getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private GraphQLSchema transformSchema(GraphQLSchema graphQLSchema) {
        SchemaTransformer schemaTransformer = new SchemaTransformer();

        return schemaTransformer.transform(graphQLSchema, new GraphQLTypeVisitorStub() {

            @Override
            public TraversalControl visitGraphQLObjectType(GraphQLObjectType node, TraverserContext<GraphQLSchemaElement> context) {
                final GraphQLCodeRegistry.Builder registryBuilder = context.getVarFromParents(GraphQLCodeRegistry.Builder.class);

                if (node.getName().equals("CDP_Profile")) {
                    final GraphQLObjectType transformedNode = node.transform(builder -> builder.field(
                            GraphQLFieldDefinition.newFieldDefinition().name("dynamicField").type(Scalars.GraphQLString).build()));

                    registryBuilder.dataFetcher(FieldCoordinates.coordinates("CDP_Profile", "dynamicField"),
                            (DataFetcher<String>) environment -> "I'm dynamic filed of CDP_Profile");

                    return changeNode(context, transformedNode);
                }

                if ("CDP_Segment".equals(node.getName())) {
                    final GraphQLObjectType transformedNode = node.transform(builder -> builder.field(
                            GraphQLFieldDefinition.newFieldDefinition().name("dynamicField").type(Scalars.GraphQLString).build()));

                    registryBuilder.dataFetcher(FieldCoordinates.coordinates("CDP_Segment", "dynamicField"),
                            (DataFetcher<String>) environment -> "I'm dynamic filed of CDP_Segment");

                    return changeNode(context, transformedNode);
                }

                return visitGraphQLType(node, context);
            }

        });
    }



}
