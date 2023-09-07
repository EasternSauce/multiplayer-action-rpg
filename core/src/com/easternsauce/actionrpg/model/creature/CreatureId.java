package com.easternsauce.actionrpg.model.creature;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class CreatureId implements Comparable<CreatureId> {
  @Getter
  String value;

  @Override
  public int compareTo(CreatureId o) {
    return getValue().compareTo(o.getValue());
  }
}
