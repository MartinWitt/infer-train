package io.github.martinwitt.infer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.kohsuke.github.GitHub;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;

public class GitHubAction {

    @Action
    void runInfer(Inputs inputs, Commands commands, Context context, GitHub gitHub) {
        System.out.println("Hello " + "From GitHub Action");
        String buildCommand = inputs.get("build-command").orElseThrow();
        System.out.println("Build command: " + buildCommand);
        System.out.println("I would run: " + "infer capture - " + buildCommand);
        System.out.println(context);
        try {
            int exitCode = runInfer("infer capture -- " + buildCommand);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Done running Infer ");
    }

    private int runInfer(String  command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(new File(System.getProperty(".")));
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

