package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.Set;

public interface AbilityChainable extends CreatureRetrievable {
    void chainAbility(Ability chainFromAbility,
                      AbilityType abilityType,
                      Vector2 pos,
                      Vector2 dirVector,
                      MyGdxGame game);

    CreatureId aliveCreatureClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded);

    PhysicsWorld getWorld(AreaId areaId);
}
