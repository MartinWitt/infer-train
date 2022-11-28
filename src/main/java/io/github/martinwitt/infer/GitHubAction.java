package io.github.martinwitt.infer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Action()
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub, Outputs outputs) throws IOException {
        System.out.println("Hello " + "From GitHub Action");
        String buildCommand = inputs.get("build-command").orElseThrow();
        System.out.println("Build command: " + buildCommand);
        System.out.println("I would run: " + "infer capture - " + buildCommand);
        System.out.println(context);
        try {
            Files.list(Path.of(context.getGitHubWorkspace())).forEach(System.out::println);
            int exitCode = runInfer(List.of("capture","--sarif", "--", "gradle", "compileJava"));
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

    private int runInfer(List<String> args) throws IOException, InterruptedException {
        System.out.println(ProcBuilder.run("infer", args.toArray(new String[0])));

       return 0;
    }
  }

