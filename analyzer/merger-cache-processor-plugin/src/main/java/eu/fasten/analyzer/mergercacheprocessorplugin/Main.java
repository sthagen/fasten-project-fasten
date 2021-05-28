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
package eu.fasten.analyzer.mergercacheprocessorplugin;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.io.FileReader;

@CommandLine.Command(name = "MergerCacheProcessorPlugin")
public class Main implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @CommandLine.Option(names = {"-f", "--file"},
            paramLabel = "JSON_FILE",
            description = "Path to JSON file which contains product and its version")
    String jsonFile;

    @CommandLine.Option(names = {"-ur", "--kb-url"},
            paramLabel = "KB_URL",
            description = "The url of the knowledge base")
    String kbUrl;

    @CommandLine.Option(names = {"-us", "--kb-user"},
            paramLabel = "KB_USER",
            description = "The user of the knowledge base")
    String kbUser;

    @CommandLine.Option(names = {"-dg", "--depgraph-path"},
            paramLabel = "DEP_GRAPH_PATH",
            description = "The directory of the dependency graph")
    String depGraphPath;

    @CommandLine.Option(names = {"-gd", "--graphdb-path"},
            paramLabel = "GRAPH_DB_PATH",
            description = "The directory of the graph database")
    String graphDbPath;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        var cacheProcessorPlugin = new MergerCacheProcessorPlugin.MergerCacheProcessorExtension();
        cacheProcessorPlugin.loadGraphResolver(kbUrl, kbUser, depGraphPath, graphDbPath);
        final FileReader reader;
        try {
            reader = new FileReader(jsonFile);
        } catch (FileNotFoundException e) {
            logger.error("Could not find the JSON file at " + jsonFile, e);
            return;
        }
        final JSONObject input = new JSONObject(new JSONTokener(reader));
        cacheProcessorPlugin.consume(input.toString());
    }
}
