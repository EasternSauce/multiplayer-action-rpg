package com.mygdx.game.game;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.Collection;

public interface EnemyAiUpdatable extends CreatureRetrievable, CreaturePosRetrievable {
    Collection<Creature> getCreatures();

    void handleAttackTarget(CreatureId attackingCreatureId,
                            Vector2 vectorTowardsTarget,
                            SkillType skillType);

    PhysicsWorld getPhysicsWorld(AreaId areaId);
}
