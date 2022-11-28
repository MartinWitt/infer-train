package io.github.martinwitt.infer;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import com.contrastsecurity.sarif.SarifSchema210;

class RuntimeReflectionRegistrationFeature implements Feature {
  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    try {
      RuntimeReflection.register(SarifSchema210.class.getDeclaredConstructor());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
