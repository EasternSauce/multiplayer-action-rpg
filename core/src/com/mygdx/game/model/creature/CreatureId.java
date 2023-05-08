package com.mygdx.game.model.creature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureId implements Comparable<CreatureId> {
    String value;

    @Override
    public int compareTo(CreatureId o) {
        return getValue().compareTo(o.getValue());
    }
}
