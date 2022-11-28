package io.github.martinwitt.infer;

import com.contrastsecurity.sarif.ArtifactLocation;
import com.contrastsecurity.sarif.PhysicalLocation;
import com.contrastsecurity.sarif.Region;
import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.SarifSchema210;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
    targets = {SarifSchema210.class, Result.class,PhysicalLocation.class,ArtifactLocation.class,Region.class, 
                SarifSchema210.Version.class})
public class MyReflectionConfiguration {
}
