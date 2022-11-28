package io.github.martinwitt.infer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.buildobjects.process.ProcBuilder;
import org.kohsuke.github.GitHub;
import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.SarifSchema210;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;
import io.quarkiverse.githubaction.Outputs;

public class GitHubAction {
    public static final String ACTION_NAME = "Infer-Scan";
    public static final String ACTION_DESCRIPTION =
            "Run Infer static analysis on a GitHub repository";
    private static final String INFER_COMMAND = "infer";

    @Action
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub, Outputs outputs) throws IOException {
        String buildCommand = inputs.get("build-command").orElseThrow();
        List<String> buildCommandArgs = new ArrayList<>();
        buildCommandArgs.addAll(List.of("run","--sarif", "--"));
        buildCommandArgs.addAll(Arrays.asList(buildCommand.split(" ", -1)));
        try {
            commands.appendJobSummary("## Infer scan start\n");
            commands.appendJobSummary(runInfer(buildCommandArgs));
            // commands.appendJobSummary(Files.list(Path.of(context.getGitHubWorkspace() + "/infer-out/")).map(v -> v.toString()).collect(Collectors.joining("\n")));
            commands.appendJobSummary("## Infer scan completed");
            if (inputs.getBoolean("use-annotations").orElse(false)) {
                StringReader reader = new StringReader(Files.readString(
                        Path.of(context.getGitHubWorkspace() + "/infer-out/report.sarif")));

                ObjectMapper mapper = new ObjectMapper();
                SarifSchema210 sarif = mapper.readValue(reader, SarifSchema210.class);
                for (var result : sarif.getRuns().get(0).getResults()) {
                    result.getLocations().get(0).getPhysicalLocation().getArtifactLocation();
                    String fileName = getFilePathFromResult(result);
                    String message = result.getMessage().getText();
                    int startLine = result.getLocations().get(0).getPhysicalLocation().getRegion()
                            .getStartLine();
                    int startColumn = result.getLocations().get(0).getPhysicalLocation().getRegion()
                            .getStartColumn();
                    commands.warning(message, 
                            result.getRuleId(), 
                            fileName, 
                            startLine, 
                            null, 
                            startColumn, 
                            null);
                } 
  
                    }
        } catch (Exception e) {
            commands.appendJobSummary("## Infer scan failed\n" + e.toString());
            e.printStackTrace();
        }
    }

    private String runInfer(List<String> args) {
        var string = new ProcBuilder("infer").withArgs(args.toArray(new String[0])).withNoTimeout()
                .run().getOutputString();
        System.out.println(string);
        return string;
    }
    
    private static String getFilePathFromResult(Result result) {
        return result.getLocations().get(0).getPhysicalLocation().getArtifactLocation().getUri()
                .replace("file:", "");
    }
  }

