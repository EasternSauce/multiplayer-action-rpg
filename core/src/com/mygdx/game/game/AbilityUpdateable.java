package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.creature.Creature;

import java.util.Collection;

public interface AbilityUpdateable extends AbilityChainable, CreaturePosRetrievable {

    void initAbilityBody(Ability ability);

    Collection<Creature> getCreatures();
}
