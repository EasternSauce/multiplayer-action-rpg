package com.mygdx.game.game.interface_;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;

public interface CreatureUpdatable extends GameUpdatable, CurrentPlayerRetrievable {

    void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector);

    void handleAttackTarget(CreatureId attackingCreatureId,
                            Vector2 vectorTowardsTarget,
                            SkillType skillType);


}
