package com.mygdx.game.model.area;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileId implements Comparable<LootPileId> {
    String value;

    @Override
    public int compareTo(LootPileId o) {
        return getValue().compareTo(o.getValue());
    }
}
