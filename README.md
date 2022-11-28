# Infer-java-Action

This action runs the infer static analyzer on a Java project.
The infer static analyzer is a tool for Java, C and Objective-C, see https://fbinfer.com/.

## Usage
```yml
run-infer:
    runs-on: ubuntu-latest
    needs: build
    steps:
        - name: Checkout repository
        uses: actions/checkout@v3
        - name : run infer action
        uses: docker://ghcr.io/martinwitt/infer-train:master
        with:
            build-command: "gradle compileJava"
            use-annotations: "true"
```

Options:

- `build-command` (required): The command to build the project.
- `use-annotations` (optional): Whether to use the GitHub PR annotations. Default: `false`


## Motivation

The infer team sadly does not provide a docker image for Infer. This action is a workaround to run infer in a GitHub action.
Also, it was a great learning experience for me to write a GitHub action.
