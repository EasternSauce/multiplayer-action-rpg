package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.game.entity.Entity;
import lombok.Getter;

import java.util.Objects;

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

  public static <U extends Entity> EntityId<U> of(EntityId<U> other) {
    return new EntityId<>(other.value);
  }

  @Override
  public int compareTo(EntityId o) {
    return getValue().compareTo(o.getValue());
  }

  public boolean isNull() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityId)) return false;
    EntityId<?> entityId = (EntityId<?>) o;
    return value.equals(entityId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
