package com.easternsauce.actionrpg.model.ability;

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
        return getValue().compareTo(o.getValue());
    }
}