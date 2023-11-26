package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.area.AreaGate;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NullAreaGateId extends EntityId<AreaGate> {
  private static NullAreaGateId instance;

  public static NullAreaGateId of() {
    if (instance == null) {
      instance = new NullAreaGateId();
    }
    return instance;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}