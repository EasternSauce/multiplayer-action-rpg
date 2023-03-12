package com.mygdx.game.game.intrface;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;

public interface CreatureUpdatable extends GameUpdatable {

    void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector);

    void handleAttackTarget(CreatureId attackingCreatureId,
                            Vector2 vectorTowardsTarget,
                            SkillType skillType);


}
