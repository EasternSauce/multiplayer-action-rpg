package com.easternsauce.actionrpg.model.enemyrallypoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyRallyPointId implements Comparable<EnemyRallyPointId> {
    String value;

    @Override
    public int compareTo(EnemyRallyPointId o) {
        return getValue().compareTo(o.getValue());
    }
}
