package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.creature.Creature;

public class NullCreatureId extends EntityId<Creature> {
  private static NullCreatureId instance;

  public static NullCreatureId of() {
    if (instance == null) {
      instance = new NullCreatureId();
    }
    return instance;
  }

  @Override
  public boolean isNull() {
    return true;
  }
}