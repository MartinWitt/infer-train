package io.github.martinwitt.infer;

import org.kohsuke.github.GitHub;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubaction.Context;
import io.quarkiverse.githubaction.Inputs;

public class GitHubAction {

    @Action
    void doSomething(Inputs inputs, Commands commands, Context context, GitHub gitHub) {
        System.out.println("Hello " + "From GitHub Action");
    }
  }

