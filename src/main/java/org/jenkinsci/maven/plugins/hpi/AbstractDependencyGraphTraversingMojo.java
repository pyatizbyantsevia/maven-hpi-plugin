package org.jenkinsci.maven.plugins.hpi;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.plugins.annotations.Component;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractDependencyGraphTraversingMojo extends AbstractJenkinsMojo {
    @Component
    protected RepositorySystemSession repositorySystemSession;

    /**
     * Traverses the whole dependency tree rooted at the project.
     */
    protected void traverseProject() throws DependencyCollectionException  {
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRootArtifact(RepositoryUtils.toArtifact(project.getArtifact()));
        collectRequest.setRepositories(RepositoryUtils.toRepos(project.getRemoteArtifactRepositories()));

        CollectResult collectResult = repositorySystem.collectDependencies(repositorySystemSession, collectRequest);

        visit(collectResult.getRoot());
    }

    /**
     * Traverses a tree rooted at the given node.
     */
    protected void visit(DependencyNode g) {
        String artifactId = g.getArtifact().getArtifactId();
        System.out.println(artifactId);
        if (accept(g)) {
            for (DependencyNode dn : g.getChildren()) {
                visit(dn);
            }
        }
    }

    /**
     * Visits a node. Called at most once for any node in the dependency tree.
     *
     * @return true
     *         if the children should be traversed.
     */
    protected abstract boolean accept(DependencyNode g);
}
