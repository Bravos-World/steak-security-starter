package com.bravos.steak.security.starter.model;

import lombok.Getter;

public enum Scope {

  OWN((byte) 1),
  TENANT((byte) 2),
  ALL((byte) 3),
  NONE((byte) 0)
  ;

  @Getter
  private final byte value;

  Scope(byte i) {
    this.value = i;
  }

}
