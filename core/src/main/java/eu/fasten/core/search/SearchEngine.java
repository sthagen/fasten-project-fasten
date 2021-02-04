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

package eu.fasten.core.search;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.function.LongPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.conf.ParseUnknownFunctions;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.UnflaggedOption;

import eu.fasten.core.data.DirectedGraph;
import eu.fasten.core.data.FastenJavaURI;
import eu.fasten.core.data.FastenURI;
import eu.fasten.core.data.graphdb.RocksDao;
import eu.fasten.core.data.metadatadb.codegen.tables.Callables;
import eu.fasten.core.data.metadatadb.codegen.tables.Modules;
import eu.fasten.core.data.metadatadb.codegen.tables.PackageVersions;
import eu.fasten.core.data.metadatadb.codegen.tables.Packages;
import eu.fasten.core.dbconnectors.PostgresConnector;
import eu.fasten.core.maven.GraphMavenResolver;
import eu.fasten.core.maven.data.Revision;
import eu.fasten.core.merge.DatabaseMerger;
import eu.fasten.core.search.predicate.CachingPredicateFactory;
import eu.fasten.core.search.predicate.PredicateFactory;
import eu.fasten.core.search.predicate.PredicateFactory.MetadataSource;
import it.unimi.dsi.bits.Fast;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

/**
 * A class offering searching capabilities over the FASTEN knowledge base.
 *
 * <p>
 * Instances of this class access the metadata Postgres database and the RocksDB database of
 * revision call graphs. Users can interrogate the engine by providing an entry point (e.g., a
 * callable) and a {@link LongPredicate} that will be used to filter the results. For more
 * documentation on the available filters, see {@link CachingPredicateFactory}.
 */

public class SearchEngine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngine.class);

	private static final int DEFAULT_LIMIT = 10;

	/** The regular expression for commands. */
	private static Pattern COMMAND_REGEXP = Pattern.compile("\\$\\s*(.*)\\s*");


	public final static class Result {
		public long gid;
		public double score;

		public Result() {
		}

		public Result(final long gid, final double score) {
			this.gid = gid;
			this.score = score;
		}

 		@Override
		public String toString() {
			return gid + " (" + score + ")";
		}

		@Override
		public int hashCode() {
			return (int)HashCommon.mix(gid);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final Result other = (Result)obj;
			if (gid != other.gid) return false;
			return true;
		}
	}

	/** The handle to the Postgres metadata database. */
	private final DSLContext context;
	/** The handle to the RocksDB DAO. */
	private final RocksDao rocksDao;
	/** The resolver. */
	private final GraphMavenResolver resolver;
	/** The predicate factory to be used to create predicates for this search engine. */
	private final PredicateFactory predicateFactory;

	/** The maximum number of results that should be printed. */
	private int limit = DEFAULT_LIMIT;
	/** The filters whose conjunction will be applied by default when executing a query, unless otherwise
	 *  specified (compare, e.g., {@link #fromCallable(long)} and {@link #fromCallable(long, LongPredicate)}). */
	private final ObjectArrayList<LongPredicate> predicateFilters = new ObjectArrayList<>();

	/** Time spent during resolution (dependency and dependents). */
	private long resolveTime;
	/** Time spent stitching graphs (mergeWithCHA()). */
	private long stitchingTime;
	/** Time spent during {@linkplain #bfs visits}. */
	private long visitTime;
	/** Maximum number of dependents used by {@link #to}. */
	private long maxDependents = Long.MAX_VALUE;
	/** Throwables thrown by mergeWithCHA(). */
	private List<Throwable> throwables = new ArrayList<>();

	/**
	 * Creates a new search engine using a given JDBC URI, database name and path to RocksDB.
	 *
	 * @implNote This method creates a context and DAO using the given parameters and delegates to
	 *           {@link #SearchEngine(DSLContext, RocksDao)}.
	 *
	 * @param jdbcURI the JDBC URI.
	 * @param database the database name.
	 * @param rocksDb the path to the RocksDB database of revision call graphs.
	 * @param resolverGraph the path to a serialized resolver graph (will be created if it does not
	 *            exist).
	 * @throws Exception
	 */
	public SearchEngine(final String jdbcURI, final String database, final String rocksDb, final String resolverGraph) throws Exception {
		this(PostgresConnector.getDSLContext(jdbcURI, database), new RocksDao(rocksDb, true), resolverGraph);
	}

	/**
	 * Creates a new search engine using a given {@link DSLContext} and {@link RocksDao}.
	 *
	 * @param context the DSL context.
	 * @param rocksDao the RocksDB DAO.
	 * @param resolver a resolver.
	 * @param resolverGraph the path to a serialized resolver graph (will be created if it does not
	 *            exist).
	 * @throws Exception
	 */

	public SearchEngine(final DSLContext context, final RocksDao rocksDao, final String resolverGraph) throws Exception {
		this.context = context;
		this.rocksDao = rocksDao;
		resolver = new GraphMavenResolver();
		resolver.buildDependencyGraph(null, resolverGraph);
		this.predicateFactory = new CachingPredicateFactory(context);
	}

	public long gid2Rev(final long gid) {
		return context.select(PackageVersions.PACKAGE_VERSIONS.ID).from(PackageVersions.PACKAGE_VERSIONS).
				join(Modules.MODULES).on(Modules.MODULES.PACKAGE_VERSION_ID.eq(PackageVersions.PACKAGE_VERSIONS.ID)).
				join(Callables.CALLABLES).on(Callables.CALLABLES.MODULE_ID.eq(Modules.MODULES.ID)).where(Callables.CALLABLES.ID.eq(Long.valueOf(gid))).fetchOne().component1().longValue();
	}

	/** Executes a given command.
	 *
	 * @param command the command.
	 */
	private void executeCommand(final String command) {
		final String[] commandAndArgs = command.split("\\s"); // Split command on whitespace
		final String help =
				"\t$help                           Help on commands\n" +
				"\t$clear                          Clear filters\n" +
				"\t$f pmatches <REGEXP>            Add filter: package (a.k.a. product) matches <REGEXP>\n" +
				"\t$f vmatches <REGEXP>            Add filter: version matches <REGEXP>\n" +
				"\t$f xmatches <REGEXP>            Add filter: path (namespace + entity) matches <REGEXP>\n" +
				"\t$f cmd <KEY> [<REGEXP>]         Add filter: callable metadata contains key <KEY> (satisfying <REGEXP>)\n" +
				"\t$f mmd <KEY> [<REGEXP>]         Add filter: module metadata contains key <KEY> (satisfying <REGEXP>)\n" +
				"\t$f pmd <KEY> [<REGEXP>]         Add filter: package+version metadata contains key <KEY> (satisfying <REGEXP>)\n" +
				"\t$f cmdjp <JP> <REGEXP>          Add filter: callable metadata queried with the JSONPointer <JP> has a value satisfying <REGEXP>\n" +
				"\t$f mmdjp <JP> <REGEXP>          Add filter: module metadata queried with the JSONPointer <JP> has a value satisfying <REGEXP>\n" +
				"\t$f pmdjp <JP> <REGEXP>          Add filter: package+version metadata queried with the JSONPointer <JP> has a value satisfying <REGEXP>\n" +
				"\t$or                             The last two filters are substituted by their disjunction (or)\n" +
				"\t$and                            The last two filters are substituted by their conjunction (and)\n" +
				"\t$not                            The last filter is substituted by its negation (not)\n" +
				"\t$limit <LIMIT>                  Print at most <LIMIT> results (-1 for infinity)\n" +
				"\t$maxDependents <LIMIT>          Maximum number of dependents considered in coreachable query resolution (-1 for infinity)" +
				"\t±<URI>                          Find reachable (+) or coreachable (-) callables from the given callable <URI> satisfying all filters\n" +
				"";
		try {
			switch(commandAndArgs[0].toLowerCase()) {

			case "help":
				System.err.println(help);
				break;

			case "limit":
				limit = Integer.parseInt(commandAndArgs[1]);
				if (limit < 0) limit = Integer.MAX_VALUE;
				break;
			
			case "maxDependents":
				maxDependents = Long.parseLong(commandAndArgs[1]);
				if (maxDependents < 0) maxDependents = Long.MAX_VALUE;
				break;

			case "clear":
				predicateFilters.clear();
				break;

			case "f":
				LongPredicate predicate = null;
				Pattern regExp;
				MetadataSource mds;
				switch(commandAndArgs[1].toLowerCase()) {
				case "pmatches":
					regExp = Pattern.compile(commandAndArgs[2]);
					predicate = predicateFactory.fastenURIMatches(uri -> uri.getProduct() != null && regExp.matcher(uri.getProduct()).matches());
					break;
				case "vmatches":
					regExp = Pattern.compile(commandAndArgs[2]);
					predicate = predicateFactory.fastenURIMatches(uri -> uri.getVersion() != null && regExp.matcher(uri.getVersion()).matches());
					break;
				case "xmatches":
					regExp = Pattern.compile(commandAndArgs[2]);
					predicate = predicateFactory.fastenURIMatches(uri -> uri.getPath() != null && regExp.matcher(uri.getPath()).matches());
					break;
				case "cmd": case "mmd": case "pmd":
					final String key = commandAndArgs[2];
					mds = null;
					switch (commandAndArgs[1].toLowerCase().charAt(0)) {
					case 'c':  mds = MetadataSource.CALLABLE; break;
					case 'm':  mds = MetadataSource.MODULE; break;
					case 'p':  mds = MetadataSource.PACKAGE_VERSION; break;
					default: throw new RuntimeException("Cannot happen");
					}
				 	if (commandAndArgs.length == 3) predicate = predicateFactory.metadataContains(mds, key);
				 	else {
				 		regExp = Pattern.compile(commandAndArgs[3]);
				 		predicate = predicateFactory.metadataContains(mds, key, regExp.asPredicate());
				 	}
				 	break;
				case "cmdjp": case "mmdjp": case "pmdjp":
					final String jsonPointer = commandAndArgs[2];
					mds = null;
					switch (commandAndArgs[1].toLowerCase().charAt(0)) {
					case 'c':  mds = MetadataSource.CALLABLE; break;
					case 'm':  mds = MetadataSource.MODULE; break;
					case 'p':  mds = MetadataSource.PACKAGE_VERSION; break;
					default: throw new RuntimeException("Cannot happen");
					}
				 	regExp = Pattern.compile(commandAndArgs[3]);
				 	predicate = predicateFactory.metadataQueryJSONPointer(mds, jsonPointer, regExp.asPredicate());
				 	break;
				default:
					throw new RuntimeException("Unknown type of predicate " + commandAndArgs[1]);
				}
				if (predicate != null) predicateFilters.push(predicate);
				break;

			case "and": case "or":
				if (predicateFilters.size() < 2) throw new RuntimeException("At least two predicates must be present");
				if ("and".equals(commandAndArgs[0].toLowerCase()))
					predicateFilters.push(predicateFilters.pop().and(predicateFilters.pop()));
				else
					predicateFilters.push(predicateFilters.pop().or(predicateFilters.pop()));
				break;

			case "not":
				if (predicateFilters.size() < 1) throw new RuntimeException("At least one predicates must be present");
				predicateFilters.push(predicateFilters.pop().negate());
				break;

			default: 
				System.err.println("Unknown command " + command);
			}
			
		} catch (final RuntimeException e) {
			System.err.println("Exception while executing command " + command);
			e.printStackTrace(System.err);
			System.err.println(help);
		}
	}

	/**
	 * Performs a breadth-first visit of the given graph, starting from the provided seed, using the
	 * provided predicate and returning a collection of ranked {@link Result} instances satisfying the
	 * provided filter.
	 *
	 * @param graph a {@link DirectedGraph}.
	 * @param forward if true, the visit follows arcs; if false, the visit follows arcs backwards.
	 * @param seed an initial seed; may contain GIDs that do not appear in the graph, which will be
	 *            ignored.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	protected static void bfs(final DirectedGraph graph, final boolean forward, final LongCollection seed, final LongPredicate filter, final Collection<Result> results) {
		final LongArrayFIFOQueue queue = new LongArrayFIFOQueue(seed.size());
		seed.forEach(x -> queue.enqueue(x)); // Load initial state
		final LongOpenHashSet seen = new LongOpenHashSet();
		int d = -1;
		long sentinel = queue.firstLong();
		final Result probe = new Result();
		final LongSet nodes = graph.nodes();

		while (!queue.isEmpty()) {
			final long gid = queue.dequeueLong();
			if (gid == sentinel) {
				d++;
				sentinel = -1;
			}

			if (!nodes.contains(gid)) continue; // We accept arbitrary seed sets

			if (!seed.contains(gid) && filter.test(gid)) {
				probe.gid = gid;
				if (!results.contains(probe)) results.add(new Result(gid, (graph.outdegree(gid) + graph.indegree(gid)) / Fast.log2(d + 2)));
			}

			final LongIterator iterator = forward ? graph.successors(gid).iterator() : graph.predecessors(gid).iterator();

			while (iterator.hasNext()) {
				final long x = iterator.nextLong();
				if (seen.add(x)) {
					if (sentinel == -1) sentinel = x;
					queue.enqueue(x);
				}
			}
		}
	}

	/**
	 * Computes the callables satisfying the given predicate and reachable from the provided callable,
	 * and returns them in a ranked list. They will be filtered by the conjunction of
	 * {@link #predicateFilters}.
	 *
	 * @param gid the global ID of a callable.
	 * @return a list of {@linkplain Result results}.
	 */
	private List<Result> fromCallable(final long gid) throws RocksDBException {
		return fromCallable(gid, predicateFilters.stream().reduce(x -> true, LongPredicate::and));
	}

	/**
	 * Computes the callables satisfying the given predicate and reachable from the provided callable,
	 * and returns them in a ranked list.
	 *
	 * @param gid the global ID of a callable.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> fromCallable(final long gid, final LongPredicate filter) throws RocksDBException {
		return from(gid2Rev(gid), LongSets.singleton(gid), filter);
	}

	/**
	 * Computes the callables satisfying the given predicate and reachable from the provided revision,
	 * and returns them in a ranked list. They will be filtered by the conjunction of
	 * {@link #predicateFilters}.
	 *
	 * @param revisionUri a FASTEN URI specifying a revision.
	 * @return a list of {@linkplain Result results}.
	 */
	private List<Result> fromRevision(final FastenURI revisionUri) throws RocksDBException {
		return fromRevision(revisionUri, predicateFilters.stream().reduce(x -> true, LongPredicate::and));
	}

	/**
	 * Computes the callables satisfying the given predicate and reachable from the provided revision,
	 * and returns them in a ranked list.
	 *
	 * @param revisionUri a FASTEN URI specifying a revision.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> fromRevision(final FastenURI revisionUri, final LongPredicate filter) throws RocksDBException {
		// Fetch revision id
		final long rev = Util.getRevisionId(revisionUri, context);
		if (rev == -1) throw new IllegalArgumentException("Unknown revision " + revisionUri);
		return from(rev, null, filter);
	}

	/**
	 * Computes the callables satisfying the given predicate and reachable from the provided seed, in
	 * the stitched graph associated with the provided revision, and returns them in a ranked list.
	 *
	 * @param rev the database id of a revision.
	 * @param seed a collection of GIDs that will be used as a seed for the visit; if {@code null}, the
	 *            entire set of GIDs of the specified revision will be used as a seed.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> from(final long rev, LongCollection seed, final LongPredicate filter) throws RocksDBException {
		final var graph = rocksDao.getGraphData(rev);
		if (graph == null) throw new NoSuchElementException("Revision associated with callable missing from the graph database");
		if (seed == null) seed = graph.nodes();

		LOGGER.debug("Revision call graph has " + graph.numNodes() + " nodes");

		final Record2<String, String> record = context.select(Packages.PACKAGES.PACKAGE_NAME, PackageVersions.PACKAGE_VERSIONS.VERSION).from(PackageVersions.PACKAGE_VERSIONS).join(Packages.PACKAGES).on(PackageVersions.PACKAGE_VERSIONS.PACKAGE_ID.eq(Packages.PACKAGES.ID)).where(PackageVersions.PACKAGE_VERSIONS.ID.eq(rev)).fetchOne();
		final String[] a = record.component1().split(":");
		final String groupId = a[0];
		final String artifactId = a[1];
		final String version = record.component2();
		resolveTime -= System.nanoTime();
		final Set<Revision> dependencySet = resolver.resolveDependencies(groupId, artifactId, version, -1, context, true);
		resolveTime += System.nanoTime();

		LOGGER.debug("Found " + dependencySet.size() + " dependencies");

		stitchingTime -= System.nanoTime();
		final DatabaseMerger dm = new DatabaseMerger(LongOpenHashSet.toSet(dependencySet.stream().mapToLong(x -> x.id)), context, rocksDao);
		final var stitchedGraph = dm.mergeWithCHA(groupId + ":" + artifactId + ":" + version);
		stitchingTime += System.nanoTime();

		LOGGER.debug("Stiched graph has " + stitchedGraph.numNodes() + " nodes");

		final ObjectLinkedOpenHashSet<Result> results = new ObjectLinkedOpenHashSet<>();

		visitTime -= System.nanoTime();
		bfs(stitchedGraph, true, seed, filter, results);
		visitTime += System.nanoTime();

		LOGGER.debug("Found " + results.size() + " reachable nodes");

		final Result[] array = results.toArray(new Result[0]);
		Arrays.sort(array, (x, y) -> Double.compare(y.score, x.score));
		return Arrays.asList(array);
	}

	/**
	 * Computes the callables satisfying the given predicate and coreachable from the provided callable,
	 * and returns them in a ranked list. They will be filtered by the conjuction of
	 * {@link #predicateFilters}.
	 *
	 * @param gid the global ID of a callable.
	 * @return a list of {@linkplain Result results}.
	 */
	private List<Result> toCallable(final long gid) throws RocksDBException {
		return toCallable(gid, predicateFilters.stream().reduce(x -> true, LongPredicate::and));
	}

	/**
	 * Computes the callables satisfying the given predicate and coreachable from the provided callable,
	 * and returns them in a ranked list. They will be filtered by the conjuction of
	 * {@link #predicateFilters}.
	 *
	 * @param gid the global ID of a callable.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> toCallable(final long gid, final LongPredicate filter) throws RocksDBException {
		return to(gid2Rev(gid), LongSets.singleton(gid), filter);
	}

	/**
	 * Computes the callables satisfying the given predicate and coreachable from the provided revision,
	 * and returns them in a ranked list.
	 *
	 * @param revisionUri a FASTEN URI specifying a revision.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> toRevision(final FastenURI revisionUri) throws RocksDBException {
		return toRevision(revisionUri, predicateFilters.stream().reduce(x -> true, LongPredicate::and));
	}

	/**
	 * Computes the callables satisfying the given predicate and coreachable from the provided revision,
	 * and returns them in a ranked list.
	 *
	 * @param revisionUri a FASTEN URI specifying a revision.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> toRevision(final FastenURI revisionUri, final LongPredicate filter) throws RocksDBException {
		// Fetch revision id
		final long rev = Util.getRevisionId(revisionUri, context);
		if (rev == -1) throw new IllegalArgumentException("Unknown revision " + revisionUri);
		return to(rev, null, filter);
	}

	/**
	 * Computes the callables satisfying the given predicate and coreachable from the provided seed, in
	 * the stitched graph associated with the provided revision, and returns them in a ranked list.
	 *
	 * @param rev the database id of a revision.
	 * @param seed a collection of GIDs that will be used as a seed for the visit; if {@code null}, the
	 *            entire set of GIDs of the specified revision will be used as a seed.
	 * @param filter a {@link LongPredicate} that will be used to filter callables.
	 * @return a list of {@linkplain Result results}.
	 */
	public List<Result> to(final long rev, LongCollection seed, final LongPredicate filter) throws RocksDBException {
		throwables.clear();
		final var graph = rocksDao.getGraphData(rev);
		if (graph == null) throw new NoSuchElementException("Revision associated with callable missing from the graph database");
		if (seed == null) seed = graph.nodes();

		Record2<String, String> record = context.select(Packages.PACKAGES.PACKAGE_NAME, PackageVersions.PACKAGE_VERSIONS.VERSION).from(PackageVersions.PACKAGE_VERSIONS).join(Packages.PACKAGES).on(PackageVersions.PACKAGE_VERSIONS.PACKAGE_ID.eq(Packages.PACKAGES.ID)).where(PackageVersions.PACKAGE_VERSIONS.ID.eq(rev)).fetchOne();
		String[] a = record.component1().split(":");
		String groupId = a[0];
		String artifactId = a[1];
		String version = record.component2();
		resolveTime -= System.nanoTime();
		final Set<Revision> s = resolver.resolveDependents(groupId, artifactId, version, -1, true);
		final Set<Revision> dependentSet = new ObjectOpenHashSet<>();

		// Temporary reduction in size to circumvent mergeWithCHA() crashes
		long m = 0;
		for(var r: s) {
			if (m++ == maxDependents) break;
			dependentSet.add(r);
		}

		resolveTime += System.nanoTime();

		LOGGER.debug("Found " + dependentSet.size() + " dependents");

		final LongOpenHashSet dependentIds = LongOpenHashSet.toSet(dependentSet.stream().mapToLong(x -> x.id));
		dependentIds.add(rev);
		LOGGER.debug("Found " + dependentIds.size() + " dependents");

		final ObjectLinkedOpenHashSet<Result> results = new ObjectLinkedOpenHashSet<>();

		long trueDependents = 0;

		for (final var iterator = dependentIds.iterator(); iterator.hasNext();) {
			final long dependentId = iterator.nextLong();

			record = context.select(Packages.PACKAGES.PACKAGE_NAME, PackageVersions.PACKAGE_VERSIONS.VERSION).from(PackageVersions.PACKAGE_VERSIONS).join(Packages.PACKAGES).on(PackageVersions.PACKAGE_VERSIONS.PACKAGE_ID.eq(Packages.PACKAGES.ID)).where(PackageVersions.PACKAGE_VERSIONS.ID.eq(dependentId)).fetchOne();

			a = record.component1().split(":");
			groupId = a[0];
			artifactId = a[1];
			version = record.component2();

			LOGGER.debug("Analyzing dependent " + groupId + ":" + artifactId + ":" + version);

			resolveTime -= System.nanoTime();
			final Set<Revision> dependencySet = resolver.resolveDependencies(groupId, artifactId, version, -1, context, true);
			resolveTime += System.nanoTime();

			LOGGER.debug("Dependent has " + graph.numNodes() + " nodes");

			LOGGER.debug("Found " + dependencySet.size() + " dependencies");

			final LongOpenHashSet dependencyIds = LongOpenHashSet.toSet(dependencySet.stream().mapToLong(x -> x.id));
			if (dependentId != rev && !dependencyIds.contains(rev)) {
				LOGGER.debug("False dependent");
				continue; // We cannot possibly reach the callable
			}

			trueDependents++;

			stitchingTime -= System.nanoTime();
			final DatabaseMerger dm = new DatabaseMerger(dependencyIds, context, rocksDao);

			DirectedGraph stitchedGraph = null;
			try {
				stitchedGraph = dm.mergeWithCHA(groupId + ":" + artifactId + ":" + version);
			} catch(Throwable t) {
				throwables.add(t);
				LOGGER.error("mergeWithCHA threw an exception", t);
			}
			stitchingTime += System.nanoTime();

			if (stitchedGraph == null) continue;

			LOGGER.debug("Stiched graph has " + stitchedGraph.numNodes() + " nodes");
			final int sizeBefore = results.size();

			visitTime -= System.nanoTime();
			bfs(stitchedGraph, false, seed, filter, results);
			visitTime += System.nanoTime();

			LOGGER.debug("Found " + (results.size() - sizeBefore) + " coreachable nodes");
		}

		LOGGER.debug("Found " + trueDependents + " true dependents");
		LOGGER.debug("Found overall " + results.size() + " coreachable nodes");

		final Result[] array = results.toArray(new Result[0]);
		Arrays.sort(array, (x, y) -> Double.compare(y.score, x.score));
		return Arrays.asList(array);
	}



	// dbContext=PostgresConnector.getDSLContext("jdbc:postgresql://monster:5432/fasten_java","fastenro");rocksDao=new
	// eu.fasten.core.data.graphdb.RocksDao("/home/vigna/graphdb/",true);

	@SuppressWarnings("boxing")
	public static void main(final String args[]) throws Exception {
		final SimpleJSAP jsap = new SimpleJSAP(SearchEngine.class.getName(), "Creates an instance of SearchEngine and answers queries from the command line (rlwrap recommended).", new Parameter[] {
				new UnflaggedOption("jdbcURI", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "The JDBC URI."),
				new UnflaggedOption("database", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The database name."),
				new UnflaggedOption("rocksDb", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The path to the RocksDB database of revision call graphs."),
				new UnflaggedOption("resolverGraph", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The path to a resolver graph (will be created if it does not exist)."), });

		final JSAPResult jsapResult = jsap.parse(args);
		if (jsap.messagePrinted()) System.exit(1);

		final String jdbcURI = jsapResult.getString("jdbcURI");
		final String database = jsapResult.getString("database");
		final String rocksDb = jsapResult.getString("rocksDB");
		final String resolverGraph = jsapResult.getString("resolverGraph");

		/* WARNING
		 *
		 * As of JDK 11.0.10, replacing the constant string below with the parameter "rocksDb" causes
		 * a JVM crash with the following stack trace:
		 *
		 * V  [libjvm.so+0x5ad861]  AccessInternal::PostRuntimeDispatch<G1BarrierSet::AccessBarrier<1097844ul, G1BarrierSet>, (AccessInternal::BarrierType)2, 1097844ul>::oop_access_barrier(void*)+0x1
		 * C  [librocksdbjni5446245757426305293.so+0x22aefc]  rocksdb_open_helper(JNIEnv_*, long, _jstring*, _jobjectArray*, _jlongArray*, std::function<rocksdb::Status (rocksdb::DBOptions const&, std::string const&, std::vector<rocksdb::ColumnFamilyDescriptor, std::allocator<rocksdb::ColumnFamilyDescriptor> > const&, std::vector<rocksdb::ColumnFamilyHandle*, std::allocator<rocksdb::ColumnFamilyHandle*> >*, rocksdb::DB**)>)+0x3c
		 * C  [librocksdbjni5446245757426305293.so+0x22b371]  Java_org_rocksdb_RocksDB_openROnly__JLjava_lang_String_2_3_3B_3JZ+0x41
		 * j  org.rocksdb.RocksDB.openROnly(JLjava/lang/String;[[B[JZ)[J+0
		 *
		 * The most likely explanation is some kind of aggressive early collection of the variable rocksDb by the G1
		 * collector which clashes with RocksDB's JNI usage of the variable.
		 */

		final SearchEngine searchEngine = new SearchEngine(jdbcURI, database, "/mnt/fasten/graphdb", resolverGraph);
		final DSLContext context = searchEngine.context;
		context.settings().withParseUnknownFunctions(ParseUnknownFunctions.IGNORE);

		@SuppressWarnings("resource")
		final Scanner scanner = new Scanner(System.in);
		for(;;) {
			System.out.print("[$help for help]>");
			System.out.flush();
			if (!scanner.hasNextLine()) break;
			String line = scanner.nextLine();
			if (line.length() == 0) continue;
			final Matcher matcher = COMMAND_REGEXP.matcher(line);
			if (matcher.matches()) {
				searchEngine.executeCommand(matcher.group(1));
				continue;
			}
			try {
				final char dir = line.charAt(0);
				if (dir != '+' && dir != '-') {
					if (dir != '#') System.err.println("First character must be '+', '-', or '#'");
					continue;
				}
				line = line.substring(1);
				final FastenJavaURI uri = FastenJavaURI.create(line);

				final long start = -System.nanoTime();
				searchEngine.stitchingTime = searchEngine.resolveTime = searchEngine.visitTime = 0;

				if (uri.getPath() == null) {
					final var r = dir == '+' ? searchEngine.fromRevision(uri) : searchEngine.toRevision(uri);
					for (int i = 0; i < Math.min(searchEngine.limit, r.size()); i++) System.out.println(r.get(i).gid + "\t" + Util.getCallableName(r.get(i).gid, context) + "\t" + r.get(i).score);
				} else {
					final long gid = Util.getCallableGID(uri, context);
					if (gid == -1) {
						System.err.println("Unknown URI " + uri);
						continue;
					}
					final var r = dir == '+' ? searchEngine.fromCallable(gid) : searchEngine.toCallable(gid);
					for (int i = 0; i < Math.min(searchEngine.limit, r.size()); i++) System.out.println(r.get(i).gid + "\t" + Util.getCallableName(r.get(i).gid, context) + "\t" + r.get(i).score);
				}

				for(var t: searchEngine.throwables) System.err.println(t);
				System.err.printf("\nTotal time: %.3fs Resolve time: %.3fs Stitching time: %.3fs Visit time %.3fs\n", (System.nanoTime() + start) * 1E-9, searchEngine.resolveTime * 1E-9, searchEngine.stitchingTime * 1E-9, searchEngine.visitTime * 1E-9);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}