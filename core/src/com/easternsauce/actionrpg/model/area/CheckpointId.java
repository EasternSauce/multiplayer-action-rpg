package com.easternsauce.actionrpg.model.area;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class CheckpointId implements Comparable<CheckpointId> {
  @Getter
  String value;

  @Override
  public int compareTo(CheckpointId o) {
    return getValue().compareTo(o.getValue());
  }
}
