package io.github.martinwitt.infer;

public enum BuildTools {
    MAVEN("mvn" , "compile"),
    GRADLE("gradle", "compileJava");

  private final String name;
  private final String compileCommand;

    BuildTools(String name, String compileCommand) {
      this.name = name;
      this.compileCommand = compileCommand;
    }

    public String getCommand() {
      return name;
    }
    
    public String getCompileCommand() {
      return compileCommand;
    }
}
