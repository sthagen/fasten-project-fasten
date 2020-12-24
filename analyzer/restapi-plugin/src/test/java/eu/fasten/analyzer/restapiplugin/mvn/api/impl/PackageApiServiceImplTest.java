package eu.fasten.analyzer.restapiplugin.mvn.api.impl;

import eu.fasten.analyzer.restapiplugin.mvn.KnowledgeBaseConnector;
import eu.fasten.analyzer.restapiplugin.mvn.RestApplication;
import eu.fasten.core.data.metadatadb.MetadataDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackageApiServiceImplTest {

    private PackageApiServiceImpl service;
    private MetadataDao kbDao;
    private final int offset = 0;
    private final int limit = Integer.parseInt(RestApplication.DEFAULT_PAGE_SIZE);

    @BeforeEach
    void setUp() {
        service = new PackageApiServiceImpl();
        kbDao = Mockito.mock(MetadataDao.class);
        KnowledgeBaseConnector.kbDao = kbDao;
    }

    @Test
    void getAllPackagesTest() {
        var response = "all packages";
        Mockito.when(kbDao.getAllPackages(offset, limit)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getAllPackages(offset, limit);
        assertEquals(expected, result);
        Mockito.verify(kbDao).getAllPackages(offset, limit);
    }

    @Test
    void getPackageLastVersionTest() {
        var packageName = "pkg";
        var response = "latest package version";
        Mockito.when(kbDao.getPackageLastVersion(packageName)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getPackageLastVersion(packageName);
        assertEquals(expected, result);

        Mockito.when(kbDao.getPackageLastVersion(packageName)).thenReturn(null);
        result = service.getPackageLastVersion(packageName);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Mockito.verify(kbDao, Mockito.times(2)).getPackageLastVersion(packageName);
    }

    @Test
    void getPackageVersionsTest() {
        var packageName = "pkg";
        var response = "package versions";
        Mockito.when(kbDao.getPackageVersions(packageName, offset, limit)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getPackageVersions(packageName, offset, limit);
        assertEquals(expected, result);
        Mockito.verify(kbDao).getPackageVersions(packageName, offset, limit);
    }

    @Test
    void getPackageVersionTest() {
        var packageName = "pkg";
        var version = "pkg version";
        var response = "package version";
        Mockito.when(kbDao.getPackageVersion(packageName, version)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getPackageVersion(packageName, version);
        assertEquals(expected, result);

        Mockito.when(kbDao.getPackageVersion(packageName, version)).thenReturn(null);
        result = service.getPackageVersion(packageName, version);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Mockito.verify(kbDao, Mockito.times(2)).getPackageVersion(packageName, version);
    }

    @Test
    void getPackageMetadataTest() {
        var packageName = "pkg";
        var version = "pkg version";
        var response = "package metadata";
        Mockito.when(kbDao.getPackageMetadata(packageName, version)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getPackageMetadata(packageName, version);
        assertEquals(expected, result);

        Mockito.when(kbDao.getPackageMetadata(packageName, version)).thenReturn(null);
        result = service.getPackageMetadata(packageName, version);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Mockito.verify(kbDao, Mockito.times(2)).getPackageMetadata(packageName, version);
    }

    @Test
    void getPackageCallgraphTest() {
        var packageName = "pkg";
        var version = "pkg version";
        var response = "package callgraph";
        Mockito.when(kbDao.getPackageCallgraph(packageName, version, offset, limit)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.getPackageCallgraph(packageName, version, offset, limit);
        Mockito.verify(kbDao).getPackageCallgraph(packageName, version, offset, limit);
        assertEquals(expected, result);
    }

    @Test
    void searchPackageNamesTest() {
        var packageName = "pkg";
        var response = "matching package versions";
        Mockito.when(kbDao.searchPackageNames(packageName, offset, limit)).thenReturn(response);
        var expected = new ResponseEntity<>(response, HttpStatus.OK);
        var result = service.searchPackageNames(packageName, offset, limit);
        assertEquals(expected, result);
        Mockito.verify(kbDao).searchPackageNames(packageName, offset, limit);
    }

    @Test
    void getERCGLinkTest() {
        var packageName = "group:artifact";
        var version = "version";
        KnowledgeBaseConnector.limaUrl = "http://lima.ewi.tudelft.nl";
        var expected = new ResponseEntity<>("http://lima.ewi.tudelft.nl/mvn/a/artifact/artifact_group_version.json", HttpStatus.OK);
        var result = service.getERCGLink(packageName, version);
        assertEquals(expected, result);
    }
}
