package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;

public interface AbilityRetrievable {

    Ability getAbility(AbilityId abilityId);

    Ability getAbility(CreatureId creatureId, SkillType skillType);


}
