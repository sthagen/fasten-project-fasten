/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.PackagesRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
        "jOOQ version:3.14.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Packages extends TableImpl<PackagesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.packages</code>
     */
    public static final Packages PACKAGES = new Packages();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PackagesRecord> getRecordType() {
        return PackagesRecord.class;
    }

    /**
     * The column <code>public.packages.id</code>.
     */
    public final TableField<PackagesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.packages.package_name</code>.
     */
    public final TableField<PackagesRecord, String> PACKAGE_NAME = createField(DSL.name("package_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.packages.forge</code>.
     */
    public final TableField<PackagesRecord, String> FORGE = createField(DSL.name("forge"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.packages.project_name</code>.
     */
    public final TableField<PackagesRecord, String> PROJECT_NAME = createField(DSL.name("project_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.packages.repository</code>.
     */
    public final TableField<PackagesRecord, String> REPOSITORY = createField(DSL.name("repository"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.packages.created_at</code>.
     */
    public final TableField<PackagesRecord, Timestamp> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.TIMESTAMP(6), this, "");

    private Packages(Name alias, Table<PackagesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Packages(Name alias, Table<PackagesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.packages</code> table reference
     */
    public Packages(String alias) {
        this(DSL.name(alias), PACKAGES);
    }

    /**
     * Create an aliased <code>public.packages</code> table reference
     */
    public Packages(Name alias) {
        this(alias, PACKAGES);
    }

    /**
     * Create a <code>public.packages</code> table reference
     */
    public Packages() {
        this(DSL.name("packages"), null);
    }

    public <O extends Record> Packages(Table<O> child, ForeignKey<O, PackagesRecord> key) {
        super(child, key, PACKAGES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<PackagesRecord, Long> getIdentity() {
        return (Identity<PackagesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<PackagesRecord> getPrimaryKey() {
        return Keys.PACKAGES_PKEY;
    }

    @Override
    public List<UniqueKey<PackagesRecord>> getKeys() {
        return Arrays.<UniqueKey<PackagesRecord>>asList(Keys.PACKAGES_PKEY, Keys.UNIQUE_PACKAGE_FORGE);
    }

    @Override
    public Packages as(String alias) {
        return new Packages(DSL.name(alias), this);
    }

    @Override
    public Packages as(Name alias) {
        return new Packages(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Packages rename(String name) {
        return new Packages(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Packages rename(Name name) {
        return new Packages(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, String, String, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
