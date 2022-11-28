package io.github.martinwitt.infer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.buildobjects.process.ProcBuilder;
import org.kohsuke.github.GitHub;
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

    @Action()
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub, Outputs outputs) throws IOException {
        String buildCommand = inputs.get("build-command").orElseThrow();
        List<String> buildCommandArgs = new ArrayList<>();
        buildCommandArgs.addAll(List.of("run","--sarif", "--"));
        buildCommandArgs.addAll(Arrays.asList(buildCommand.split(" ", -1)));
        try {
            commands.appendJobSummary("## Infer scan start");
            commands.appendJobSummary(runInfer(buildCommandArgs));
            commands.appendJobSummary(Files.list(Path.of(context.getGitHubWorkspace() + "/infer-out/")).map(v -> v.toString()).collect(Collectors.joining("\n")));
            commands.appendJobSummary("## Infer scan completed");
            Path.of(context.getGitHubWorkspace()).forEach(System.out::println);
            commands.appendJobSummary(Files
                    .readString(Path.of(context.getGitHubWorkspace() + "/infer-out/report.sarif")));
        } catch (Exception e) {
            commands.jobSummary("## Infer scan failed\n" + e.toString());
            e.printStackTrace();
        }
    }

    private String runInfer(List<String> args)  {
        var string = new ProcBuilder("infer").withArgs(args.toArray(new String[0])).withNoTimeout()
                .run().getOutputString();
       System.out.println(string);
        return string;
    }
  }

