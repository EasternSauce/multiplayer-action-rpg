package com.mygdx.game.game;

import com.mygdx.game.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.PhysicsWorld;
import com.mygdx.game.util.Vector2;

import java.util.Collection;

public interface EnemyAiUpdatable extends CreatureRetrievable {
    Collection<Creature> getCreatures();

    void handleAttackTarget(CreatureId attackingCreatureId,
                            Vector2 vectorTowardsTarget,
                            AbilityType abilityType);

    AreaId getCurrentAreaId();

    PhysicsWorld getPhysicsWorld(AreaId areaId);
}
