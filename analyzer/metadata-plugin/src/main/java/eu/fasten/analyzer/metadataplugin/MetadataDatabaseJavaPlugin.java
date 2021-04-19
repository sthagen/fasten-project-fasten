/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.analyzer.metadataplugin;

import eu.fasten.core.data.*;
import eu.fasten.core.data.metadatadb.MetadataDao;
import eu.fasten.core.data.metadatadb.codegen.enums.Access;
import eu.fasten.core.data.metadatadb.codegen.enums.CallableType;
import eu.fasten.core.data.metadatadb.codegen.enums.CallType;
import eu.fasten.core.data.metadatadb.codegen.tables.records.CallSitesRecord;
import eu.fasten.core.data.metadatadb.codegen.tables.records.CallablesRecord;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.json.JSONObject;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import java.util.*;
import java.io.File;

public class MetadataDatabaseJavaPlugin extends Plugin {
    public MetadataDatabaseJavaPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class MetadataDBJavaExtension extends MetadataDBExtension {
        private static DSLContext dslContext;

        @Override
        public void setDBConnection(Map<String, DSLContext> dslContexts) {
            MetadataDBJavaExtension.dslContext = dslContexts.get(Constants.mvnForge);
        }

        @Override
        public DSLContext getDBConnection() {
            return MetadataDBJavaExtension.dslContext;
        }

        /**
         * Sets outputPath to a JSON file where plugin's output can be stored.
         *
         * @param callgraph Callgraph which contains information needed for output path
         */
        @Override
        protected void setOutputPath(ExtendedRevisionCallGraph callgraph) {
            var forge = callgraph.forge;
            final String groupId = callgraph.product.split(Constants.mvnCoordinateSeparator)[0];
            var product = callgraph.getRevisionName();
            var firstLetter = product.substring(0, 1);
            this.outputPath = File.separator + forge + File.separator
                    + firstLetter + File.separator
                    + groupId + File.separator + product + ".json";
        }

        protected Map<String, Long> getNamespaceMap(ExtendedRevisionCallGraph graph, MetadataDao metadataDao) {
            ExtendedRevisionJavaCallGraph javaGraph = (ExtendedRevisionJavaCallGraph) graph;
            var namespaces = new HashSet<String>();
            javaGraph.getClassHierarchy().get(JavaScope.internalTypes).keySet().forEach(k -> namespaces.add(k.toString()));
            javaGraph.getClassHierarchy().get(JavaScope.externalTypes).keySet().forEach(k -> namespaces.add(k.toString()));
            javaGraph.getClassHierarchy().get(JavaScope.internalTypes).values().forEach(v -> namespaces.addAll(JavaType.toListOfString(v.getSuperInterfaces())));
            javaGraph.getClassHierarchy().get(JavaScope.internalTypes).values().forEach(v -> namespaces.addAll(JavaType.toListOfString(v.getSuperClasses())));
            javaGraph.getClassHierarchy().get(JavaScope.externalTypes).values().forEach(v -> namespaces.addAll(JavaType.toListOfString(v.getSuperInterfaces())));
            javaGraph.getClassHierarchy().get(JavaScope.externalTypes).values().forEach(v -> namespaces.addAll(JavaType.toListOfString(v.getSuperClasses())));
            for (var edgeEntry : graph.getGraph().getExternalCalls().entrySet()) {
                for (var obj : edgeEntry.getValue().keySet()) {
                    var pc = obj.toString();
                    var metadataMap = (Map<String, Object>) edgeEntry.getValue().get(Integer.parseInt(pc));
                    var callMetadata = new JSONObject();
                    for (var key : metadataMap.keySet()) {
                        callMetadata.put(key, metadataMap.get(key));
                    }
                    String receiverUri = callMetadata.optString("receiver");
                    if (!receiverUri.isEmpty()) {
                        namespaces.add(receiverUri);
                    }
                }
            }
            return metadataDao.insertNamespaces(namespaces);
        }

        public Pair<ArrayList<CallablesRecord>, Integer> insertDataExtractCallables(
                ExtendedRevisionCallGraph callgraph, MetadataDao metadataDao, long packageVersionId,
                Map<String, Long> namespaceMap) {
            ExtendedRevisionJavaCallGraph javaCallGraph = (ExtendedRevisionJavaCallGraph) callgraph;
            var callables = new ArrayList<CallablesRecord>();
            var cha = javaCallGraph.getClassHierarchy();
            var internalTypes = cha.get(JavaScope.internalTypes);
            // Insert all modules, files, module contents and extract callables from internal types
            for (var fastenUri : internalTypes.keySet()) {
                var type = internalTypes.get(fastenUri);
                var moduleId = insertModule(type, FastenURI.create(fastenUri), packageVersionId,
                    metadataDao);
                var fileId = metadataDao.insertFile(packageVersionId, type.getSourceFileName());
                metadataDao.insertModuleContent(moduleId, fileId);
                callables.addAll(extractCallablesFromType(type, moduleId, true));
            }

            var numInternal = callables.size();

            var externalTypes = cha.get(JavaScope.externalTypes);
            // Extract all external callables
            for (var fastenUri : externalTypes.keySet()) {
                var type = externalTypes.get(fastenUri);
                callables.addAll(extractCallablesFromType(type, -1L, false));
            }
            return new ImmutablePair<>(callables, numInternal);
        }

        protected long insertModule(JavaType type, FastenURI fastenUri, long packageVersionId,
                                    Map<String, Long> namespaceMap, MetadataDao metadataDao) {
            var isFinal = type.isFinal();
            var access = getAccess(type.getAccess());
            var superClasses = JavaType.toListOfString(type.getSuperClasses()).stream().map(namespaceMap::get).toArray(Long[]::new);
            var superInterfaces = JavaType.toListOfString(type.getSuperInterfaces()).stream().map(namespaceMap::get).toArray(Long[]::new);
            return metadataDao.insertModule(packageVersionId, namespaceMap.get(fastenUri.toString()),
                    isFinal, access, superClasses, superInterfaces, null);
        }

        private List<CallablesRecord> extractCallablesFromType(JavaType type,
                                                               long moduleId, boolean isInternal) {
            // Extracts a list of all callable records and their metadata from the type
            var callables = new ArrayList<CallablesRecord>(type.getMethods().size());

            for (var methodEntry : type.getMethods().entrySet()) {
                // Get Local ID
                var localId = (long) methodEntry.getKey();

                // Get FASTEN URI
                var uri = methodEntry.getValue().getUri().toString();

                // Collect metadata
                var callableMetadata = new JSONObject(methodEntry.getValue().getMetadata());
                Integer firstLine = null;
                if (callableMetadata.has("first")
                        && !(callableMetadata.get("first") instanceof String)) {
                    firstLine = callableMetadata.getInt("first");
                }
                callableMetadata.remove("first");
                Integer lastLine = null;
                if (callableMetadata.has("last")
                        && !(callableMetadata.get("last") instanceof String)) {
                    lastLine = callableMetadata.getInt("last");
                }
                callableMetadata.remove("last");
                CallableType callableType = null;
                if (callableMetadata.has("type") && (callableMetadata.get("type") instanceof String)) {
                    callableType = getCallableType(callableMetadata.getString("type"));
                }
                callableMetadata.remove("type");
                Boolean callableDefined = null;
                if (callableMetadata.has("defined") && (callableMetadata.get("defined") instanceof Boolean)) {
                    callableDefined = callableMetadata.getBoolean("defined");
                }
                callableMetadata.remove("defined");
                Access access = null;
                if (callableMetadata.has("access") && (callableMetadata.get("access") instanceof String)) {
                    access = getAccess(callableMetadata.getString("access"));
                }
                callableMetadata.remove("access");
                // Add a record to the list
                callables.add(new CallablesRecord(localId, moduleId, uri, isInternal,
                        firstLine, lastLine, callableType, callableDefined, access,
                        JSONB.valueOf(callableMetadata.toString())));
            }
            return callables;
        }

        protected List<CallSitesRecord> insertEdges(Graph graph, Long2LongOpenHashMap lidToGidMap,
                                                Map<String, Long> namespaceMap, MetadataDao metadataDao) {
            final var numEdges = graph.getInternalCalls().size() + graph.getExternalCalls().size();

            // Map of all edges (internal and external)
            var externalCalls = graph.getExternalCalls();

            var edges = new ArrayList<CallSitesRecord>(numEdges);
            for (var edgeEntry : externalCalls.entrySet()) {

                // Get Global ID of the source callable
                var source = lidToGidMap.get((long) edgeEntry.getKey().firstInt());
                // Get Global ID of the target callable
                var target = lidToGidMap.get((long) edgeEntry.getKey().secondInt());

                // Create receivers
                var receivers = new Long[edgeEntry.getValue().size()];
                var counter = 0;
                for (var obj : edgeEntry.getValue().keySet()) {
                    var pc = obj.toString();
                    // Get edge metadata
                    var metadataMap = (Map<String, Object>) edgeEntry.getValue().get(Integer.parseInt(pc));
                    var callMetadata = new JSONObject();
                    for (var key : metadataMap.keySet()) {
                        callMetadata.put(key, metadataMap.get(key));
                    }
                    String receiverUri = callMetadata.optString("receiver");
                    receivers[counter++] = namespaceMap.get(receiverUri);
                }
                Integer line = null; //callMetadata.optInt("line", -1);
                CallType type = null; // this.getReceiverType(callMetadata.optString("type"));
                // Add edge record to the list of records
                edges.add(new CallSitesRecord(source, target, line, type, receivers, null));
            }

            var internalCalls = graph.getInternalCalls();
            for (var edgeEntry : internalCalls.entrySet()) {
                // Get Global ID of the source callable
                var source = lidToGidMap.get((long) edgeEntry.getKey().get(0));
                // Get Global ID of the target callable
                var target = lidToGidMap.get((long) edgeEntry.getKey().get(1));
                edges.add(new CallSitesRecord(source, target, null, null, null, null));
            }

            // Batch insert all edges
            final var edgesIterator = edges.iterator();
            while (edgesIterator.hasNext()) {
                var edgesBatch = new ArrayList<CallSitesRecord>(Constants.insertionBatchSize);
                while (edgesIterator.hasNext()
                        && edgesBatch.size() < Constants.insertionBatchSize) {
                    edgesBatch.add(edgesIterator.next());
                }
                metadataDao.batchInsertEdges(edgesBatch);
            }
            return edges;
        }

        private CallType getReceiverType(String type) {
            switch (type) {
                case "invokestatic":
                    return CallType.static_;
                case "invokespecial":
                    return CallType.special;
                case "invokevirtual":
                    return CallType.virtual;
                case "invokedynamic":
                    return CallType.dynamic;
                case "invokeinterface":
                    return CallType.interface_;
                default:
                    return null;
            }
        }
    }
}

