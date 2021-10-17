/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables.records;


import eu.fasten.core.data.metadatadb.codegen.tables.Files;

import java.sql.Timestamp;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.15"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FilesRecord extends UpdatableRecordImpl<FilesRecord> implements Record6<Long, Long, String, byte[], Timestamp, JSONB> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.files.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.files.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.files.package_version_id</code>.
     */
    public void setPackageVersionId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.files.package_version_id</code>.
     */
    public Long getPackageVersionId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.files.path</code>.
     */
    public void setPath(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.files.path</code>.
     */
    public String getPath() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.files.checksum</code>.
     */
    public void setChecksum(byte[] value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.files.checksum</code>.
     */
    public byte[] getChecksum() {
        return (byte[]) get(3);
    }

    /**
     * Setter for <code>public.files.created_at</code>.
     */
    public void setCreatedAt(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.files.created_at</code>.
     */
    public Timestamp getCreatedAt() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>public.files.metadata</code>.
     */
    public void setMetadata(JSONB value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.files.metadata</code>.
     */
    public JSONB getMetadata() {
        return (JSONB) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, byte[], Timestamp, JSONB> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, String, byte[], Timestamp, JSONB> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Files.FILES.ID;
    }

    @Override
    public Field<Long> field2() {
        return Files.FILES.PACKAGE_VERSION_ID;
    }

    @Override
    public Field<String> field3() {
        return Files.FILES.PATH;
    }

    @Override
    public Field<byte[]> field4() {
        return Files.FILES.CHECKSUM;
    }

    @Override
    public Field<Timestamp> field5() {
        return Files.FILES.CREATED_AT;
    }

    @Override
    public Field<JSONB> field6() {
        return Files.FILES.METADATA;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getPackageVersionId();
    }

    @Override
    public String component3() {
        return getPath();
    }

    @Override
    public byte[] component4() {
        return getChecksum();
    }

    @Override
    public Timestamp component5() {
        return getCreatedAt();
    }

    @Override
    public JSONB component6() {
        return getMetadata();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getPackageVersionId();
    }

    @Override
    public String value3() {
        return getPath();
    }

    @Override
    public byte[] value4() {
        return getChecksum();
    }

    @Override
    public Timestamp value5() {
        return getCreatedAt();
    }

    @Override
    public JSONB value6() {
        return getMetadata();
    }

    @Override
    public FilesRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public FilesRecord value2(Long value) {
        setPackageVersionId(value);
        return this;
    }

    @Override
    public FilesRecord value3(String value) {
        setPath(value);
        return this;
    }

    @Override
    public FilesRecord value4(byte[] value) {
        setChecksum(value);
        return this;
    }

    @Override
    public FilesRecord value5(Timestamp value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public FilesRecord value6(JSONB value) {
        setMetadata(value);
        return this;
    }

    @Override
    public FilesRecord values(Long value1, Long value2, String value3, byte[] value4, Timestamp value5, JSONB value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FilesRecord
     */
    public FilesRecord() {
        super(Files.FILES);
    }

    /**
     * Create a detached, initialised FilesRecord
     */
    public FilesRecord(Long id, Long packageVersionId, String path, byte[] checksum, Timestamp createdAt, JSONB metadata) {
        super(Files.FILES);

        setId(id);
        setPackageVersionId(packageVersionId);
        setPath(path);
        setChecksum(checksum);
        setCreatedAt(createdAt);
        setMetadata(metadata);
    }
}
