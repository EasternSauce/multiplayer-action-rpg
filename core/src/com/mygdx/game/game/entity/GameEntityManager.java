package com.mygdx.game.game.entity;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.body.LootPileBody;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.LootPileRenderer;
import com.mygdx.game.renderer.creature.CreatureRenderer;
import com.mygdx.game.renderer.game.GameRenderer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
public class GameEntityManager {
    @Getter
    private final GameRenderer gameRenderer = GameRenderer.of();
    @Getter
    private final GamePhysics gamePhysics = GamePhysics.of();

    public void createCreatureEntity(CreatureId creatureId, GameUpdatable game) {
        Creature creature = game.getCreatures().get(creatureId);

        if (creature != null) {
            if (!gameRenderer.getCreatureRenderers().containsKey(creatureId)) {
                CreatureRenderer creatureRenderer = CreatureRenderer.of(creatureId);
                creatureRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getCreatureRenderers().put(creatureId, creatureRenderer);
            }
            if (!gamePhysics.getCreatureBodies().containsKey(creatureId)) {
                CreatureBody creatureBody = CreatureBody.of(creatureId);
                creatureBody.init(game, creature.getParams().getAreaId());
                gamePhysics.getCreatureBodies().put(creatureId, creatureBody);
            }
        }
    }

    public void createAbilityEntity(AbilityId abilityId, GameUpdatable game) {
        Ability ability = game.getAbilities().get(abilityId);

        if (ability != null) {

            if (!gameRenderer.getAbilityRenderers().containsKey(abilityId)) {
                AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
                abilityRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getAbilityRenderers().put(abilityId, abilityRenderer);
            }
            if (!gamePhysics.getAbilityBodies().containsKey(abilityId)) {
                AbilityBody abilityBody = AbilityBody.of(abilityId);
                if (ability.getParams().getState() == AbilityState.ACTIVE) {
                    abilityBody.init(game, ability.getParams().getIsSkipCreatingBody());
                }
                gamePhysics.getAbilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void activateAbility(AbilityId abilityId, GameUpdatable game) {
        Ability ability = game.getAbilities().get(abilityId);

        if (ability != null && gamePhysics.getAbilityBodies().containsKey(ability.getParams().getId())) {
            gamePhysics.getAbilityBodies()
                       .get(ability.getParams().getId())
                       .init(game, ability.getParams().getIsSkipCreatingBody());
        }
    }

    public void createLootPileEntity(LootPileId lootPileId, GameUpdatable game) {
        LootPile lootPile = game.getLootPile(lootPileId);

        if (lootPile != null) {
            if (!gameRenderer.getLootPileRenderers().containsKey(lootPileId)) {
                LootPileRenderer lootPileRenderer = LootPileRenderer.of(lootPileId);
                lootPileRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getLootPileRenderers().put(lootPileId, lootPileRenderer);
            }
            if (!gamePhysics.getLootPileBodies().containsKey(lootPileId)) {
                LootPileBody lootPileBody = LootPileBody.of(lootPileId);
                lootPileBody.init(game);
                gamePhysics.getLootPileBodies().put(lootPileId, lootPileBody);
            }
        }
    }

    public void removeCreatureEntity(CreatureId creatureId, GameUpdatable game) {
        if (creatureId != null) {
            game.getCreatures().remove(creatureId);

            getGameRenderer().getCreatureRenderers().remove(creatureId);

            if (gamePhysics.getCreatureBodies().containsKey(creatureId)) {
                gamePhysics.getCreatureBodies().get(creatureId).onRemove();
                gamePhysics.getCreatureBodies().remove(creatureId);
            }
        }
    }

    public void removeAbilityEntity(AbilityId abilityId, GameUpdatable game) {

        if (abilityId != null) {

            game.getAbilities().remove(abilityId);

            getGameRenderer().getAbilityRenderers().remove(abilityId);

            if (gamePhysics.getAbilityBodies().containsKey(abilityId)) {
                gamePhysics.getAbilityBodies().get(abilityId).onRemove();
                gamePhysics.getAbilityBodies().remove(abilityId);
            }
        }
    }

    public void removeLootPileEntity(LootPileId lootPileId, GameUpdatable game) {
        if (lootPileId != null) {

            game.getLootPiles().remove(lootPileId);

            getGameRenderer().getLootPileRenderers().remove(lootPileId);

            if (gamePhysics.getLootPileBodies().containsKey(lootPileId)) {
                gamePhysics.getLootPileBodies().get(lootPileId).onRemove();
                gamePhysics.getLootPileBodies().remove(lootPileId);
            }
        }
    }

    public void spawnEnemy(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn, GameUpdatable game) {
        game.getCreatures()
            .put(creatureId,
                 Enemy.of(CreatureParams.of(creatureId, areaId, enemySpawn)
                                        .setBaseSpeed(7f)
                                        .setAttackDistance(enemySpawn.getEnemyTemplate().getAttackDistance())
                                        .setMainAttackSkill(enemySpawn.getEnemyTemplate().getMainAttackSkill())
                                        .setDropTable(enemySpawn.getEnemyTemplate().getDropTable())));

        game.getEventProcessor().getCreatureModelsToBeCreated().add(creatureId);


    }

    public void updateCreatures(float delta, CoreGame game) {
        Set<CreatureId> creaturesToUpdate = game.getCreaturesToUpdate();

        creaturesToUpdate.forEach(creatureId -> {
            if (getGamePhysics().getCreatureBodies().containsKey(creatureId)) {
                getGamePhysics().getCreatureBodies().get(creatureId).update(game);
            }
        });

        // set gamestate position based on b2body position
        creaturesToUpdate.forEach(creatureId -> {
            if (game.getCreatures().containsKey(creatureId) &&
                getGamePhysics().getCreatureBodies().containsKey(creatureId)) {

                game.getCreatures()
                    .get(creatureId)
                    .getParams()
                    .setPos(getGamePhysics().getCreatureBodies().get(creatureId).getBodyPos());

            }
        });

        // if creature is to be updated, then body should be active, otherwise it should be inactive
        getGamePhysics().getCreatureBodies()
                        .forEach((key, value) -> getGamePhysics().getCreatureBodies()
                                                                 .get(key)
                                                                 .setActive(creaturesToUpdate.contains(key)));

        creaturesToUpdate.forEach(creatureId -> {
            if (game.getCreatures().containsKey(creatureId) &&
                getGameRenderer().getCreatureRenderers().containsKey(creatureId)) {
                getGameRenderer().getCreatureRenderers().get(creatureId).update(game);
            }
        });

        creaturesToUpdate.forEach(creatureId -> {
            if (game.getCreatures().containsKey(creatureId)) {
                game.getCreatures().get(creatureId).update(delta, game);
            }
        });

    }

    public void updateAbilities(float delta, CoreGame game) {
        Set<AbilityId> abilitiesToUpdate = game.getAbilitiesToUpdate();

        abilitiesToUpdate.forEach(abilityId -> game.getAbilities().get(abilityId).update(delta, game));


        abilitiesToUpdate.forEach(abilityId -> {
            if (getGamePhysics().getAbilityBodies().containsKey(abilityId)) {
                getGamePhysics().getAbilityBodies().get(abilityId).update(game);
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (getGamePhysics().getAbilityBodies().containsKey(abilityId)) {
                Ability ability = game.getAbilities().get(abilityId);
                if (!ability.isPositionChangedOnUpdate() &&
                    ability.bodyShouldExist() &&
                    getGamePhysics().getAbilityBodies().get(abilityId).getIsBodyInitialized()) {
                    ability.getParams().setPos(getGamePhysics().getAbilityBodies().get(abilityId).getBodyPos());
                }
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (getGameRenderer().getAbilityRenderers().containsKey(abilityId)) {
                getGameRenderer().getAbilityRenderers().get(abilityId).update(game);
            }
        });

    }

    public void teleportCreature(TeleportEvent teleportEvent, GameUpdatable game) {
        if (teleportEvent.getToAreaId()
                         .equals(game.getCreature(teleportEvent.getCreatureId()).getParams().getAreaId())) {
            getGamePhysics().getCreatureBodies()
                            .get(teleportEvent.getCreatureId())
                            .forceSetTransform(teleportEvent.getPos());
        }
        else {
            if (teleportEvent.getCreatureId() != null) {
                Creature creature = game.getCreature(teleportEvent.getCreatureId());

                creature.getParams().setAreaId(teleportEvent.getToAreaId());

                creature.getParams().setPos(teleportEvent.getPos());
                creature.getParams().setMovementCommandTargetPos(teleportEvent.getPos());

                if (getGamePhysics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    getGamePhysics().getCreatureBodies().get(teleportEvent.getCreatureId()).onRemove();
                    getGamePhysics().getCreatureBodies().remove(teleportEvent.getCreatureId());
                }

                if (!getGamePhysics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    CreatureBody creatureBody = CreatureBody.of(teleportEvent.getCreatureId());
                    creatureBody.init(game, teleportEvent.getToAreaId());
                    getGamePhysics().getCreatureBodies().put(teleportEvent.getCreatureId(), creatureBody);
                }

                creature.getParams().setJustTeleportedToGate(true);


            }
        }

    }
}
