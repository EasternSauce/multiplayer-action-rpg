package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.area.Area;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NullAreaId extends EntityId<Area> {
  private static NullAreaId instance;

  public static NullAreaId of() {
    if (instance == null) {
      instance = new NullAreaId();
    }
    return instance;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}