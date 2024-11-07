package org.jenkinsci.maven.plugins.hpi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.DependencyNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AssembleDependenciesMojoTest {

    private AssembleDependenciesMojo assembleDependenciesMojo;

    @Before
    public void setUp() throws Exception {
        assembleDependenciesMojo = new AssembleDependenciesMojo();

        MavenSession mockSession = Mockito.mock(MavenSession.class);
        MavenProject mockProject = Mockito.mock(MavenProject.class);
        ProjectBuildingRequest mockRequest = new DefaultProjectBuildingRequest();
        RepositorySystem repositorySystem = Mockito.mock(RepositorySystem.class);
        CollectResult collectResult = Mockito.mock(CollectResult.class);

        Mockito.when(mockSession.getProjectBuildingRequest()).thenReturn(mockRequest);
        assembleDependenciesMojo.session = mockSession;
        assembleDependenciesMojo.project = mockProject;

        List<String> parsedScopes = new ArrayList<>();
        parsedScopes.add(null);
        parsedScopes.add("compile");
        assembleDependenciesMojo.setParsedScopes(parsedScopes);

        DependencyNode rootNode = createDependencyNode("root", "compile", "hpi", null);
        DependencyNode childNode1 = createDependencyNode("child-1", "compile", "hpi", rootNode);
        DependencyNode grandChildNode1 = createDependencyNode("grandchild-1", "compile", "hpi", childNode1);
        DependencyNode grandGrandChildNode1 = createDependencyNode("grand-grandchild-1", "compile", "hpi", grandChildNode1);

        DependencyNode childNode2 = createDependencyNode("child-2", "compile", "hpi", rootNode);

        DependencyNode childNode3 = createDependencyNode("child-3", "compile", "hpi", rootNode);
        DependencyNode grandChildNode3 = createDependencyNode("grandchild-3", "compile", "hpi", childNode3);

        // Link nodes
        Mockito.when(rootNode.getChildren()).thenReturn(Arrays.asList(childNode1, childNode2, childNode3));
        Mockito.when(childNode1.getChildren()).thenReturn(Collections.singletonList(grandChildNode1));
        Mockito.when(grandChildNode1.getChildren()).thenReturn(Collections.singletonList(grandGrandChildNode1));

        Mockito.when(childNode2.getChildren()).thenReturn(Collections.emptyList());

        Mockito.when(childNode3.getChildren()).thenReturn(Collections.singletonList(grandChildNode3));
        Mockito.when(grandChildNode3.getChildren()).thenReturn(Collections.emptyList());


        Mockito.when(repositorySystem.collectDependencies(Mockito.any(), Mockito.any()))
               .thenReturn(rootNode);
    }

    @Test
    public void traverseProjectTest() throws Exception {
        assembleDependenciesMojo.traverseProject();
    }

    private DependencyNode createDependencyNode(String artifactId, String scope, String type, DependencyNode parent) {
        DependencyNode mockNode = Mockito.mock(DependencyNode.class);
        Artifact mockArtifact = Mockito.mock(Artifact.class);

        Mockito.when(mockArtifact.getArtifactId()).thenReturn(artifactId);
        Mockito.when(mockArtifact.getScope()).thenReturn(scope);
        Mockito.when(mockArtifact.getType()).thenReturn(type);

        return mockNode;
    }
}