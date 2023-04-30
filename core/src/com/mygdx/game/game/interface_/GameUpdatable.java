package com.mygdx.game.game.interface_;

import com.mygdx.game.game.entity.EntityEventProcessor;
import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.screen.ConnectScreenMessageHolder;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityParams;
import com.mygdx.game.model.ability.AbilityType;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface GameUpdatable {

    void updateCameraPositions();

    boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos);

    void spawnAbility(AbilityType abilityType, AbilityParams abilityParams);

    PhysicsWorld getPhysicsWorld(AreaId areaId);

    Set<AbilityId> getAbilitiesToUpdate();

    List<PhysicsEvent> getPhysicsEventQueue();

    void addTeleportEvent(TeleportEvent teleportEvent);

    Map<CreatureId, CreatureBody> getCreatureBodies();

    Map<AbilityId, AbilityBody> getAbilityBodies();

    boolean isForceUpdateBodyPositions();

    void setForceUpdateBodyPositions(boolean value);

    void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, Ability ability);

    void forEachAliveCreature(Consumer<Creature> creatureAction);

    void forEachDeadCreature(Consumer<Creature> creatureAction);

    EntityEventProcessor getEventProcessor();

    void setChatInputProcessor();

    void setConnectScreenInputProcessor(ConnectScreenMessageHolder messageHolder);

    GameState getGameState();

    AreaId getCurrentAreaId();
}
