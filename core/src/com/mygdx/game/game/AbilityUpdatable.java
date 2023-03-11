package com.mygdx.game.game;

import com.mygdx.game.game.data.TeleportInfo;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AbilityUpdatable {

    void initAbilityBody(Ability ability);

    Creature getCreature(CreatureId creatureId);

    void chainAbility(Ability chainFromAbility,
                      AbilityType abilityType,
                      Vector2 chainToPos,
                      Vector2 dirVector);

    CreatureId aliveCreatureClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded);

    Vector2 getCreaturePos(CreatureId creatureId);

    Collection<Creature> getCreatures();

    PhysicsWorld getWorld(AreaId areaId);

    Ability getAbility(AbilityId abilityId);

    Ability getAbility(CreatureId creatureId, SkillType skillType);

    List<TeleportInfo> creaturesToTeleport();

    void spawnAbility(AbilityType abilityType,
                      AbilityParams abilityParams);
}
