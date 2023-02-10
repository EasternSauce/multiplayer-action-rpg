package com.mygdx.game.game;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;

import java.util.Set;

public interface AbilitySpawnable extends CreatureRetrievable {
    void spawnAbility(AbilityId abilityId,
                      AreaId areaId,
                      CreatureId creatureId,
                      AbilityType abilityType,
                      Set<CreatureId> creaturesAlreadyHit,
                      Vector2 chainFromPos,
                      Vector2 pos,
                      Vector2 dirVector);
}
