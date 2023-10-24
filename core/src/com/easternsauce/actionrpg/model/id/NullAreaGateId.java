package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.area.AreaGate;

public class NullAreaGateId extends EntityId<AreaGate> {
  private static NullAreaGateId instance;

  public static NullAreaGateId of() {
    if (instance == null) {
      instance = new NullAreaGateId();
    }
    return instance;
  }

  @Override
  public boolean isNull() {
    return true;
  }
}