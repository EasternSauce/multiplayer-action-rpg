package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.ability.Ability;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NullAbilityId extends EntityId<Ability> {
  private static NullAbilityId instance;

  public static NullAbilityId of() {
    if (instance == null) {
      instance = new NullAbilityId();
    }
    return instance;
  }

  @Override
  public boolean isNull() {
    return true;
  }
}