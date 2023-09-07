package com.easternsauce.actionrpg.model.ability;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class AbilityId implements Comparable<AbilityId> {
  @Getter
  String value;

  @Override
  public int compareTo(AbilityId o) {
    return getValue().compareTo(o.getValue());
  }
}
