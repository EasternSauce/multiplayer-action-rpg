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

                if (game.getCreaturesToUpdate().contains(event.destinationCreatureId()) &&
                    game.getAbilitiesToUpdate().contains(event.abilityId())) {

                    if (event.sourceCreatureId().equals(event.destinationCreatureId())) {
                        Ability ability = game.getAbility(event.abilityId());
                        ability.onThisCreatureHit(game);
                    }
                    else {
                        handleCreatureAttacked(event, game);
                    }

                }
            }
            else if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                Ability ability = game.getAbility(event.abilityId());

                if (ability != null && ability.params().state() == AbilityState.ACTIVE) {
                    ability.onTerrainHit(event.abilityPos(), event.tilePos());
                }

            }
            else if (physicsEvent instanceof AbilityHitsAbilityEvent) {
                AbilityHitsAbilityEvent event = (AbilityHitsAbilityEvent) physicsEvent;

                Ability abilityA = game.getAbility(event.abilityA_Id());
                Ability abilityB = game.getAbility(event.abilityB_Id());

                if (abilityA != null && abilityA.params().state() == AbilityState.ACTIVE) {
                    abilityA.onOtherAbilityHit(event.abilityB_Id(), game);
                }
                if (abilityB != null && abilityB.params().state() == AbilityState.ACTIVE) {
                    abilityB.onOtherAbilityHit(event.abilityA_Id(), game);
                }
            }
            else if (physicsEvent instanceof CreatureHitsAreaGateEvent) {
                CreatureHitsAreaGateEvent event = (CreatureHitsAreaGateEvent) physicsEvent;

                Creature creature = game.getCreature(event.creatureId());
                AreaGate areaGate = event.areaGate();

                creature.params().areaWhenEnteredGate(creature.params().areaId());

                if (creature instanceof Player && !creature.params().justTeleportedToGate()) {
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

                    game.addTeleportEvent(TeleportEvent.of(event.creatureId(), pos, fromAreaId, toAreaId));


                }
            }
            else if (physicsEvent instanceof CreatureLeavesAreaGateEvent) {

                CreatureLeavesAreaGateEvent event = (CreatureLeavesAreaGateEvent) physicsEvent;

                Creature creature = game.getCreature(event.creatureId());

                if (creature instanceof Player &&
                    creature.params().justTeleportedToGate() &&
                    creature.params().areaWhenEnteredGate().equals(creature.params().areaId())) {


                    creature.params().justTeleportedToGate(false);

                }
            }
            else if (physicsEvent instanceof CreatureHitsLootPileEvent) {
                CreatureHitsLootPileEvent event = (CreatureHitsLootPileEvent) physicsEvent;

                //                LootPile lootPile = game.getLootPile(event.lootPileId());

                //                if (lootPile != null) lootPile.isLooted(true);

                PlayerParams playerParams = game.getPlayerParams(event.creatureId());

                if (playerParams != null) {
                    playerParams.itemPickupMenuLootPiles().add(event.lootPileId());
                }

            }

            else if (physicsEvent instanceof CreatureLeavesLootPileEvent) {
                CreatureLeavesLootPileEvent event = (CreatureLeavesLootPileEvent) physicsEvent;

                PlayerParams playerParams = game.getPlayerParams(event.creatureId());

                if (playerParams != null) {
                    playerParams.itemPickupMenuLootPiles().remove(event.lootPileId());
                }

            }
        });
        game.getPhysicsEventQueue().clear();

    }

    private static void handleCreatureAttacked(AbilityHitsCreatureEvent event, GameUpdatable game) {

        Creature sourceCreature = game.getCreature(event.sourceCreatureId());
        Creature destinationCreature = game.getCreature(event.destinationCreatureId());
        Ability ability = game.getAbility(event.abilityId());

        if (ability != null && destinationCreature.isAlive()) {
            if ((sourceCreature instanceof Player || destinationCreature instanceof Player) &&
                !ability.params().creaturesAlreadyHit().containsKey(event.destinationCreatureId())) {
                destinationCreature.handleBeingAttacked(ability.isRanged(),
                                                        ability.params().dirVector(),
                                                        ability.params().currentDamage(),
                                                        event.sourceCreatureId(),
                                                        game);

                ability.params()
                       .creaturesAlreadyHit()
                       .put(event.destinationCreatureId(), ability.params().stateTimer().time());
                ability.onCreatureHit();
            }


        }
    }

    public static void handleForceUpdateBodyPositions(GameUpdatable game) {
        if (game.isForceUpdateBodyPositions()) { // only runs after receiving game state update
            game.setForceUpdateBodyPositions(false);

            game.getCreatures().forEach((creatureId, creature) -> {
                if (game.getCreatureBodies().containsKey(creatureId) &&
                    game.getCreatureBodies().get(creatureId).getBodyPos().distance(creature.params().pos()) >
                    Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions are far apart
                ) {
                    game.getCreatureBodies().get(creatureId).trySetTransform(creature.params().pos());
                }
            });

            game.getAbilities().forEach((abilityId, ability) -> {
                if (game.getAbilityBodies().containsKey(abilityId) &&
                    game.getAbilityBodies().get(abilityId).isBodyInitialized() &&
                    // this is needed to fix body created client/server desync
                    ability.bodyShouldExist() &&
                    game.getAbilityBodies().get(abilityId).getBodyPos().distance(ability.params().pos()) >
                    Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions are far apart
                ) {
                    game.getAbilityBodies().get(abilityId).trySetTransform(ability.params().pos());
                }
            });
        }
    }
}
