package io.github.martinwitt.infer;

import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.SarifSchema210;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;
import io.quarkiverse.githubaction.Outputs;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.buildobjects.process.ProcBuilder;
import org.kohsuke.github.GitHub;

public class GitHubAction {

    private static final String INFER_OUT_REPORT_SARIF = "/infer-out/report.sarif";
    private static final String ACTION_NAME = "Infer-Scan";
    private static final String ACTION_DESCRIPTION = "Run Infer static analysis on a GitHub repository";
    private static final String INFER_COMMAND = "infer";

    @Inject
    PrMode prMode;

    @Action
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub, Outputs outputs)
            throws IOException {
        String buildCommand = inputs.get("build-command").orElseThrow();
        List<String> buildCommandArgs = new ArrayList<>();
        buildCommandArgs.addAll(List.of("run", "--sarif", "--"));
        buildCommandArgs.addAll(Arrays.asList(buildCommand.split(" ", -1)));
        try {
            commands.jobSummary("# Infer scan start\n");
            commands.group("## infer build log");
            commands.notice(runInfer(buildCommandArgs));
            commands.endGroup();
            commands.appendJobSummary("## Infer scan completed");
            commands.group("## Infer scan results");
            commands.appendJobSummary(Files.readString(Path.of(context.getGitHubWorkspace(), "/infer-out/report.txt")));
            commands.endGroup();
            if (inputs.getBoolean("use-annotations").orElse(false)) {
                StringReader reader = new StringReader(Files.readString(
                        Path.of(context.getGitHubWorkspace() + INFER_OUT_REPORT_SARIF), StandardCharsets.UTF_8));
                ObjectMapper mapper = new ObjectMapper();
                SarifSchema210 sarif = mapper.readValue(reader, SarifSchema210.class);
                List<Result> results = sarif.getRuns().get(0).getResults();
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

    private String runInfer(List<String> args) {
        return new ProcBuilder(INFER_COMMAND)
                .withArgs(args.toArray(new String[0]))
                .withNoTimeout()
                .run()
                .getOutputString();
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
