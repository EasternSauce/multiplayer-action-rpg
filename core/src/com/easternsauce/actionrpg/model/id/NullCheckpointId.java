package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.area.Checkpoint;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NullCheckpointId extends EntityId<Checkpoint> {
  private static NullCheckpointId instance;

  public static NullCheckpointId of() {
    if (instance == null) {
      instance = new NullCheckpointId();
    }
    return instance;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}