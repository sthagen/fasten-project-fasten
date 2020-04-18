/*
 * This file is generated by jOOQ.
 */
package eu.fasten.core.data.metadatadb.codegen;


import eu.fasten.core.data.metadatadb.codegen.tables.BinaryModuleContents;
import eu.fasten.core.data.metadatadb.codegen.tables.BinaryModules;
import eu.fasten.core.data.metadatadb.codegen.tables.Callables;
import eu.fasten.core.data.metadatadb.codegen.tables.Dependencies;
import eu.fasten.core.data.metadatadb.codegen.tables.Edges;
import eu.fasten.core.data.metadatadb.codegen.tables.Files;
import eu.fasten.core.data.metadatadb.codegen.tables.ModuleContents;
import eu.fasten.core.data.metadatadb.codegen.tables.Modules;
import eu.fasten.core.data.metadatadb.codegen.tables.PackageVersions;
import eu.fasten.core.data.metadatadb.codegen.tables.Packages;

import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index BINARY_MODULE_CONTENTS_PKEY = Indexes0.BINARY_MODULE_CONTENTS_PKEY;
    public static final Index BINARY_MODULES_COMPOUND_INDEX = Indexes0.BINARY_MODULES_COMPOUND_INDEX;
    public static final Index BINARY_MODULES_PKEY = Indexes0.BINARY_MODULES_PKEY;
    public static final Index CALLABLES_PKEY = Indexes0.CALLABLES_PKEY;
    public static final Index UNIQUE_URI_CALL = Indexes0.UNIQUE_URI_CALL;
    public static final Index DEPENDENCIES_COMPOUND_INDEX = Indexes0.DEPENDENCIES_COMPOUND_INDEX;
    public static final Index UNIQUE_SOURCE_TARGET = Indexes0.UNIQUE_SOURCE_TARGET;
    public static final Index FILES_COMPOUND_INDEX = Indexes0.FILES_COMPOUND_INDEX;
    public static final Index FILES_PKEY = Indexes0.FILES_PKEY;
    public static final Index MODULE_CONTENTS_PKEY = Indexes0.MODULE_CONTENTS_PKEY;
    public static final Index MODULES_COMPOUND_INDEX = Indexes0.MODULES_COMPOUND_INDEX;
    public static final Index MODULES_PKEY = Indexes0.MODULES_PKEY;
    public static final Index PACKAGE_VERSIONS_COMPOUND_INDEX = Indexes0.PACKAGE_VERSIONS_COMPOUND_INDEX;
    public static final Index PACKAGE_VERSIONS_PKEY = Indexes0.PACKAGE_VERSIONS_PKEY;
    public static final Index PACKAGES_COMPOUND_INDEX = Indexes0.PACKAGES_COMPOUND_INDEX;
    public static final Index PACKAGES_PKEY = Indexes0.PACKAGES_PKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index BINARY_MODULE_CONTENTS_PKEY = Internal.createIndex("binary_module_contents_pkey", BinaryModuleContents.BINARY_MODULE_CONTENTS, new OrderField[] { BinaryModuleContents.BINARY_MODULE_CONTENTS.BINARY_MODULE_ID, BinaryModuleContents.BINARY_MODULE_CONTENTS.FILE_ID }, true);
        public static Index BINARY_MODULES_COMPOUND_INDEX = Internal.createIndex("binary_modules_compound_index", BinaryModules.BINARY_MODULES, new OrderField[] { BinaryModules.BINARY_MODULES.PACKAGE_VERSION_ID, BinaryModules.BINARY_MODULES.NAME }, false);
        public static Index BINARY_MODULES_PKEY = Internal.createIndex("binary_modules_pkey", BinaryModules.BINARY_MODULES, new OrderField[] { BinaryModules.BINARY_MODULES.ID }, true);
        public static Index CALLABLES_PKEY = Internal.createIndex("callables_pkey", Callables.CALLABLES, new OrderField[] { Callables.CALLABLES.ID }, true);
        public static Index UNIQUE_URI_CALL = Internal.createIndex("unique_uri_call", Callables.CALLABLES, new OrderField[] { Callables.CALLABLES.FASTEN_URI, Callables.CALLABLES.IS_INTERNAL_CALL }, true);
        public static Index DEPENDENCIES_COMPOUND_INDEX = Internal.createIndex("dependencies_compound_index", Dependencies.DEPENDENCIES, new OrderField[] { Dependencies.DEPENDENCIES.PACKAGE_VERSION_ID, Dependencies.DEPENDENCIES.DEPENDENCY_ID, Dependencies.DEPENDENCIES.VERSION_RANGE }, false);
        public static Index UNIQUE_SOURCE_TARGET = Internal.createIndex("unique_source_target", Edges.EDGES, new OrderField[] { Edges.EDGES.SOURCE_ID, Edges.EDGES.TARGET_ID }, true);
        public static Index FILES_COMPOUND_INDEX = Internal.createIndex("files_compound_index", Files.FILES, new OrderField[] { Files.FILES.PACKAGE_VERSION_ID, Files.FILES.PATH }, false);
        public static Index FILES_PKEY = Internal.createIndex("files_pkey", Files.FILES, new OrderField[] { Files.FILES.ID }, true);
        public static Index MODULE_CONTENTS_PKEY = Internal.createIndex("module_contents_pkey", ModuleContents.MODULE_CONTENTS, new OrderField[] { ModuleContents.MODULE_CONTENTS.MODULE_ID, ModuleContents.MODULE_CONTENTS.FILE_ID }, true);
        public static Index MODULES_COMPOUND_INDEX = Internal.createIndex("modules_compound_index", Modules.MODULES, new OrderField[] { Modules.MODULES.PACKAGE_VERSION_ID, Modules.MODULES.NAMESPACE }, false);
        public static Index MODULES_PKEY = Internal.createIndex("modules_pkey", Modules.MODULES, new OrderField[] { Modules.MODULES.ID }, true);
        public static Index PACKAGE_VERSIONS_COMPOUND_INDEX = Internal.createIndex("package_versions_compound_index", PackageVersions.PACKAGE_VERSIONS, new OrderField[] { PackageVersions.PACKAGE_VERSIONS.PACKAGE_ID, PackageVersions.PACKAGE_VERSIONS.VERSION, PackageVersions.PACKAGE_VERSIONS.CG_GENERATOR }, false);
        public static Index PACKAGE_VERSIONS_PKEY = Internal.createIndex("package_versions_pkey", PackageVersions.PACKAGE_VERSIONS, new OrderField[] { PackageVersions.PACKAGE_VERSIONS.ID }, true);
        public static Index PACKAGES_COMPOUND_INDEX = Internal.createIndex("packages_compound_index", Packages.PACKAGES, new OrderField[] { Packages.PACKAGES.PACKAGE_NAME, Packages.PACKAGES.FORGE }, false);
        public static Index PACKAGES_PKEY = Internal.createIndex("packages_pkey", Packages.PACKAGES, new OrderField[] { Packages.PACKAGES.ID }, true);
    }
}
