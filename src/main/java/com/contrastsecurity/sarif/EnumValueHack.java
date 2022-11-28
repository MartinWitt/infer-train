package com.contrastsecurity.sarif;

import com.contrastsecurity.sarif.SarifSchema210.Version;

/**
 * We use this hack to keep the enum value for jackson deserialization with graal alive.
 */
public class EnumValueHack {

  public com.contrastsecurity.sarif.SarifSchema210.Version getVersion() {
    return Version._2_1_0;
  }

  public Object getLevel() {
    Object error = com.contrastsecurity.sarif.Result.Level.ERROR;
    Object warning = com.contrastsecurity.sarif.Result.Level.WARNING;
    Object note = com.contrastsecurity.sarif.Result.Level.NOTE;
    Object none = com.contrastsecurity.sarif.Result.Level.NONE;
    return null;
  }
}
