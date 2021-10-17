/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen;


import eu.fasten.core.data.metadatadb.codegen.tables.ArtifactRepositories;
import eu.fasten.core.data.metadatadb.codegen.tables.BinaryModuleContents;
import eu.fasten.core.data.metadatadb.codegen.tables.BinaryModules;
import eu.fasten.core.data.metadatadb.codegen.tables.CallSites;
import eu.fasten.core.data.metadatadb.codegen.tables.Callables;
import eu.fasten.core.data.metadatadb.codegen.tables.Dependencies;
import eu.fasten.core.data.metadatadb.codegen.tables.Files;
import eu.fasten.core.data.metadatadb.codegen.tables.IngestedArtifacts;
import eu.fasten.core.data.metadatadb.codegen.tables.ModuleContents;
import eu.fasten.core.data.metadatadb.codegen.tables.ModuleNames;
import eu.fasten.core.data.metadatadb.codegen.tables.Modules;
import eu.fasten.core.data.metadatadb.codegen.tables.PackageVersions;
import eu.fasten.core.data.metadatadb.codegen.tables.Packages;
import eu.fasten.core.data.metadatadb.codegen.tables.PgpArmorHeaders;
import eu.fasten.core.data.metadatadb.codegen.tables.VirtualImplementations;
import eu.fasten.core.data.metadatadb.codegen.tables.Vulnerabilities;
import eu.fasten.core.data.metadatadb.codegen.tables.VulnerabilitiesPurls;
import eu.fasten.core.data.metadatadb.codegen.tables.VulnerabilitiesXCallables;
import eu.fasten.core.data.metadatadb.codegen.tables.VulnerabilitiesXPackageVersions;
import eu.fasten.core.data.metadatadb.codegen.tables.records.PgpArmorHeadersRecord;

import javax.annotation.processing.Generated;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;


/**
 * Convenience access to all tables in public.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.artifact_repositories</code>.
     */
    public static final ArtifactRepositories ARTIFACT_REPOSITORIES = ArtifactRepositories.ARTIFACT_REPOSITORIES;

    /**
     * The table <code>public.binary_module_contents</code>.
     */
    public static final BinaryModuleContents BINARY_MODULE_CONTENTS = BinaryModuleContents.BINARY_MODULE_CONTENTS;

    /**
     * The table <code>public.binary_modules</code>.
     */
    public static final BinaryModules BINARY_MODULES = BinaryModules.BINARY_MODULES;

    /**
     * The table <code>public.call_sites</code>.
     */
    public static final CallSites CALL_SITES = CallSites.CALL_SITES;

    /**
     * The table <code>public.callables</code>.
     */
    public static final Callables CALLABLES = Callables.CALLABLES;

    /**
     * The table <code>public.dependencies</code>.
     */
    public static final Dependencies DEPENDENCIES = Dependencies.DEPENDENCIES;

    /**
     * The table <code>public.files</code>.
     */
    public static final Files FILES = Files.FILES;

    /**
     * The table <code>public.ingested_artifacts</code>.
     */
    public static final IngestedArtifacts INGESTED_ARTIFACTS = IngestedArtifacts.INGESTED_ARTIFACTS;

    /**
     * The table <code>public.module_contents</code>.
     */
    public static final ModuleContents MODULE_CONTENTS = ModuleContents.MODULE_CONTENTS;

    /**
     * The table <code>public.module_names</code>.
     */
    public static final ModuleNames MODULE_NAMES = ModuleNames.MODULE_NAMES;

    /**
     * The table <code>public.modules</code>.
     */
    public static final Modules MODULES = Modules.MODULES;

    /**
     * The table <code>public.package_versions</code>.
     */
    public static final PackageVersions PACKAGE_VERSIONS = PackageVersions.PACKAGE_VERSIONS;

    /**
     * The table <code>public.packages</code>.
     */
    public static final Packages PACKAGES = Packages.PACKAGES;

    /**
     * The table <code>public.pgp_armor_headers</code>.
     */
    public static final PgpArmorHeaders PGP_ARMOR_HEADERS = PgpArmorHeaders.PGP_ARMOR_HEADERS;

    /**
     * Call <code>public.pgp_armor_headers</code>.
     */
    public static Result<PgpArmorHeadersRecord> PGP_ARMOR_HEADERS(
          Configuration configuration
        , String __1
    ) {
        return configuration.dsl().selectFrom(eu.fasten.core.data.metadatadb.codegen.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
              __1
        )).fetch();
    }

    /**
     * Get <code>public.pgp_armor_headers</code> as a table.
     */
    public static PgpArmorHeaders PGP_ARMOR_HEADERS(
          String __1
    ) {
        return eu.fasten.core.data.metadatadb.codegen.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
              __1
        );
    }

    /**
     * Get <code>public.pgp_armor_headers</code> as a table.
     */
    public static PgpArmorHeaders PGP_ARMOR_HEADERS(
          Field<String> __1
    ) {
        return eu.fasten.core.data.metadatadb.codegen.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
              __1
        );
    }

    /**
     * The table <code>public.virtual_implementations</code>.
     */
    public static final VirtualImplementations VIRTUAL_IMPLEMENTATIONS = VirtualImplementations.VIRTUAL_IMPLEMENTATIONS;

    /**
     * The table <code>public.vulnerabilities</code>.
     */
    public static final Vulnerabilities VULNERABILITIES = Vulnerabilities.VULNERABILITIES;

    /**
     * The table <code>public.vulnerabilities_purls</code>.
     */
    public static final VulnerabilitiesPurls VULNERABILITIES_PURLS = VulnerabilitiesPurls.VULNERABILITIES_PURLS;

    /**
     * The table <code>public.vulnerabilities_x_callables</code>.
     */
    public static final VulnerabilitiesXCallables VULNERABILITIES_X_CALLABLES = VulnerabilitiesXCallables.VULNERABILITIES_X_CALLABLES;

    /**
     * The table <code>public.vulnerabilities_x_package_versions</code>.
     */
    public static final VulnerabilitiesXPackageVersions VULNERABILITIES_X_PACKAGE_VERSIONS = VulnerabilitiesXPackageVersions.VULNERABILITIES_X_PACKAGE_VERSIONS;
}
