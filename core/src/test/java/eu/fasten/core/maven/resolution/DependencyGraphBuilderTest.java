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

package eu.fasten.core.maven.resolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fasten.core.maven.data.PomAnalysisResult;
import eu.fasten.core.maven.data.VersionConstraint;

public class DependencyGraphBuilderTest {

    private DependencyGraphBuilder graphBuilder;

    @BeforeEach
    public void setup() {
        graphBuilder = new DependencyGraphBuilder();
    }

    @Test
    public void findMatchingRevisionsTest() {
        var revisions = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var constraints = Set.of(
                new VersionConstraint("(1.0,3.0]")
        );
        var expected = List.of(
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);

        constraints = Set.of(
                new VersionConstraint("[2.0,3.0]")
        );
        actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);
    }

    private PomAnalysisResult newRevision(String g, String a, String v, Timestamp timestamp) {
        var par = new PomAnalysisResult();
        par.groupId = g;
        par.artifactId = a;
        par.version = v;
        par.releaseDate = timestamp.getTime();
        return par;
    }

    @Test
    public void findMatchingRevisionsMultipleConstraintsTest() {
        var revisions = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var constraints = Set.of(
                new VersionConstraint("[1.0,2.0)"),
                new VersionConstraint("(2.0,3.0]")
        );
        var expected = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);
    }

    @Test
    public void findMatchingRevisionsSimpleTest() {
        var revisions = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var constraints = Set.of(new VersionConstraint("1.0"));
        var expected = List.of(newRevision("a", "a", "1.0", new Timestamp(1)));
        var actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);

        constraints = Set.of(new VersionConstraint("[1.0]"));
        actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);
    }

    @Test
    public void findMatchingRevisionsWithoutLowerOrUpperBoundTest() {
        var revisions = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        var constraints = Set.of(new VersionConstraint("(,2.0]"));
        var expected = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2))
        );
        var actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);

        constraints = Set.of(new VersionConstraint("[2.0,]"));
        expected = List.of(
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3))
        );
        actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);
    }

    @Test
    public void findMatchingRevisionsRangesWithRequirementsTest() {
        var revisions = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "2.0", new Timestamp(2)),
                newRevision("a", "a", "3.0", new Timestamp(3)),
                newRevision("a", "a", "4.0", new Timestamp(4))
        );
        var constraints = Set.of(
                new VersionConstraint("(,1.0]"),
                new VersionConstraint("[3.0,)")
        );
        var expected = List.of(
                newRevision("a", "a", "1.0", new Timestamp(1)),
                newRevision("a", "a", "3.0", new Timestamp(3)),
                newRevision("a", "a", "4.0", new Timestamp(4))
        );
        var actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);

        constraints = Set.of(
                new VersionConstraint("(,2.0)"),
                new VersionConstraint("(2.0,)")
        );
        actual = graphBuilder.findMatchingRevisions(revisions, constraints);
        assertEquals(expected, actual);
    }
}