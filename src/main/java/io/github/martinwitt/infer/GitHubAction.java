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
            int exitCode = runInfer(List.of("infer", "capture","--sarif", "--", buildCommand));
            commands.appendJobSummary(Files
                    .readString(Path.of(context.getGitHubWorkspace() + " infer-out/output.json")));

        } catch (Exception e) {
            commands.appendJobSummary(e.toString());
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
        outputs.produce("resultfile", context.getGitHubWorkspace() + " infer-out/report.sarif");
        outputs.produce("results_infer", Files.readString(Path.of(context.getGitHubWorkspace() + " infer-out/output.json")));
        System.out.println("Done running Infer ");
        commands.appendJobSummary("Done running Infer");
    }

    private int runInfer(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
       return process.waitFor();
    }
    
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }
  }

