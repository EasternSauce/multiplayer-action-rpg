package com.easternsauce.actionrpg.model.creature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class DropTableEntry implements Comparable<DropTableEntry> {
  @Getter
  private ItemDrop itemDrop;
  @Getter
  private Float dropChance;

  @Override
  public int compareTo(DropTableEntry o) {
    return toString().compareTo(o.toString());
  }
}
