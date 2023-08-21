package io.github.martinwitt.infer;

import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.SarifSchema210;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.kohsuke.github.GitHub;

public class GitHubAction {

    private static final String INFER_OUT_REPORT_SARIF = "/infer-out/report.sarif";
    private static final String ACTION_NAME = "Infer-Scan";
    private static final String ACTION_DESCRIPTION = "Run Infer static analysis on a GitHub repository";
    private static final String INFER_COMMAND = "infer";

    @Inject
    PrMode prMode;

    @Action
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub) throws IOException {
        String buildCommand = inputs.get("build-command").orElseThrow();
        List<String> buildCommandArgs = new ArrayList<>();
        buildCommandArgs.addAll(List.of("run", "--sarif", "--"));
        buildCommandArgs.addAll(Arrays.asList(buildCommand.split(" ", -1)));
        try {
            commands.jobSummary("# Infer scan start\n");
            commands.group("## infer build log");
            InferRunResult runInfer = runInfer(buildCommandArgs);
            commands.notice(runInfer.stdOut);
            commands.notice(runInfer.stdErr);
            commands.notice("Exit value of infer was:" + runInfer.exitCode);
            commands.endGroup();
            commands.appendJobSummary("## Infer scan completed");
            commands.group("## Infer scan results");
            commands.appendJobSummary(Files.readString(Path.of(context.getGitHubWorkspace(), "/infer-out/report.txt")));
            commands.endGroup();
            List<Result> results = getResults(context);
            commands.notice("Found " + results.size() + " results");
            commands.setOutput(
                    "Infer-Sarif", Files.readString(Path.of(context.getGitHubWorkspace(), INFER_OUT_REPORT_SARIF)));
            for (Result result : results) {
                String ruleId = result.getRuleId();
                String message = result.getMessage().getText();
                String markdown =
                        """
                            # %s
                            ```java
                            %s
                            ```

                                """
                                .formatted(ruleId, message);
                commands.warning(markdown);
            }
            if (inputs.getBoolean("use-annotations").orElse(false)) {

                if (inputs.getBoolean("pr-mode").orElse(false)) {
                    results = prMode.filterResults(
                            results,
                            Path.of(context.getGitHubWorkspace()),
                            context.getGitHubBaseRef(),
                            context.getGitHubRef());
                }
                for (var result : results) {
                    result.getLocations().get(0).getPhysicalLocation().getArtifactLocation();
                    String fileName = getFilePathFromResult(result);
                    String message = result.getMessage().getText();
                    int startLine = result.getLocations()
                            .get(0)
                            .getPhysicalLocation()
                            .getRegion()
                            .getStartLine();
                    int startColumn = result.getLocations()
                            .get(0)
                            .getPhysicalLocation()
                            .getRegion()
                            .getStartColumn();
                    commands.warning(message, result.getRuleId(), fileName, startLine, null, startColumn, null);
                }
            }
        } catch (Exception e) {
            commands.appendJobSummary("## Infer scan failed\n" + e.toString());
        }
    }

    private List<Result> getResults(Context context) throws IOException, StreamReadException, DatabindException {
        StringReader reader = new StringReader(Files.readString(
                Path.of(context.getGitHubWorkspace() + INFER_OUT_REPORT_SARIF), StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        SarifSchema210 sarif = mapper.readValue(reader, SarifSchema210.class);
        return sarif.getRuns().get(0).getResults();
    }

    private InferRunResult runInfer(List<String> args) {
        ProcResult run = new ProcBuilder(INFER_COMMAND)
                .withArgs(args.toArray(new String[0]))
                .withNoTimeout()
                .run();
        return new InferRunResult(run.getOutputString(), run.getErrorString(), run.getExitValue());
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

    record InferRunResult(String stdOut, String stdErr, int exitCode) {}
}
