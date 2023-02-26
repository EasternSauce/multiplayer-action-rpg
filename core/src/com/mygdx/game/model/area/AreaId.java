package com.mygdx.game.model.area;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaId implements Comparable<AreaId> {
    String value;

    @Override
    public int compareTo(AreaId o) {
        return value().compareTo(o.value());
    }
}
