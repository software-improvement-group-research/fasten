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

package eu.fasten.analyzer.javacgopalv3.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.fasten.analyzer.javacgopalv3.data.MavenCoordinate;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class MavenCoordinateTest {

    @Test
    void constructorTest() {
        MavenCoordinate coordinate1 = new MavenCoordinate("group", "artifact", "version");
        MavenCoordinate coordinate2 = new MavenCoordinate(new ArrayList<>(Collections
                .singletonList("repo")), "group", "artifact", "version");

        assertEquals("group", coordinate1.getGroupID());
        assertEquals(coordinate1.getGroupID(), coordinate2.getGroupID());

        assertEquals("artifact", coordinate1.getArtifactID());
        assertEquals(coordinate1.getArtifactID(), coordinate2.getArtifactID());

        assertEquals("version", coordinate1.getVersionConstraint());
        assertEquals(coordinate1.getVersionConstraint(), coordinate2.getVersionConstraint());

        assertEquals(new ArrayList<>(Collections
                        .singletonList("https://repo.maven.apache.org/maven2/")),
                coordinate1.getMavenRepos());
        assertNotEquals(coordinate1.getMavenRepos(), coordinate2.getMavenRepos());

        var repos = new ArrayList<>(Collections.singletonList("repo"));
        coordinate1.setMavenRepos(repos);

        assertEquals(coordinate1.getMavenRepos(), coordinate2.getMavenRepos());
    }

    @Test
    void fromString() {
        var coordinate = MavenCoordinate.fromString("GroupID:ArtifactID:Version");

        assertEquals("GroupID", coordinate.getGroupID());
        assertEquals("ArtifactID", coordinate.getArtifactID());
        assertEquals("Version", coordinate.getVersionConstraint());
    }

    @Test
    void getProduct() {
        var coordinate = MavenCoordinate.fromString("GroupID:ArtifactID:Version");
        assertEquals("GroupID:ArtifactID", coordinate.getProduct());
    }

    @Test
    void getCoordinate() {
        var coordinate = MavenCoordinate.fromString("GroupID:ArtifactID:Version");
        assertEquals("GroupID:ArtifactID:Version", coordinate.getCoordinate());
    }

    @Test
    void toURL() {
        var coordinate = MavenCoordinate.fromString("GroupID:ArtifactID:Version");
        assertEquals("repo/GroupID/ArtifactID/Version/ArtifactID-Version.jar", coordinate.toURL("repo/", "jar"));
    }

    // ------------------
    // Maven Resolver Tests
    // ------------------

    @Test
    void downloadJarEmptyRepos() {
        MavenCoordinate coordinate =
                new MavenCoordinate(new ArrayList<>(), "group", "artifact", "version");

        var resolver = new MavenCoordinate.MavenResolver();

        assertThrows(FileNotFoundException.class, () -> resolver.downloadJar(coordinate));
    }

    @Test
    void downloadJarWrongRepos() {
        MavenCoordinate coordinate = new MavenCoordinate(new ArrayList<>(Collections
                .singletonList("repo")), "group", "artifact", "version");

        var resolver = new MavenCoordinate.MavenResolver();

        assertThrows(FileNotFoundException.class, () -> resolver.downloadJar(coordinate));
    }
}