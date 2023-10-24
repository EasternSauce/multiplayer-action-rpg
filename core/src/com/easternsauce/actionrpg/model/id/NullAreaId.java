package com.easternsauce.actionrpg.model.id;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.creature.Creature;

public class NullAreaId extends EntityId<Area> {
    private static NullAreaId instance;

    public static NullAreaId of() {
        if (instance == null) {
            instance = new NullAreaId();
        }
        return instance;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}