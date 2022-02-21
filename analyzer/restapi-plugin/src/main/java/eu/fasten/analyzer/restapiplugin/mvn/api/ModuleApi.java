/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.analyzer.restapiplugin.mvn.api;

import eu.fasten.analyzer.restapiplugin.mvn.KnowledgeBaseConnector;
import eu.fasten.analyzer.restapiplugin.mvn.LazyIngestionProvider;
import eu.fasten.analyzer.restapiplugin.mvn.RestApplication;
import eu.fasten.core.maven.data.PackageVersionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/mvn/packages")
public class ModuleApi {

    @GetMapping(value = "/{pkg}/{pkg_ver}/modules", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getPackageModules(@PathVariable("pkg") String package_name,
                                             @PathVariable("pkg_ver") String package_version,
                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                             @RequestParam(required = false, defaultValue = RestApplication.DEFAULT_PAGE_SIZE) int limit,
                                             @RequestParam(required = false) String artifactRepository,
                                             @RequestParam(required = false) Long releaseDate) {
        String result;
        try {
            result = KnowledgeBaseConnector.kbDao.getPackageModules(
                    package_name, package_version, offset, limit);
        } catch (PackageVersionNotFoundException e) {
            try {
                LazyIngestionProvider.ingestArtifactIfNecessary(package_name, package_version, artifactRepository, releaseDate);
            } catch (IllegalArgumentException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("Package version not found, but should be processed soon. Try again later", HttpStatus.CREATED);
        }
        result = result.replace("\\/", "/");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/{pkg}/{pkg_ver}/modules/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getModuleMetadata(@PathVariable("pkg") String package_name,
                                             @PathVariable("pkg_ver") String package_version,
                                             @RequestBody String module_namespace,
                                             @RequestParam(value = "artifactRepository", required = false) String artifactRepository,
                                             @RequestParam(required = false) Long releaseDate) {
        
        String result;
        try {
            result = KnowledgeBaseConnector.kbDao.getModuleMetadata(package_name, package_version, module_namespace);
        } catch (PackageVersionNotFoundException e) {
            try {
                LazyIngestionProvider.ingestArtifactIfNecessary(package_name, package_version, artifactRepository, releaseDate);
            } catch (IllegalArgumentException | IOException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Package version not found, but should be processed soon. Try again later", HttpStatus.CREATED);
        }
        if (result == null) {
            return new ResponseEntity<>("Module not found", HttpStatus.NOT_FOUND);
        }
        result = result.replace("\\/", "/");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/{pkg}/{pkg_ver}/modules/files", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getModuleFiles(@PathVariable("pkg") String package_name,
                                          @PathVariable("pkg_ver") String package_version,
                                          @RequestBody String module_namespace,
                                          @RequestParam(required = false, defaultValue = "0") int offset,
                                          @RequestParam(required = false, defaultValue = RestApplication.DEFAULT_PAGE_SIZE) int limit,
                                          @RequestParam(required = false) String artifactRepository,
                                          @RequestParam(required = false) Long releaseDate) {

        String result;
        try {
            result = KnowledgeBaseConnector.kbDao.getModuleFiles(
                    package_name, package_version, module_namespace, offset, limit);
        } catch (PackageVersionNotFoundException e) {
            try {
                LazyIngestionProvider.ingestArtifactIfNecessary(package_name, package_version, artifactRepository, releaseDate);
            } catch (IllegalArgumentException | IOException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Package version not found, but should be processed soon. Try again later", HttpStatus.CREATED);
        }
        if (result == null) {
            return new ResponseEntity<>("Module not found", HttpStatus.NOT_FOUND);
        }
        result = result.replace("\\/", "/");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/{pkg}/{pkg_ver}/modules/callables", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getModuleCallables(@PathVariable("pkg") String package_name,
                                              @PathVariable("pkg_ver") String package_version,
                                              @RequestBody String module_namespace,
                                              @RequestParam(required = false, defaultValue = "0") int offset,
                                              @RequestParam(required = false, defaultValue = RestApplication.DEFAULT_PAGE_SIZE) int limit,
                                              @RequestParam(required = false) String artifactRepository,
                                              @RequestParam(required = false) Long releaseDate) {

        String result;
        try {
            result = KnowledgeBaseConnector.kbDao.getModuleCallables(
                    package_name, package_version, module_namespace, offset, limit);
        } catch (PackageVersionNotFoundException e) {
            try {
                LazyIngestionProvider.ingestArtifactIfNecessary(package_name, package_version, artifactRepository, releaseDate);
            } catch (IllegalArgumentException | IOException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Package version not found, but should be processed soon. Try again later", HttpStatus.CREATED);
        }
        if (result == null) {
            return new ResponseEntity<>("Module not found", HttpStatus.NOT_FOUND);
        }
        result = result.replace("\\/", "/");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
