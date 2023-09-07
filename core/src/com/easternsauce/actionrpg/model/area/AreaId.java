package com.easternsauce.actionrpg.model.area;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class AreaId implements Comparable<AreaId> {
  @Getter
  String value;

  @Override
  public int compareTo(AreaId o) {
    return getValue().compareTo(o.getValue());
  }
}
