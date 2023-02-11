package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.PhysicsWorld;

import java.util.Set;

public interface AbilityChainable extends CreatureRetrievable {
    void chainAbility(Ability chainFromAbility,
                      AbilityType abilityType,
                      Vector2 chainToPos,
                      CreatureId creatureId);

    CreatureId aliveCreatureClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded);

    PhysicsWorld getWorld(AreaId areaId);
}
