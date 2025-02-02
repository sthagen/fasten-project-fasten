/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.ArtifactRepositoriesRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
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
public class ArtifactRepositories extends TableImpl<ArtifactRepositoriesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.artifact_repositories</code>
     */
    public static final ArtifactRepositories ARTIFACT_REPOSITORIES = new ArtifactRepositories();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ArtifactRepositoriesRecord> getRecordType() {
        return ArtifactRepositoriesRecord.class;
    }

    /**
     * The column <code>public.artifact_repositories.id</code>.
     */
    public final TableField<ArtifactRepositoriesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.artifact_repositories.repository_base_url</code>.
     */
    public final TableField<ArtifactRepositoriesRecord, String> REPOSITORY_BASE_URL = createField(DSL.name("repository_base_url"), SQLDataType.CLOB.nullable(false), this, "");

    private ArtifactRepositories(Name alias, Table<ArtifactRepositoriesRecord> aliased) {
        this(alias, aliased, null);
    }

    private ArtifactRepositories(Name alias, Table<ArtifactRepositoriesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.artifact_repositories</code> table
     * reference
     */
    public ArtifactRepositories(String alias) {
        this(DSL.name(alias), ARTIFACT_REPOSITORIES);
    }

    /**
     * Create an aliased <code>public.artifact_repositories</code> table
     * reference
     */
    public ArtifactRepositories(Name alias) {
        this(alias, ARTIFACT_REPOSITORIES);
    }

    /**
     * Create a <code>public.artifact_repositories</code> table reference
     */
    public ArtifactRepositories() {
        this(DSL.name("artifact_repositories"), null);
    }

    public <O extends Record> ArtifactRepositories(Table<O> child, ForeignKey<O, ArtifactRepositoriesRecord> key) {
        super(child, key, ARTIFACT_REPOSITORIES);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<ArtifactRepositoriesRecord, Long> getIdentity() {
        return (Identity<ArtifactRepositoriesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ArtifactRepositoriesRecord> getPrimaryKey() {
        return Keys.ARTIFACT_REPOSITORIES_PKEY;
    }

    @Override
    public List<UniqueKey<ArtifactRepositoriesRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.UNIQUE_ARTIFACT_REPOSITORIES);
    }

    @Override
    public ArtifactRepositories as(String alias) {
        return new ArtifactRepositories(DSL.name(alias), this);
    }

    @Override
    public ArtifactRepositories as(Name alias) {
        return new ArtifactRepositories(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ArtifactRepositories rename(String name) {
        return new ArtifactRepositories(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ArtifactRepositories rename(Name name) {
        return new ArtifactRepositories(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
