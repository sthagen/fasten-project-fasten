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

package eu.fasten.analyzer.restapiplugin.api;

import eu.fasten.analyzer.restapiplugin.KnowledgeBaseConnector;
import eu.fasten.analyzer.restapiplugin.LazyIngestionProvider;
import eu.fasten.core.data.Constants;
import eu.fasten.core.utils.FastenUriUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StitchingApi {
    
    private static final Logger LOG = LoggerFactory.getLogger(StitchingApi.class);

    private LazyIngestionProvider ingestion = new LazyIngestionProvider();

    public void setLazyIngestionProvider(LazyIngestionProvider ingestion) {
        this.ingestion = ingestion;
    }    

    @PostMapping(value = "/callable_uris", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resolveCallablesToUris(@RequestBody List<Long> gidList) {
        var fastenUris = KnowledgeBaseConnector.kbDao.getFullFastenUris(gidList);
        var json = new JSONObject();
        fastenUris.forEach((key, value) -> json.put(String.valueOf(key), value));
        var result = json.toString();
        return Responses.ok(result);
    }

    @PostMapping(value = "/metadata/callables", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getCallablesMetadata(@RequestBody List<String> fullFastenUris,
                                                @RequestParam(required = false, defaultValue = "false") boolean allAttributes,
                                                @RequestParam(required = false, defaultValue = "[]") List<String> attributes) {
        var total = System.currentTimeMillis();
        LOG.info("Received a list of callables");
        if (!allAttributes && attributes == null) {
            return new ResponseEntity<>("Either 'allAttributes' must be 'true' or a list of 'attributes' must be provided", HttpStatus.BAD_REQUEST);
        }
        Map<String, List<String>> packageVersionUris;
        LOG.info("Parsing full FASTEN URIs and grouping callables by package version");
        var start = System.currentTimeMillis();
        try {
            packageVersionUris = fullFastenUris.stream().map(FastenUriUtils::parseFullFastenUri).collect(Collectors.toMap(
                    x -> x.get(0) + "!" + x.get(1) + "$" + x.get(2),
                    y -> List.of(y.get(3)),
                    (x, y) -> {
                        var z = new ArrayList<String>();
                        z.addAll(x);
                        z.addAll(y);
                        return z;
                    }));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        LOG.info("Parsing and grouping is done: {}ms", System.currentTimeMillis() - start);
        var metadataMap = new HashMap<String, JSONObject>(fullFastenUris.size());
        LOG.info("Starting retrieving data from the database");
        var time = System.currentTimeMillis();
        for (var artifact : packageVersionUris.keySet()) {
            var forge = artifact.split("!")[0];
            var forgelessArtifact = Arrays.stream(artifact.split("!")).skip(1).collect(Collectors.joining("!"));
            var packageName = forgelessArtifact.split("\\$")[0];
            var version = forgelessArtifact.split("\\$")[1];
            var partialUris = packageVersionUris.get(artifact);
            LOG.info("Sending database request to retrieve metadata for {} callables of {}:{}", partialUris.size(), packageName, version);
            start = System.currentTimeMillis();
            var urisMetadata = KnowledgeBaseConnector.kbDao.getCallablesMetadataByUri(forge, packageName, version, partialUris);
            LOG.info("Database query is complete: {}ms", System.currentTimeMillis() - start);
            if (urisMetadata != null) {
                metadataMap.putAll(urisMetadata);
            }
        }
        LOG.info("All data is retrieved. In total data retrieval took {}ms", System.currentTimeMillis() - time);
        LOG.info("Now removing attributes which are not needed and putting everything into JSON");
        start = System.currentTimeMillis();
        var json = new JSONObject();
        for (var entry : metadataMap.entrySet()) {
            var neededMetadata = new JSONObject();
            if (!allAttributes) {
                for (var attribute : entry.getValue().keySet()) {
                    if (attributes.contains(attribute)) {
                        neededMetadata.put(attribute, entry.getValue().get(attribute));
                    }
                }
            } else {
                neededMetadata = entry.getValue();
            }
            json.put(entry.getKey(), neededMetadata);
        }
        var result = json.toString();
        LOG.info("Done: {}ms. Sending response", System.currentTimeMillis() - start);
        LOG.info("In total everything took {}ms", System.currentTimeMillis() - total);
        return Responses.ok(result);
    }

    @PostMapping(value = "/__INTERNAL__/ingest/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> batchIngestArtifacts(@RequestBody String jsonArtifacts) {
        JSONArray jsonArrayArtifacts = new JSONArray(jsonArtifacts);
        var artifacts = new ArrayList<LazyIngestionProvider.IngestedArtifact>();
        for (int i = 0; i < jsonArrayArtifacts.length(); i++) {
            var json = jsonArrayArtifacts.getJSONObject(i);
            artifacts.add(new LazyIngestionProvider.IngestedArtifact(
                    json.getString("groupId") + Constants.mvnCoordinateSeparator + json.getString("artifactId"),
                    json.getString("version"),
                    json.optString("artifactRepository", null),
                    json.optLong("date", -1)
            ));
        }
        try {
            ingestion.batchIngestArtifacts(artifacts);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return Responses.ok("Ingested successfully");
    }
}