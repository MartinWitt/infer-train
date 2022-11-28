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
        buildCommandArgs.addAll(List.of("capture","--sarif", "--"));
        buildCommandArgs.addAll(Arrays.asList(buildCommand.split(" ", -1)));
        try {
            runInfer(buildCommandArgs);
            Path.of(context.getGitHubWorkspace()).forEach(System.out::println);
            commands.appendJobSummary(Files
                    .readString(Path.of(context.getGitHubWorkspace() + " infer-out/output.json")));

        } catch (Exception e) {
            commands.appendJobSummary(e.toString());
            e.printStackTrace();
        }
        outputs.produce("resultfile", context.getGitHubWorkspace() + " infer-out/report.sarif");
        outputs.produce("results_infer", Files.readString(Path.of(context.getGitHubWorkspace() + " infer-out/output.json")));
        System.out.println("Done running Infer ");
        commands.appendJobSummary("Done running Infer");
    }

    private int runInfer(List<String> args)  {
        new ProcBuilder("infer").withArgs(args.toArray(new String[0])).withNoTimeout()
                .withOutputStream(System.out).run();
       return 0;
    }
  }

