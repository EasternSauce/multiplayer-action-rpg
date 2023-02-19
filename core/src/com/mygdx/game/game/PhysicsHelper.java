package com.mygdx.game.game;

import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.physics.event.AbilityHitsCreatureEvent;
import com.mygdx.game.physics.event.AbilityHitsTerrainEvent;

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

                if (game.creaturesToUpdate().contains(event.attackedCreatureId()) && game.abilitiesToUpdate()
                                                                                         .contains(event.abilityId())) {
                    handleCreatureAttacked(event, attackedCreature, attackedIsPlayer, attackingIsPlayer, ability);
                }
            }
            if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                Ability ability = game.gameState().abilities().get(event.abilityId());

                if (ability != null) {
                    ability.onTerrainHit();
                }

            }
        });
        game.physics().physicsEventQueue().clear();

    }

    private static void handleCreatureAttacked(AbilityHitsCreatureEvent event,
                                               Creature attackedCreature,
                                               boolean attackedIsPlayer,
                                               boolean attackingIsPlayer,
                                               Ability ability) {
        if (ability != null && attackedCreature.isAlive()) {
            if ((attackedIsPlayer || attackingIsPlayer) && !ability.params()
                                                                   .creaturesAlreadyHit()
                                                                   .contains(event.attackedCreatureId())) {
                attackedCreature.handleBeingAttacked(ability.isRanged(),
                                                     ability.params().damage(),
                                                     event.attackingCreatureId());
            }

            ability.params().creaturesAlreadyHit().add(event.attackedCreatureId());
            ability.onCreatureHit();
        }
    }

    public static void handleForceUpdateBodyPositions(MyGdxGame game) {
        if (game.physics().forceUpdateBodyPositions()) { // only runs after receiving game state update
            game.physics().forceUpdateBodyPositions(false);

            game.gameState().creatures().forEach((creatureId, creature) -> {
                if (game.physics().creatureBodies().containsKey(creatureId) && game.physics()
                                                                                   .creatureBodies()
                                                                                   .get(creatureId)
                                                                                   .getBodyPos()
                                                                                   .distance(creature.params()
                                                                                                     .pos()) >
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
                    game.physics()
                        .abilityBodies()
                        .get(abilityId)
                        .getBodyPos()
                        .distance(
                                ability.params()
                                       .pos()) >
                    0.05f // only setTransform if positions are far apart
                ) {
                    game.physics().abilityBodies().get(abilityId).trySetTransform(ability.params().pos());
                }
            });
        }
    }
}
