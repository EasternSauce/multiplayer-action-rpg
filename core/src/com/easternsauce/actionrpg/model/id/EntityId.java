package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.game.entity.Entity;
import lombok.Getter;

public class EntityId<T extends Entity> implements Comparable<EntityId<T>> {
  @Getter
  private final String value;

  protected EntityId() {
    this.value = "null_id";
  }

  protected EntityId(String value) {
    this.value = value;
  }

  public static <U extends Entity> EntityId<U> of(String value) {
    return new EntityId<>(value);
  }

  @Override
  public int compareTo(EntityId o) {
    return getValue().compareTo(o.getValue());
  }

  public boolean isNull() {
    return false;
  }
}
