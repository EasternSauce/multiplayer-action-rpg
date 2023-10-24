package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.creature.Creature;

public class NullCheckpointId extends EntityId<Checkpoint> {
    private static NullCheckpointId instance;

    public static NullCheckpointId of() {
        if (instance == null) {
            instance = new NullCheckpointId();
        }
        return instance;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}