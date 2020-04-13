/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Indexes;
import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.BinaryModulesRecord;

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
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BinaryModules extends TableImpl<BinaryModulesRecord> {

    private static final long serialVersionUID = 760317736;

    /**
     * The reference instance of <code>public.binary_modules</code>
     */
    public static final BinaryModules BINARY_MODULES = new BinaryModules();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BinaryModulesRecord> getRecordType() {
        return BinaryModulesRecord.class;
    }

    /**
     * The column <code>public.binary_modules.id</code>.
     */
    public final TableField<BinaryModulesRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('binary_modules_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.binary_modules.package_version_id</code>.
     */
    public final TableField<BinaryModulesRecord, Long> PACKAGE_VERSION_ID = createField(DSL.name("package_version_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.binary_modules.name</code>.
     */
    public final TableField<BinaryModulesRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.binary_modules.created_at</code>.
     */
    public final TableField<BinaryModulesRecord, Timestamp> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>public.binary_modules.metadata</code>.
     */
    public final TableField<BinaryModulesRecord, JSONB> METADATA = createField(DSL.name("metadata"), org.jooq.impl.SQLDataType.JSONB, this, "");

    /**
     * Create a <code>public.binary_modules</code> table reference
     */
    public BinaryModules() {
        this(DSL.name("binary_modules"), null);
    }

    /**
     * Create an aliased <code>public.binary_modules</code> table reference
     */
    public BinaryModules(String alias) {
        this(DSL.name(alias), BINARY_MODULES);
    }

    /**
     * Create an aliased <code>public.binary_modules</code> table reference
     */
    public BinaryModules(Name alias) {
        this(alias, BINARY_MODULES);
    }

    private BinaryModules(Name alias, Table<BinaryModulesRecord> aliased) {
        this(alias, aliased, null);
    }

    private BinaryModules(Name alias, Table<BinaryModulesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> BinaryModules(Table<O> child, ForeignKey<O, BinaryModulesRecord> key) {
        super(child, key, BINARY_MODULES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BINARY_MODULES_COMPOUND_INDEX, Indexes.BINARY_MODULES_PKEY);
    }

    @Override
    public Identity<BinaryModulesRecord, Long> getIdentity() {
        return Keys.IDENTITY_BINARY_MODULES;
    }

    @Override
    public UniqueKey<BinaryModulesRecord> getPrimaryKey() {
        return Keys.BINARY_MODULES_PKEY;
    }

    @Override
    public List<UniqueKey<BinaryModulesRecord>> getKeys() {
        return Arrays.<UniqueKey<BinaryModulesRecord>>asList(Keys.BINARY_MODULES_PKEY);
    }

    @Override
    public List<ForeignKey<BinaryModulesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BinaryModulesRecord, ?>>asList(Keys.BINARY_MODULES__BINARY_MODULES_PACKAGE_VERSION_ID_FKEY);
    }

    public PackageVersions packageVersions() {
        return new PackageVersions(this, Keys.BINARY_MODULES__BINARY_MODULES_PACKAGE_VERSION_ID_FKEY);
    }

    @Override
    public BinaryModules as(String alias) {
        return new BinaryModules(DSL.name(alias), this);
    }

    @Override
    public BinaryModules as(Name alias) {
        return new BinaryModules(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BinaryModules rename(String name) {
        return new BinaryModules(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BinaryModules rename(Name name) {
        return new BinaryModules(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, Timestamp, JSONB> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
