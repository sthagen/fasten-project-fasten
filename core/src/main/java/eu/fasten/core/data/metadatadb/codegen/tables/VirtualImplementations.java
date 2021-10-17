/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables;


import eu.fasten.core.data.metadatadb.codegen.Keys;
import eu.fasten.core.data.metadatadb.codegen.Public;
import eu.fasten.core.data.metadatadb.codegen.tables.records.VirtualImplementationsRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
        "jOOQ version:3.14.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VirtualImplementations extends TableImpl<VirtualImplementationsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.virtual_implementations</code>
     */
    public static final VirtualImplementations VIRTUAL_IMPLEMENTATIONS = new VirtualImplementations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VirtualImplementationsRecord> getRecordType() {
        return VirtualImplementationsRecord.class;
    }

    /**
     * The column <code>public.virtual_implementations.virtual_package_version_id</code>.
     */
    public final TableField<VirtualImplementationsRecord, Long> VIRTUAL_PACKAGE_VERSION_ID = createField(DSL.name("virtual_package_version_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.virtual_implementations.package_version_id</code>.
     */
    public final TableField<VirtualImplementationsRecord, Long> PACKAGE_VERSION_ID = createField(DSL.name("package_version_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private VirtualImplementations(Name alias, Table<VirtualImplementationsRecord> aliased) {
        this(alias, aliased, null);
    }

    private VirtualImplementations(Name alias, Table<VirtualImplementationsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.virtual_implementations</code> table reference
     */
    public VirtualImplementations(String alias) {
        this(DSL.name(alias), VIRTUAL_IMPLEMENTATIONS);
    }

    /**
     * Create an aliased <code>public.virtual_implementations</code> table reference
     */
    public VirtualImplementations(Name alias) {
        this(alias, VIRTUAL_IMPLEMENTATIONS);
    }

    /**
     * Create a <code>public.virtual_implementations</code> table reference
     */
    public VirtualImplementations() {
        this(DSL.name("virtual_implementations"), null);
    }

    public <O extends Record> VirtualImplementations(Table<O> child, ForeignKey<O, VirtualImplementationsRecord> key) {
        super(child, key, VIRTUAL_IMPLEMENTATIONS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<VirtualImplementationsRecord>> getKeys() {
        return Arrays.<UniqueKey<VirtualImplementationsRecord>>asList(Keys.UNIQUE_VIRTUAL_IMPLEMENTATION);
    }

    @Override
    public List<ForeignKey<VirtualImplementationsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<VirtualImplementationsRecord, ?>>asList(Keys.VIRTUAL_IMPLEMENTATIONS__VIRTUAL_IMPLEMENTATIONS_VIRTUAL_PACKAGE_VERSION_ID_FKEY, Keys.VIRTUAL_IMPLEMENTATIONS__VIRTUAL_IMPLEMENTATIONS_PACKAGE_VERSION_ID_FKEY);
    }

    public PackageVersions virtualImplementationsVirtualPackageVersionIdFkey() {
        return new PackageVersions(this, Keys.VIRTUAL_IMPLEMENTATIONS__VIRTUAL_IMPLEMENTATIONS_VIRTUAL_PACKAGE_VERSION_ID_FKEY);
    }

    public PackageVersions virtualImplementationsPackageVersionIdFkey() {
        return new PackageVersions(this, Keys.VIRTUAL_IMPLEMENTATIONS__VIRTUAL_IMPLEMENTATIONS_PACKAGE_VERSION_ID_FKEY);
    }

    @Override
    public VirtualImplementations as(String alias) {
        return new VirtualImplementations(DSL.name(alias), this);
    }

    @Override
    public VirtualImplementations as(Name alias) {
        return new VirtualImplementations(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public VirtualImplementations rename(String name) {
        return new VirtualImplementations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public VirtualImplementations rename(Name name) {
        return new VirtualImplementations(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
