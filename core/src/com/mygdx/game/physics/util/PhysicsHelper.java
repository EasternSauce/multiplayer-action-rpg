package com.mygdx.game.physics.util;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.game.data.AreaGate;
import com.mygdx.game.game.data.TeleportInfo;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.event.*;

public class PhysicsHelper {

    public static void processPhysicsEventQueue(MyGdxGame game) {
        game.physics().physicsEventQueue().forEach(physicsEvent -> {
            if (physicsEvent instanceof AbilityHitsCreatureEvent) {
                AbilityHitsCreatureEvent event = (AbilityHitsCreatureEvent) physicsEvent;

                Creature attackedCreature = game.gameState().creatures().get(event.attackedCreatureId());

                Creature attackingCreature = game.gameState().creatures().get(event.attackingCreatureId());

                boolean attackedIsPlayer = (attackedCreature instanceof Player);
                boolean attackingIsPlayer = (attackingCreature instanceof Player);

                Ability ability = game.gameState().abilities().get(event.abilityId());

                if (game.creaturesToUpdate().contains(event.attackedCreatureId()) && // TODO: refactor
                    game.abilitiesToUpdate().contains(event.abilityId())) {
                    if (event.attackingCreatureId().equals(event.attackedCreatureId())) {
                        ability.onThisCreatureHit(game);
                    }
                    else {
                        handleCreatureAttacked(event,
                                               attackedCreature,
                                               attackedIsPlayer,
                                               attackingIsPlayer,
                                               ability,
                                               game);
                    }

                }
            }
            else if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                Ability ability = game.gameState().abilities().get(event.abilityId());

                if (ability != null && ability.params().state() == AbilityState.ACTIVE) {
                    ability.onTerrainHit(event.tileCenter(), game);
                }

            }
            else if (physicsEvent instanceof AbilityHitsAbilityEvent) {
                AbilityHitsAbilityEvent event = (AbilityHitsAbilityEvent) physicsEvent;

                Ability abilityA = game.gameState().abilities().get(event.abilityA_Id());
                Ability abilityB = game.gameState().abilities().get(event.abilityB_Id());

                if (abilityA != null && abilityA.params().state() == AbilityState.ACTIVE) {
                    abilityA.onAbilityHit(event.abilityB_Id(), game);
                }
                if (abilityB != null && abilityB.params().state() == AbilityState.ACTIVE) {
                    abilityB.onAbilityHit(event.abilityA_Id(), game);
                }
            }
            else if (physicsEvent instanceof CreatureHitsAreaGateEvent) {
                CreatureHitsAreaGateEvent event = (CreatureHitsAreaGateEvent) physicsEvent;

                Creature creature = game.getCreature(event.creatureId());
                AreaGate areaGate = event.areaGate();

                if (creature instanceof Player && !creature.params().isInsideGate()) {
                    AreaId fromAreaId;
                    AreaId toAreaId;
                    Vector2 pos;

                    AreaId creatureAreaId = creature.params().areaId();

                    if (creatureAreaId.equals(areaGate.areaA_Id())) {
                        fromAreaId = areaGate.areaA_Id();
                        toAreaId = areaGate.areaB_Id();
                        pos = areaGate.posB();
                    }
                    else if (creatureAreaId.equals(areaGate.areaB_Id())) {
                        fromAreaId = areaGate.areaB_Id();
                        toAreaId = areaGate.areaA_Id();
                        pos = areaGate.posA();
                    }
                    else {
                        throw new RuntimeException("unreachable");
                    }

                    game.creaturesToTeleport().add(TeleportInfo.of(event.creatureId(), pos, toAreaId));

                    creature.params().isInsideGate(true);
                }
            }
            else if (physicsEvent instanceof CreatureLeavesAreaGateEvent) {
                // TODO
            }
        });
        game.physics().physicsEventQueue().clear();

    }

    private static void handleCreatureAttacked(AbilityHitsCreatureEvent event,
                                               Creature attackedCreature,
                                               boolean attackedIsPlayer,
                                               boolean attackingIsPlayer,
                                               Ability ability,
                                               MyGdxGame game) {
        if (ability != null && attackedCreature.isAlive()) {
            if ((attackedIsPlayer || attackingIsPlayer) &&
                !ability.params().creaturesAlreadyHit().containsKey(event.attackedCreatureId())) {
                attackedCreature.handleBeingAttacked(ability.isRanged(),
                                                     ability.params().dirVector(),
                                                     ability.params().currentDamage(),
                                                     event.attackingCreatureId(),
                                                     game);

                ability.params()
                       .creaturesAlreadyHit()
                       .put(event.attackedCreatureId(), ability.params().stateTimer().time());
                ability.onCreatureHit();
            }


        }
    }

    public static void handleForceUpdateBodyPositions(MyGdxGame game) {
        if (game.physics().forceUpdateBodyPositions()) { // only runs after receiving game state update
            game.physics().forceUpdateBodyPositions(false);

            game.gameState().creatures().forEach((creatureId, creature) -> {
                if (game.physics().creatureBodies().containsKey(creatureId) &&
                    game.physics().creatureBodies().get(creatureId).getBodyPos().distance(creature.params().pos()) >
                    0.05f // only setTransform if positions are far apart
                ) {
                    game.physics().creatureBodies().get(creatureId).trySetTransform(creature.params().pos());
                }
            });

            game.gameState().abilities().forEach((abilityId, ability) -> {
                if (game.physics().abilityBodies().containsKey(abilityId) &&
                    game.physics().abilityBodies().get(abilityId).isBodyInitialized() &&
                    // this is needed to fix body created client/server desync
                    ability.bodyShouldExist() &&
                    game.physics().abilityBodies().get(abilityId).getBodyPos().distance(ability.params().pos()) >
                    0.05f // only setTransform if positions are far apart
                ) {
                    game.physics().abilityBodies().get(abilityId).trySetTransform(ability.params().pos());
                }
            });
        }
    }
}
