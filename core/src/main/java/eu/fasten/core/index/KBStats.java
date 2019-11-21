package eu.fasten.core.index;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.rocksdb.RocksDBException;

import com.google.common.math.StatsAccumulator;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.UnflaggedOption;

import eu.fasten.core.data.KnowledgeBase;
import eu.fasten.core.data.KnowledgeBase.CallGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;

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

public class KBStats {

	private static ImmutableGraph[] graph;
	private static Properties[] property;

	public static void main(final String[] args) throws JSAPException, ClassNotFoundException, RocksDBException, IOException {
		final SimpleJSAP jsap = new SimpleJSAP( Indexer.class.getName(),
				"Creates or updates a knowledge base (associated to a given database), indexing either a list of JSON files or a Kafka topic where JSON object are published",
				new Parameter[] {
						new UnflaggedOption("kb", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "The directory of the RocksDB instance containing the knowledge base." ),
		});

		final JSAPResult jsapResult = jsap.parse(args);
		if ( jsap.messagePrinted() ) return;

		final String kbDir = jsapResult.getString("kb");
		if (!new File(kbDir).exists()) throw new IllegalArgumentException("No such directory: " + kbDir);
		final KnowledgeBase kb = KnowledgeBase.getInstance(kbDir);

		final StatsAccumulator nodes = new StatsAccumulator();
		final StatsAccumulator arcs = new StatsAccumulator();
		final StatsAccumulator bitsPerLink = new StatsAccumulator();
		final StatsAccumulator bitsPerLinkt = new StatsAccumulator();
		for(final CallGraph callGraph: kb.callGraphs.values()) {
			graph = callGraph.graphs();
			nodes.add(graph[0].numNodes());
			arcs.add(graph[0].numArcs());
			property = callGraph.graphProperties();
			bitsPerLink.add(Double.parseDouble((String)(property[0].getProperty("bitsperlink"))));
			bitsPerLinkt.add(Double.parseDouble((String)(property[1].getProperty("bitsperlink"))));
		}

		kb.close();

		System.out.println(nodes.snapshot());
		System.out.println(arcs.snapshot());
		System.out.println(bitsPerLink.snapshot());
		System.out.println(bitsPerLinkt.snapshot());
	}

}
