package com.easternsauce.actionrpg.model.enemyrallypoint;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class EnemyRallyPointId implements Comparable<EnemyRallyPointId> {
    @Getter
    String value;

    @Override
    public int compareTo(EnemyRallyPointId o) {
        return getValue().compareTo(o.getValue());
    }
}
