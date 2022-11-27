package io.github.martinwitt.infer;

import io.quarkiverse.githubaction.Action;

public class GitHubAction {

    @Action
    void doSomething() {
        System.out.println("Hello " + "From GitHub Action");
    }
  }

