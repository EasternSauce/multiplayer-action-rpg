package com.mygdx.game.game.interface_;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GameUpdatable {
    Map<CreatureId, Creature> getCreatures();

    Map<CreatureId, Creature> getRemovedCreatures();

    Creature getCreature(CreatureId creatureId);

    Vector2 getCreaturePos(CreatureId creatureId);

    Map<AbilityId, Ability> getAbilities();

    Ability getAbility(AbilityId abilityId);

    Ability getAbility(CreatureId creatureId, SkillType skillType);

    @SuppressWarnings("unused")
    Vector2 getAbilityPos(AbilityId abilityId);

    void setWorldCameraPosition(float x, float y);

    void setWorldTextCameraPosition(float x, float y);

    void updateCameras();

    boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos);

    void spawnAbility(
            AbilityType abilityType,
            AbilityParams abilityParams);

    PhysicsWorld getPhysicsWorld(AreaId areaId);

    Set<CreatureId> getCreaturesToUpdate();

    Set<AbilityId> getAbilitiesToUpdate();

    List<PhysicsEvent> getPhysicsEventQueue();

    void addTeleportEvent(TeleportEvent teleportEvent);

    Map<CreatureId, CreatureBody> getCreatureBodies();

    Map<AbilityId, AbilityBody> getAbilityBodies();

    boolean isForceUpdateBodyPositions();

    void setForceUpdateBodyPositions(boolean value);

    AreaId getDefaultAreaId();

    PlayerParams getPlayerParams(CreatureId currentPlayerId);

    Set<AreaGate> getAreaGates();

    LootPile getLootPile(LootPileId lootPileId);

    Map<LootPileId, LootPile> getLootPiles();

    Float getTime();

    void onAbilityHitsCreature(CreatureId attackerId,
                               CreatureId targetId,
                               Ability ability);

    Float nextRandomValue();


}
