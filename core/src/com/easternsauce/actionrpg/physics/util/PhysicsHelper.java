package com.easternsauce.actionrpg.physics.util;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.event.*;
import com.easternsauce.actionrpg.util.Constants;

public class PhysicsHelper {

    public static void processPhysicsEventQueue(CoreGame game) {
        game.getPhysicsEventQueue().forEach(physicsEvent -> {
            if (physicsEvent instanceof AbilityHitsCreatureEvent) {
                AbilityHitsCreatureEvent event = (AbilityHitsCreatureEvent) physicsEvent;

                if (game.getGameState().getCreaturesToUpdate().contains(event.getDestinationCreatureId()) &&
                    game.getAbilitiesToUpdate().contains(event.getAbilityId())) {

                    if (event.getSourceCreatureId().equals(event.getDestinationCreatureId())) {
                        Ability ability = game.getGameState().accessAbilities().getAbility(event.getAbilityId());
                        ability.onSelfCreatureHit(game);
                    }
                    else {
                        handleCreatureAttacked(event, game);
                    }

                }
            }
            else if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                Ability ability = game.getGameState().accessAbilities().getAbility(event.getAbilityId());

                if (ability != null && ability.getParams().getState() == AbilityState.ACTIVE) {
                    ability.onTerrainHit(event.getAbilityPos(), event.getTilePos());
                }

            }
            else if (physicsEvent instanceof AbilityHitsAbilityEvent) {
                AbilityHitsAbilityEvent event = (AbilityHitsAbilityEvent) physicsEvent;

                Ability abilityA = game.getGameState().accessAbilities().getAbility(event.getAbilityA_Id());
                Ability abilityB = game.getGameState().accessAbilities().getAbility(event.getAbilityB_Id());

                if (abilityA != null && abilityA.getParams().getState() == AbilityState.ACTIVE) {
                    abilityA.onOtherAbilityHit(event.getAbilityB_Id(), game);
                }
                if (abilityB != null && abilityB.getParams().getState() == AbilityState.ACTIVE) {
                    abilityB.onOtherAbilityHit(event.getAbilityA_Id(), game);
                }
            }
            else if (physicsEvent instanceof CreatureHitsAreaGateEvent) {
                CreatureHitsAreaGateEvent event = (CreatureHitsAreaGateEvent) physicsEvent;

                Creature creature = game.getGameState().accessCreatures().getCreature(event.getCreatureId());

                creature.getParams().getMovementParams().setAreaWhenEnteredGate(creature.getParams().getAreaId());

                if (creature instanceof Player && !creature.getParams().getMovementParams().getIsStillInsideGateAfterTeleport()) {
                    AreaGate areaGate = game.getGameState().getAreaGate(event.getAreaGateId());
                    AreaGate leadingToAreaGate = game.getGameState().getAreaGate(areaGate.getLeadingToAreaGateId());

                    AreaId fromAreaId = areaGate.getAreaId();
                    AreaId toAreaId = leadingToAreaGate.getAreaId();
                    Vector2 pos = leadingToAreaGate.getPos();

                    game.addTeleportEvent(TeleportEvent.of(event.getCreatureId(), pos, fromAreaId, toAreaId, true));

                }
            }
            else if (physicsEvent instanceof CreatureLeavesAreaGateEvent) {

                CreatureLeavesAreaGateEvent event = (CreatureLeavesAreaGateEvent) physicsEvent;

                Creature creature = game.getGameState().accessCreatures().getCreature(event.getCreatureId());

                if (creature instanceof Player && creature.getParams().getMovementParams().getIsStillInsideGateAfterTeleport() &&
                    creature.getParams().getMovementParams().getAreaWhenEnteredGate().equals(creature.getParams().getAreaId())) {

                    creature.getParams().getMovementParams().setIsStillInsideGateAfterTeleport(false);

                }
            }
            else if (physicsEvent instanceof CreatureHitsLootPileEvent) {
                CreatureHitsLootPileEvent event = (CreatureHitsLootPileEvent) physicsEvent;

                PlayerConfig playerConfig = game.getGameState().getPlayerConfig(event.getCreatureId());

                if (playerConfig != null) {
                    playerConfig.getItemPickupMenuLootPiles().add(event.getLootPileId());
                }

            }
            else if (physicsEvent instanceof CreatureLeavesLootPileEvent) {
                CreatureLeavesLootPileEvent event = (CreatureLeavesLootPileEvent) physicsEvent;

                PlayerConfig playerConfig = game.getGameState().getPlayerConfig(event.getCreatureId());

                if (playerConfig != null) {
                    playerConfig.getItemPickupMenuLootPiles().remove(event.getLootPileId());
                }

            }
        });
        game.getPhysicsEventQueue().clear();

    }

    private static void handleCreatureAttacked(AbilityHitsCreatureEvent event, CoreGame game) {
        Creature sourceCreature = game.getGameState().accessCreatures().getCreature(event.getSourceCreatureId());
        Creature destinationCreature = game.getGameState().accessCreatures().getCreature(event.getDestinationCreatureId());
        Ability ability = game.getGameState().accessAbilities().getAbility(event.getAbilityId());

        if (ability != null && destinationCreature.isAlive()) {
            Vector2 contactPoint = calculateContactPoint(destinationCreature, ability);

            if ((sourceCreature instanceof Player || destinationCreature instanceof Player) &&
                !ability.getParams().getCreaturesAlreadyHit().containsKey(event.getDestinationCreatureId())) {

                game
                    .getGameState()
                    .accessAbilities()
                    .onAbilityHitsCreature(event.getSourceCreatureId(),
                                           event.getDestinationCreatureId(),
                                           ability.getParams().getId(),
                                           contactPoint,
                                           game);
            }

        }
    }

    private static Vector2 calculateContactPoint(Creature destinationCreature, Ability ability) {
        Vector2 creaturePos = destinationCreature.getParams().getPos();
        Vector2 abilityPos = ability.getParams().getPos();
        Float creatureRadius = destinationCreature.animationConfig().getSpriteWidth();

        Vector2 contactPoint;
        if (creaturePos.distance(abilityPos) < creatureRadius) {
            contactPoint = creaturePos.midpointTowards(abilityPos);
        }
        else {
            Vector2 hitVector = creaturePos.vectorTowards(abilityPos);
            contactPoint = creaturePos.add(hitVector.normalized().multiplyBy(creatureRadius));
        }
        return contactPoint;
    }

    public static void handleForceUpdateBodyPositions(CoreGame game) {
        if (game.isForceUpdateBodyPositions()) { // only runs after receiving gameState state update
            game.setForceUpdateBodyPositions(false);

            game.getGameState().accessCreatures().getCreatures().forEach((creatureId, creature) -> {
                if (game.getCreatureBodies().containsKey(creatureId) &&
                    game.getCreatureBodies().get(creatureId).getBodyPos().distance(creature.getParams().getPos()) >
                    Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions
                    // are far apart
                ) {
                    game.getCreatureBodies().get(creatureId).trySetTransform(creature.getParams().getPos());
                }
            });

            game.getGameState().accessAbilities().getAbilities().forEach((abilityId, ability) -> {
                //noinspection SpellCheckingInspection
                if (game.getAbilityBodies().containsKey(abilityId) &&
                    game.getAbilityBodies().get(abilityId).getIsBodyInitialized() &&
                    // this is needed to fix body created client/server desync
                    ability.bodyShouldExist() &&
                    game.getAbilityBodies().get(abilityId).getBodyPos().distance(ability.getParams().getPos()) >
                    Constants.FORCE_UPDATE_MINIMUM_DISTANCE
                    // only setTransform if positions are far apart
                ) {
                    game.getAbilityBodies().get(abilityId).trySetTransform(ability.getParams().getPos());
                }
            });
        }
    }
}
