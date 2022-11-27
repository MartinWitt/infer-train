package io.github.martinwitt.infer;

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
    }
  }

