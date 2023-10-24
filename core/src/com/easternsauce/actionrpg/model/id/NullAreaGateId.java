package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.creature.Creature;

public class NullAreaGateId extends EntityId<AreaGate> {
    private static NullAreaGateId instance;

    public static NullAreaGateId of() {
        if (instance == null) {
            instance = new NullAreaGateId();
        }
        return instance;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}