package io.github.martinwitt.infer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Main {

  public static String runInfer(String command, String repoPath) throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(command);
    builder.directory(new File(repoPath));
    Process process = builder.start();
    StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
    Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
    process.waitFor();
    return Files.readString(Path.of(repoPath, "/infer-out/output.json"));
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
