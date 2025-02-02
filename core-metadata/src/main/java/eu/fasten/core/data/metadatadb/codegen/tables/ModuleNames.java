/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.ModuleNamesRecord;

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
public class ModuleNames extends TableImpl<ModuleNamesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.module_names</code>
     */
    public static final ModuleNames MODULE_NAMES = new ModuleNames();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleNamesRecord> getRecordType() {
        return ModuleNamesRecord.class;
    }

    /**
     * The column <code>public.module_names.id</code>.
     */
    public final TableField<ModuleNamesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.module_names.name</code>.
     */
    public final TableField<ModuleNamesRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    private ModuleNames(Name alias, Table<ModuleNamesRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleNames(Name alias, Table<ModuleNamesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.module_names</code> table reference
     */
    public ModuleNames(String alias) {
        this(DSL.name(alias), MODULE_NAMES);
    }

    /**
     * Create an aliased <code>public.module_names</code> table reference
     */
    public ModuleNames(Name alias) {
        this(alias, MODULE_NAMES);
    }

    /**
     * Create a <code>public.module_names</code> table reference
     */
    public ModuleNames() {
        this(DSL.name("module_names"), null);
    }

    public <O extends Record> ModuleNames(Table<O> child, ForeignKey<O, ModuleNamesRecord> key) {
        super(child, key, MODULE_NAMES);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<ModuleNamesRecord, Long> getIdentity() {
        return (Identity<ModuleNamesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ModuleNamesRecord> getPrimaryKey() {
        return Keys.MODULE_NAMES_PKEY;
    }

    @Override
    public List<UniqueKey<ModuleNamesRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.UNIQUE_MODULE_NAMES);
    }

    @Override
    public ModuleNames as(String alias) {
        return new ModuleNames(DSL.name(alias), this);
    }

    @Override
    public ModuleNames as(Name alias) {
        return new ModuleNames(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleNames rename(String name) {
        return new ModuleNames(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleNames rename(Name name) {
        return new ModuleNames(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
