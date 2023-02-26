package com.mygdx.game.model.ability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityId implements Comparable<AbilityId> {
    String value;

    @Override
    public int compareTo(AbilityId o) {
        return value().compareTo(o.value());
    }
}
