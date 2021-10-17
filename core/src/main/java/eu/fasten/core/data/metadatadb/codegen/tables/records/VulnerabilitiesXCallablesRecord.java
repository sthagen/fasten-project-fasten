/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen.tables.records;


import eu.fasten.core.data.metadatadb.codegen.tables.VulnerabilitiesXCallables;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


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
public class VulnerabilitiesXCallablesRecord extends TableRecordImpl<VulnerabilitiesXCallablesRecord> implements Record2<String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.vulnerabilities_x_callables.vulnerability_id</code>.
     */
    public void setVulnerabilityId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.vulnerabilities_x_callables.vulnerability_id</code>.
     */
    public String getVulnerabilityId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.vulnerabilities_x_callables.callable_id</code>.
     */
    public void setCallableId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.vulnerabilities_x_callables.callable_id</code>.
     */
    public Long getCallableId() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return VulnerabilitiesXCallables.VULNERABILITIES_X_CALLABLES.VULNERABILITY_ID;
    }

    @Override
    public Field<Long> field2() {
        return VulnerabilitiesXCallables.VULNERABILITIES_X_CALLABLES.CALLABLE_ID;
    }

    @Override
    public String component1() {
        return getVulnerabilityId();
    }

    @Override
    public Long component2() {
        return getCallableId();
    }

    @Override
    public String value1() {
        return getVulnerabilityId();
    }

    @Override
    public Long value2() {
        return getCallableId();
    }

    @Override
    public VulnerabilitiesXCallablesRecord value1(String value) {
        setVulnerabilityId(value);
        return this;
    }

    @Override
    public VulnerabilitiesXCallablesRecord value2(Long value) {
        setCallableId(value);
        return this;
    }

    @Override
    public VulnerabilitiesXCallablesRecord values(String value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VulnerabilitiesXCallablesRecord
     */
    public VulnerabilitiesXCallablesRecord() {
        super(VulnerabilitiesXCallables.VULNERABILITIES_X_CALLABLES);
    }

    /**
     * Create a detached, initialised VulnerabilitiesXCallablesRecord
     */
    public VulnerabilitiesXCallablesRecord(String vulnerabilityId, Long callableId) {
        super(VulnerabilitiesXCallables.VULNERABILITIES_X_CALLABLES);

        setVulnerabilityId(vulnerabilityId);
        setCallableId(callableId);
    }
}
