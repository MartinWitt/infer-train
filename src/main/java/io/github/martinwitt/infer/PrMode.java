package io.github.martinwitt.infer;

import com.contrastsecurity.sarif.Result;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class PrMode {
    /**
     * Filters the results to only include results that are in files that have been changed in the PR.
     * @param results  The results to filter.
     * @param repoRoot  The root of the repository.
     * @param baseRef  The base ref of the PR.
     * @param headRef  The head ref of the PR.
     * @return  The filtered results.
     */
    public List<Result> filterResults(List<Result> results, Path repoRoot, String baseRef, String headRef) {
        try (Git git = Git.open(repoRoot.toFile())) {
            AbstractTreeIterator oldTreeParser = prepareTreeParser(git.getRepository(), baseRef);
            AbstractTreeIterator newTreeParser = prepareTreeParser(git.getRepository(), headRef);
            List<DiffEntry> diff = git.diff()
                    .setOldTree(oldTreeParser)
                    .setNewTree(newTreeParser)
                    // to filter on Suffix use the following instead
                    // setPathFilter(PathFilter.create("README.md")).
                    // setPathFilter(PathSuffixFilter.create(".java")).
                    .call();
            var changedFiles = diff.stream().map(DiffEntry::getNewPath).toList();
            List<Result> filteredResults = new ArrayList<>();
            for (Result result : results) {
                if (changedFiles.contains(getFilePathFromResult(result))) {
                    filteredResults.add(result);
                }
            }
            return filteredResults;
        } catch (Exception e) {
            // error while filtering lets keep all results
            return results;
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    private static String getFilePathFromResult(Result result) {
        return result.getLocations()
                .get(0)
                .getPhysicalLocation()
                .getArtifactLocation()
                .getUri()
                // someone at facebooks infer team decided to prefix the file path with "file:" which is not a valid
                // path for github annotations
                .replace("file:", "");
    }
}
