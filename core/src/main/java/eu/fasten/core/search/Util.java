package eu.fasten.core.search;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.conf.Settings;

import eu.fasten.core.data.FastenURI;
import eu.fasten.core.data.metadatadb.codegen.tables.Callables;

/**
 * Miscellaneous utility methods.
 */

public class Util {

	private Util() {}

	/**
	 * Given a FASTEN URI and a database connection, returns the associated GID.
	 *
	 * @implSpec This method uses the SHA-based index.
	 *
	 * @implNote The provided {@link DSLContext} <strong>must</strong> be able to parse
	 *           {@linkplain Settings#withParseUnknownFunctions(org.jooq.conf.ParseUnknownFunctions)
	 *           unknown functions}.
	 *
	 * @param uri a FASTEN URI.
	 * @param context a database connection that is able to parse
	 *            {@linkplain Settings#withParseUnknownFunctions(org.jooq.conf.ParseUnknownFunctions)
	 *            unknown functions}.
	 * @return the GID of {@code uri}, if {@code uri} is in the database; &minus;1 otherwise.
	 */

	public static long getCallableGID(final FastenURI uri, final DSLContext context) throws SQLException {
		final String product = uri.getRawProduct();
		final String version = uri.getRawVersion();
		final String path = uri.getRawPath();
		final var parsingConnection = context.parsingConnection();
        final var statement = parsingConnection.createStatement();

		final String query = "select callables.id from callables " +
				"join modules on modules.id=callables.module_id " +
				"join package_versions on package_versions.id=modules.package_version_id " +
				"join packages on packages.id=package_versions.package_id " +
				"where packages.package_name='" + product.replace("'", "\\'") + 
				"' and package_versions.version='" + version.replace("'", "\\'") +
				"' and digest(fasten_uri, 'sha1'::text) = digest('" + path.replace("'", "\\'") + "', 'sha1'::text)";

		final java.sql.ResultSet result = statement.executeQuery(query);
		return result.next() ? -1 : result.getLong(1);
	}

	/** Returns the {@link FastenURI} of a given {@link Callables#CALLABLES#ID}.
	 * 
	 * @param callableGID the {@link Callables#CALLABLES#ID}.
	 * @return the corresponding {@link FastenURI}.
	 * @throws NoSuchElementException if the callableGID does not correspond to any element in the {@link Callables#CALLABLES} table.
	 */
	public static FastenURI getCallableName(final long callableGID, final DSLContext dbContext) {
		Record1<String> singleRow = dbContext.select(Callables.CALLABLES.FASTEN_URI).from(Callables.CALLABLES).where(Callables.CALLABLES.ID.eq(callableGID)).fetchOne();
		if (singleRow == null) throw new NoSuchElementException();
	    return FastenURI.create(singleRow.component1());
	}

}
