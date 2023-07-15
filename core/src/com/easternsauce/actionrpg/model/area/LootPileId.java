package com.easternsauce.actionrpg.model.area;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class LootPileId implements Comparable<LootPileId> {
    @Getter
    private String value;

    @Override
    public int compareTo(LootPileId o) {
        return getValue().compareTo(o.getValue());
    }
}
