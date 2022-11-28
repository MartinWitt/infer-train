package com.contrastsecurity.sarif;

import com.contrastsecurity.sarif.SarifSchema210.Version;

/**
 * We use this hack to keep the enum value for jackson deserialization with graal alive.
 */
public class EnumValueHack {

  public com.contrastsecurity.sarif.SarifSchema210.Version getVersion() {
    return Version._2_1_0;
  }
}
