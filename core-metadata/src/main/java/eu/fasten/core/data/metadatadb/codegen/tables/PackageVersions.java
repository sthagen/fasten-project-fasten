/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Indexes;
import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.PackageVersionsRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.16.21"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PackageVersions extends TableImpl<PackageVersionsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.package_versions</code>
     */
    public static final PackageVersions PACKAGE_VERSIONS = new PackageVersions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PackageVersionsRecord> getRecordType() {
        return PackageVersionsRecord.class;
    }

    /**
     * The column <code>public.package_versions.id</code>.
     */
    public final TableField<PackageVersionsRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.package_versions.package_id</code>.
     */
    public final TableField<PackageVersionsRecord, Long> PACKAGE_ID = createField(DSL.name("package_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.package_versions.version</code>.
     */
    public final TableField<PackageVersionsRecord, String> VERSION = createField(DSL.name("version"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.package_versions.cg_generator</code>.
     */
    public final TableField<PackageVersionsRecord, String> CG_GENERATOR = createField(DSL.name("cg_generator"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.package_versions.artifact_repository_id</code>.
     */
    public final TableField<PackageVersionsRecord, Long> ARTIFACT_REPOSITORY_ID = createField(DSL.name("artifact_repository_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.package_versions.architecture</code>.
     */
    public final TableField<PackageVersionsRecord, String> ARCHITECTURE = createField(DSL.name("architecture"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.package_versions.created_at</code>.
     */
    public final TableField<PackageVersionsRecord, Timestamp> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.package_versions.metadata</code>.
     */
    public final TableField<PackageVersionsRecord, JSONB> METADATA = createField(DSL.name("metadata"), SQLDataType.JSONB, this, "");

    private PackageVersions(Name alias, Table<PackageVersionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private PackageVersions(Name alias, Table<PackageVersionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.package_versions</code> table reference
     */
    public PackageVersions(String alias) {
        this(DSL.name(alias), PACKAGE_VERSIONS);
    }

    /**
     * Create an aliased <code>public.package_versions</code> table reference
     */
    public PackageVersions(Name alias) {
        this(alias, PACKAGE_VERSIONS);
    }

    /**
     * Create a <code>public.package_versions</code> table reference
     */
    public PackageVersions() {
        this(DSL.name("package_versions"), null);
    }

    public <O extends Record> PackageVersions(Table<O> child, ForeignKey<O, PackageVersionsRecord> key) {
        super(child, key, PACKAGE_VERSIONS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.PACKAGE_VERSIONS_PACKAGE_ID);
    }

    @Override
    public Identity<PackageVersionsRecord, Long> getIdentity() {
        return (Identity<PackageVersionsRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<PackageVersionsRecord> getPrimaryKey() {
        return Keys.PACKAGE_VERSIONS_PKEY;
    }

    @Override
    public List<UniqueKey<PackageVersionsRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.UNIQUE_PACKAGE_VERSION_GENERATOR);
    }

    @Override
    public List<ForeignKey<PackageVersionsRecord, ?>> getReferences() {
        return Arrays.asList(Keys.PACKAGE_VERSIONS__PACKAGE_VERSIONS_PACKAGE_ID_FKEY, Keys.PACKAGE_VERSIONS__PACKAGE_VERSIONS_ARTIFACT_REPOSITORY_ID_FKEY);
    }

    private transient Packages _packages;
    private transient ArtifactRepositories _artifactRepositories;

    /**
     * Get the implicit join path to the <code>public.packages</code> table.
     */
    public Packages packages() {
        if (_packages == null)
            _packages = new Packages(this, Keys.PACKAGE_VERSIONS__PACKAGE_VERSIONS_PACKAGE_ID_FKEY);

        return _packages;
    }

    /**
     * Get the implicit join path to the
     * <code>public.artifact_repositories</code> table.
     */
    public ArtifactRepositories artifactRepositories() {
        if (_artifactRepositories == null)
            _artifactRepositories = new ArtifactRepositories(this, Keys.PACKAGE_VERSIONS__PACKAGE_VERSIONS_ARTIFACT_REPOSITORY_ID_FKEY);

        return _artifactRepositories;
    }

    @Override
    public PackageVersions as(String alias) {
        return new PackageVersions(DSL.name(alias), this);
    }

    @Override
    public PackageVersions as(Name alias) {
        return new PackageVersions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PackageVersions rename(String name) {
        return new PackageVersions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PackageVersions rename(Name name) {
        return new PackageVersions(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, String, String, Long, String, Timestamp, JSONB> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
