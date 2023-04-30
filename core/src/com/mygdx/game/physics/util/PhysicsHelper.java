package com.mygdx.game.physics.util;

import com.mygdx.game.Constants;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.event.*;

public class PhysicsHelper {

    public static void processPhysicsEventQueue(GameUpdatable game) {
        game.getPhysicsEventQueue().forEach(physicsEvent -> {
            if (physicsEvent instanceof AbilityHitsCreatureEvent) {
                AbilityHitsCreatureEvent event = (AbilityHitsCreatureEvent) physicsEvent;

                if (game.getGameState().getCreaturesToUpdate().contains(event.getDestinationCreatureId()) &&
                        game.getAbilitiesToUpdate().contains(event.getAbilityId())) {

                    if (event.getSourceCreatureId().equals(event.getDestinationCreatureId())) {
                        Ability ability = game.getGameState().getAbility(event.getAbilityId());
                        ability.onThisCreatureHit(game);
                    } else {
                        handleCreatureAttacked(event, game);
                    }

                }
            } else if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                Ability ability = game.getGameState().getAbility(event.getAbilityId());

                if (ability != null && ability.getParams().getState() == AbilityState.ACTIVE) {
                    ability.onTerrainHit(event.getAbilityPos(), event.getTilePos());
                }

            } else if (physicsEvent instanceof AbilityHitsAbilityEvent) {
                AbilityHitsAbilityEvent event = (AbilityHitsAbilityEvent) physicsEvent;

                Ability abilityA = game.getGameState().getAbility(event.getAbilityA_Id());
                Ability abilityB = game.getGameState().getAbility(event.getAbilityB_Id());

                if (abilityA != null && abilityA.getParams().getState() == AbilityState.ACTIVE) {
                    abilityA.onOtherAbilityHit(event.getAbilityB_Id(), game);
                }
                if (abilityB != null && abilityB.getParams().getState() == AbilityState.ACTIVE) {
                    abilityB.onOtherAbilityHit(event.getAbilityA_Id(), game);
                }
            } else if (physicsEvent instanceof CreatureHitsAreaGateEvent) {
                CreatureHitsAreaGateEvent event = (CreatureHitsAreaGateEvent) physicsEvent;

                Creature creature = game.getGameState().getCreature(event.getCreatureId());
                AreaGate areaGate = event.getAreaGate();

                creature.getParams().setAreaWhenEnteredGate(creature.getParams().getAreaId());

                if (creature instanceof Player && !creature.getParams().getJustTeleportedToGate()) {
                    AreaId fromAreaId;
                    AreaId toAreaId;
                    Vector2 pos;

                    AreaId creatureAreaId = creature.getParams().getAreaId();

                    if (creatureAreaId.equals(areaGate.getAreaA_Id())) {
                        fromAreaId = areaGate.getAreaA_Id();
                        toAreaId = areaGate.getAreaB_Id();
                        pos = areaGate.getPosB();
                    } else if (creatureAreaId.equals(areaGate.getAreaB_Id())) {
                        fromAreaId = areaGate.getAreaB_Id();
                        toAreaId = areaGate.getAreaA_Id();
                        pos = areaGate.getPosA();
                    } else {
                        throw new RuntimeException("unreachable");
                    }

                    game.addTeleportEvent(TeleportEvent.of(event.getCreatureId(), pos, fromAreaId, toAreaId));


                }
            } else if (physicsEvent instanceof CreatureLeavesAreaGateEvent) {

                CreatureLeavesAreaGateEvent event = (CreatureLeavesAreaGateEvent) physicsEvent;

                Creature creature = game.getGameState().getCreature(event.getCreatureId());

                if (creature instanceof Player &&
                        creature.getParams().getJustTeleportedToGate() &&
                        creature.getParams().getAreaWhenEnteredGate().equals(creature.getParams().getAreaId())) {


                    creature.getParams().setJustTeleportedToGate(false);

                }
            } else if (physicsEvent instanceof CreatureHitsLootPileEvent) {
                CreatureHitsLootPileEvent event = (CreatureHitsLootPileEvent) physicsEvent;

                //                LootPile lootPile = game.getLootPile(event.getLootPileId());

                //                if (lootPile != null) lootPile.isLooted(true);

                PlayerParams playerParams = game.getGameState().getPlayerParams(event.getCreatureId());

                if (playerParams != null) {
                    playerParams.getItemPickupMenuLootPiles().add(event.getLootPileId());
                }

            } else if (physicsEvent instanceof CreatureLeavesLootPileEvent) {
                CreatureLeavesLootPileEvent event = (CreatureLeavesLootPileEvent) physicsEvent;

                PlayerParams playerParams = game.getGameState().getPlayerParams(event.getCreatureId());

                if (playerParams != null) {
                    playerParams.getItemPickupMenuLootPiles().remove(event.getLootPileId());
                }

            }
        });
        game.getPhysicsEventQueue().clear();

    }

    private static void handleCreatureAttacked(AbilityHitsCreatureEvent event, GameUpdatable game) {

        Creature sourceCreature = game.getGameState().getCreature(event.getSourceCreatureId());
        Creature destinationCreature = game.getGameState().getCreature(event.getDestinationCreatureId());
        Ability ability = game.getGameState().getAbility(event.getAbilityId());

        if (ability != null && destinationCreature.isAlive()) {
            if ((sourceCreature instanceof Player || destinationCreature instanceof Player) &&
                    !ability.getParams().getCreaturesAlreadyHit().containsKey(event.getDestinationCreatureId())) {

                game.onAbilityHitsCreature(event.getSourceCreatureId(), event.getDestinationCreatureId(), ability);
            }


        }
    }

    public static void handleForceUpdateBodyPositions(GameUpdatable game) {
        if (game.isForceUpdateBodyPositions()) { // only runs after receiving game state update
            game.setForceUpdateBodyPositions(false);

            game.getGameState().getCreatures().forEach((creatureId, creature) -> {
                if (game.getCreatureBodies().containsKey(creatureId) &&
                        game.getCreatureBodies().get(creatureId).getBodyPos().distance(creature.getParams().getPos()) >
                                Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions are far apart
                ) {
                    game.getCreatureBodies().get(creatureId).trySetTransform(creature.getParams().getPos());
                }
            });

            game.getGameState().getAbilities().forEach((abilityId, ability) -> {
                if (game.getAbilityBodies().containsKey(abilityId) &&
                        game.getAbilityBodies().get(abilityId).getIsBodyInitialized() &&
                        // this is needed to fix body created client/server desync
                        ability.bodyShouldExist() &&
                        game.getAbilityBodies().get(abilityId).getBodyPos().distance(ability.getParams().getPos()) >
                                Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions are far apart
                ) {
                    game.getAbilityBodies().get(abilityId).trySetTransform(ability.getParams().getPos());
                }
            });
        }
    }
}
